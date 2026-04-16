package com.jinlin.kuaimianshibackend.service;

import com.jinlin.kuaimianshibackend.model.entity.User;

import java.util.List;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 新增用户（后台管理用）
     */
    boolean addUser(User user);

    /**
     * 用户注册
     */
    User register(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     */
    User login(String userAccount, String userPassword);

    /**
     * 根据 id 删除用户（逻辑删除）
     */
    boolean deleteUser(Long id);

    /**
     * 更新用户
     */
    boolean updateUser(User user);

    /**
     * 根据 id 获取用户
     */
    User getUserById(Long id);

    /**
     * 查询所有用户
     */
    List<User> listUsers();

    /**
     * 按条件分页查询用户
     *
     * @param userAccount 账号（可选）
     * @param userName    昵称（可选）
     * @param current     当前页号，从 1 开始
     * @param pageSize    每页大小
     */
    List<User> listUsersByPage(String userAccount, String userName, long current, long pageSize);

    /**
     * 统计符合条件的用户数量
     */
    long countUsersByCondition(String userAccount, String userName);
}
