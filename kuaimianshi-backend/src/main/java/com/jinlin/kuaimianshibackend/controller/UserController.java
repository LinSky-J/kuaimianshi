package com.jinlin.kuaimianshibackend.controller;

import com.jinlin.kuaimianshibackend.common.BaseResponse;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.common.ResultUtils;
import com.jinlin.kuaimianshibackend.model.dto.*;
import com.jinlin.kuaimianshibackend.model.entity.User;
import com.jinlin.kuaimianshibackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户相关接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<User> register(@RequestBody UserRegisterRequest registerRequest) {
        User user = userService.register(registerRequest.getUserAccount(),
                registerRequest.getUserPassword(), registerRequest.getCheckPassword());
        return ResultUtils.success(user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody UserLoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUserAccount(), loginRequest.getUserPassword());
        return ResultUtils.success(user);
    }

    /**
     * 新增用户（后台管理）
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addUser(@RequestBody UserAddRequest addRequest) {
        User user = new User();
        BeanUtils.copyProperties(addRequest, user);
        boolean result = userService.addUser(user);
        return ResultUtils.success(result);
    }

    /**
     * 删除用户（逻辑删除）
     */
    @PostMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteUser(@PathVariable Long id) {
        boolean result = userService.deleteUser(id);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest updateRequest) {
        User user = new User();
        BeanUtils.copyProperties(updateRequest, user);
        boolean result = userService.updateUser(user);
        if (!result) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户详情
     */
    @GetMapping("/get/{id}")
    public BaseResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        return ResultUtils.success(user);
    }

    /**
     * 获取所有用户列表（不分页）
     */
    @GetMapping("/list")
    public BaseResponse<List<User>> listUsers() {
        List<User> list = userService.listUsers();
        return ResultUtils.success(list);
    }

    /**
     * 按条件分页查询用户列表
     */
    @PostMapping("/page")
    public BaseResponse<Map<String, Object>> pageUsers(@RequestBody UserQueryRequest queryRequest) {
        String userAccount = queryRequest.getUserAccount();
        String userName = queryRequest.getUserName();
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();

        List<User> records = userService.listUsersByPage(userAccount, userName, current, pageSize);
        long total = userService.countUsersByCondition(userAccount, userName);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("current", current);
        result.put("pageSize", pageSize);

        return ResultUtils.success(result);
    }
}
