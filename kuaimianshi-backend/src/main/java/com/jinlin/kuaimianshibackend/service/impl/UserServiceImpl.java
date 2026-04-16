package com.jinlin.kuaimianshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.exception.BusinessException;
import com.jinlin.kuaimianshibackend.mapper.UserMapper;
import com.jinlin.kuaimianshibackend.model.entity.User;
import com.jinlin.kuaimianshibackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String SALT = "kuaimianshi";

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean addUser(User user) {
        // 简单示例：这里可以扩展参数校验
        return userMapper.insert(user) > 0;
    }

    @Override
    public User register(String userAccount, String userPassword, String checkPassword) {
        if (!StringUtils.hasText(userAccount) || !StringUtils.hasText(userPassword) || !StringUtils.hasText(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于 4 位");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        if (userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于 6 位");
        }

        // 账号不能重复
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount, userAccount);
        long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 密码加盐加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserRole("user");

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败");
        }
        return user;
    }

    @Override
    public User login(String userAccount, String userPassword) {
        if (!StringUtils.hasText(userAccount) || !StringUtils.hasText(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, encryptPassword);

        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        // 使用 MyBatis-Plus 逻辑删除能力（依赖 User.isDelete 上的 @TableLogic）
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> listUsers() {
        // 查询全部未被逻辑删除的用户（@TableLogic 自动生效）
        return userMapper.selectList(null);
    }

    @Override
    public List<User> listUsersByPage(String userAccount, String userName, long current, long pageSize) {
        if (current <= 0) {
            current = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(userAccount)) {
            wrapper.like(User::getUserAccount, userAccount);
        }
        if (StringUtils.hasText(userName)) {
            wrapper.like(User::getUserName, userName);
        }

        Page<User> page = new Page<>(current, pageSize);
        userMapper.selectPage(page, wrapper);
        return page.getRecords();
    }

    @Override
    public long countUsersByCondition(String userAccount, String userName) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(userAccount)) {
            wrapper.like(User::getUserAccount, userAccount);
        }
        if (StringUtils.hasText(userName)) {
            wrapper.like(User::getUserName, userName);
        }
        return userMapper.selectCount(wrapper);
    }
}
