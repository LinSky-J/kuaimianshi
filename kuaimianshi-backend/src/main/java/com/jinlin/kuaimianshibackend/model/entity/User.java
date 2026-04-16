package com.jinlin.kuaimianshibackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体，对应数据库表 user
 */
@Data
@TableName("user")
public class User implements Serializable {

    /**
     * 主键 id，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码（建议后续存加密后的密文）
     */
    private String userPassword;

    /**
     * 微信开放平台 id
     */
    private String unionId;

    /**
     * 公众号 openId
     */
    private String mpOpenId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user / admin / ban
     */
    private String userRole;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除（0 否，1 是）—— MyBatis-Plus 逻辑删除字段
     */
    @TableLogic
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
