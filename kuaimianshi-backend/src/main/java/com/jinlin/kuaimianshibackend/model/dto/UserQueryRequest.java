package com.jinlin.kuaimianshibackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求（用于分页/条件查询）
 */
@Data
public class UserQueryRequest implements Serializable {

    /**
     * 用户账号（模糊匹配）
     */
    private String userAccount;

    /**
     * 用户昵称（模糊匹配）
     */
    private String userName;

    /**
     * 当前页号，从 1 开始
     */
    private long current = 1L;

    /**
     * 每页大小
     */
    private long pageSize = 10L;

    private static final long serialVersionUID = 1L;
}
