package com.jinlin.kuaimianshibackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinlin.kuaimianshibackend.common.BaseResponse;
import com.jinlin.kuaimianshibackend.common.DeleteRequest;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.common.ResultUtils;
import com.jinlin.kuaimianshibackend.config.WxOpenConfig;
import com.jinlin.kuaimianshibackend.constant.UserConstant;
import com.jinlin.kuaimianshibackend.exception.BusinessException;
import com.jinlin.kuaimianshibackend.exception.ThrowUtils;
import com.jinlin.kuaimianshibackend.model.dto.user.UserAddRequest;
import com.jinlin.kuaimianshibackend.model.dto.user.UserEditRequest;
import com.jinlin.kuaimianshibackend.model.dto.user.UserLoginRequest;
import com.jinlin.kuaimianshibackend.model.dto.user.UserQueryRequest;
import com.jinlin.kuaimianshibackend.model.dto.user.UserRegisterRequest;
import com.jinlin.kuaimianshibackend.model.dto.user.UserUpdateMyRequest;
import com.jinlin.kuaimianshibackend.model.dto.user.UserUpdateRequest;
import com.jinlin.kuaimianshibackend.model.entity.User;
import com.jinlin.kuaimianshibackend.model.vo.LoginUserVO;
import com.jinlin.kuaimianshibackend.model.vo.UserVO;
import com.jinlin.kuaimianshibackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.Scope;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jinlin.kuaimianshibackend.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口控制器。
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;
    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户通过微信登
     * @param request
     * @param response
     * @param code
     * @return
     */
    @PostMapping("/login/wx_open")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestParam("code") String code ){
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxMpService = wxOpenConfig.getWxMpService();
            accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, code);
            String openid = userInfo.getOpenid();
            String unionId = userInfo.getUnionId();
            if(StringUtils.isAnyBlank(openid,unionId)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"登录失败系统错误");
            }
            LoginUserVO loginUserVO = userService.userLoginByMpOpen(userInfo, request);
            return ResultUtils.success(loginUserVO);
        } catch (WxErrorException e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }
    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogOut(HttpServletRequest request) {
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result  = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    /**
     * 添加用户（管理员）
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if(userAddRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest,user);
        //默认密码：12345678
        String defaultPassword ="12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户（管理员）
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request) {
        if(deleteRequest==null || deleteRequest.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     *更新用户（管理员）
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 编辑用户信息（仅限用户和管理员）
     * @param userEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editUser(@RequestBody UserEditRequest userEditRequest,HttpServletRequest request) {
        if (userEditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 构建更新对象
        User user = new User();
        BeanUtils.copyProperties(userEditRequest, user);
        // 如果是用户编辑自己，强制设置 ID 为当前用户 ID（防止越权修改）
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据ID获取用户（管理员）
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id,HttpServletRequest request) {
        if(id<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user==null,ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }


    /**
     * 根据ID，获取包装类
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id,HttpServletRequest request) {
        if(id<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user==null,ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                   HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                       HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
                                              HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 添加用户签到记录
     *
     * @param request
     * @return 当前是否已签到成功
     */
    @PostMapping("/add/sign_in")
    public BaseResponse<Boolean> addUserSignIn(HttpServletRequest request) {
        // 必须要登录才能签到
        User loginUser = userService.getLoginUser(request);
        boolean result = userService.addUserSignIn(loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 获取用户签到记录
     *
     * @param year    年份（为空表示当前年份）
     * @param request
     * @return 签到记录映射
     */
    @GetMapping("/get/sign_in")
    public BaseResponse<List<Integer>> getUserSignInRecord(Integer year, HttpServletRequest request) {
        // 必须要登录才能获取
        User loginUser = userService.getLoginUser(request);
        List<Integer> userSignInRecord = userService.getUserSignInRecord(loginUser.getId(), year);
        return ResultUtils.success(userSignInRecord);
    }
}
