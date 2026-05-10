package com.jinlin.kuaimianshibackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.constant.CommonConstant;
import com.jinlin.kuaimianshibackend.constant.RedisConstant;
import com.jinlin.kuaimianshibackend.constant.UserConstant;
import com.jinlin.kuaimianshibackend.enums.UserRoleEnum;
import com.jinlin.kuaimianshibackend.exception.BusinessException;
import com.jinlin.kuaimianshibackend.mapper.UserMapper;
import com.jinlin.kuaimianshibackend.model.dto.user.UserEditRequest;
import com.jinlin.kuaimianshibackend.model.dto.user.UserQueryRequest;
import com.jinlin.kuaimianshibackend.model.dto.user.UserUpdateMyRequest;
import com.jinlin.kuaimianshibackend.model.entity.User;
import com.jinlin.kuaimianshibackend.model.vo.LoginUserVO;
import com.jinlin.kuaimianshibackend.model.vo.UserVO;
import com.jinlin.kuaimianshibackend.service.UserService;
import com.jinlin.kuaimianshibackend.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.tomcat.util.buf.ByteChunk;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jinlin.kuaimianshibackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现。
 *
 * <p>该类使用 MyBatis-Plus 的 {@link ServiceImpl} 提供通用 CRUD 能力，
 * 复杂条件查询通过 LambdaQueryWrapper 构造，避免手写 SQL。</p>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private RedissonClient redissonClient;
    /**
     * 盐值
     */
    public static final String SALT ="jinlin";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //校验
        if(StringUtils.isAllBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号小于4位");
        }
        if(checkPassword.length()<8 || checkPassword.length()>8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if(!checkPassword.equals(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不同");
        }
        synchronized (userAccount.intern()){
            //查询数据库中是否用相同的账户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account",userAccount);
            Long count = this.baseMapper.selectCount(queryWrapper);
            if(count>0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户已注册");
            }
            //2.加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            //3.插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if(!saveResult){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库出错，注册失败");
            }
            return user.getId();
        }
    }
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验参数
        if(StringUtils.isAllBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度小于4");
        }
        if(userPassword.length()<8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码小于8");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        queryWrapper.eq("encryptPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if(user==null){
            log.info("user login failed, userAccount cannot match userPasswor");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或密码错误");
        }
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        return getLoginUserVO(user);
    }

    /**
     * 微信用户登录
     *
     * @param wxOAuth2UserInfo 从微信获取的用户信息
     * @param request
     * @return
     */
    @Override
    public LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String openid = wxOAuth2UserInfo.getOpenid();
        synchronized (unionId.intern()){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionid",unionId);
            User user = this.getOne(queryWrapper);
            if(user!=null || UserRoleEnum.BAN.getValue().equals(user.getUserRole())){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"改用户已被封，禁止登录");
            }
            //用户不存在，则创建
            if(user==null){
                user = new User();
                user.setMpOpenId(openid);
                user.setUnionId(unionId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean save = this.save(user);
                if(!save){
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR,"登录失败");
                }
            }
            //记住用户的登录状态
            request.getSession().setAttribute(USER_LOGIN_STATE,user);
            return getLoginUserVO(user);
        }
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        //先判断是否已经登录
        Object loginUserId = StpUtil.getLoginIdDefaultNull();
        if(loginUserId==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User currentUser = this.getById((String) loginUserId);
        if(currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 获取当前用户（允许未登录）
     * @param request
     * @return
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        //先判断当前是否已登录
        Object loginUserId = StpUtil.getLoginIdDefaultNull();
        if(loginUserId==null){
            return null;
        }
        return this.getById((String) loginUserId);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //只能管理员进行查询
        Object userObj = StpUtil.getSession().get(USER_LOGIN_STATE);
        User user=(User)userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user!=null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        StpUtil.checkLogin();
        //移除登录态
        StpUtil.logout();
        /*if(request.getSession().getAttribute(USER_LOGIN_STATE)==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"未登录");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);*/
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user==null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        //校验
        if(user==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.isValidSortFiled(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 添加用户签到记录
     * @param userId
     * @return 当前用户是否已经签到成功
     */
    @Override
    public boolean addUserSignIn(long userId) {
        LocalDate date = LocalDate.now();
        String key = RedisConstant.getUserSignInRedisKey(date.getYear(),userId);
        // 获取Redis 的 BitMap
        RBitSet signInBitSet = redissonClient.getBitSet(key);
        //获取当前日期是一年中的第几天，作为偏移量（从1开始计数）
        int offset = date.getDayOfYear();
        //查询当天有没有签到
        if(signInBitSet.get(offset)){
            //如果没有签到，则设置
            signInBitSet.set(offset);
        }
        //当天已签到
        return  true;
    }

    /**
     * 查询用户某个年份的签到记录
     * @param userId  用户ID
     * @param year    年份
     * @return  签到记录映射
     */
    @Override
    public List<Integer> getUserSignInRecord(long userId, Integer year) {
        //年份为null则使用当前年份
        if(year==null){
            LocalDate date = LocalDate.now();
            year = date.getYear();
        }
        String key = RedisConstant.getUserSignInRedisKey(year, userId);
        //获取RedisBitMap
        RBitSet signInBitSet = redissonClient.getBitSet(key);
        //加载BitSet到内存中，避免后续读取时发送多次请求
        BitSet bitSet = signInBitSet.asBitSet();
        //统计签到日期
        List<Integer> dayList = new ArrayList<>();
        //从索引 0 开始查找下一个被设置为 1 的位
        int index = bitSet.nextSetBit(0);
        while (index >= 0) {
            dayList.add(index);
            // 继续查找下一个被设置为 1 的位
            index = bitSet.nextSetBit(index + 1);
        }
        return dayList;
    }
}
