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
 * Question entity, mapped to the `question` table.
 */
@Data
@TableName("question")
public class Question implements Serializable {

    /**
     * Primary key.
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Question title.
     */
    private String title;

    /**
     * Question content.
     */
    private String content;

    /**
     * Tags in JSON array string format.
     */
    private String tags;

    /**
     * Recommended answer.
     */
    private String answer;

    /**
     * Creator user id.
     */
    private Long userId;

    /**
     * Edit time.
     */
    private Date editTime;

    /**
     * Create time.
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * Update time.
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * Logical delete flag.
     */
    @TableLogic
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
