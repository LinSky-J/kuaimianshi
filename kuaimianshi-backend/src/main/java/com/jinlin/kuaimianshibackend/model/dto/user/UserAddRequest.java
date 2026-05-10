package com.jinlin.kuaimianshibackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员新增用户请求。
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}
