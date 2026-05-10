package com.jinlin.kuaimianshibackend.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体，对应数据库中的 `user` 表。
 */
@Data
@TableName("user")
public class User implements Serializable {

    /**
     * 用户 id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 加密后的密码
     */
    private String userPassword;

    /**
     * 微信开放平台 unionId
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
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年级
     */
    private String grade;

    /**
     * 工作经验
     */
    private String workExperience;

    /**
     * 擅长方向
     */
    private String expertiseDirection;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

}
