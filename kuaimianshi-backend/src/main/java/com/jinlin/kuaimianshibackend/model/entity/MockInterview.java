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
 * Mock interview entity, mapped to the {@code mock_interview} table.
 */
@Data
@TableName("mock_interview")
public class MockInterview implements Serializable {

    /**
     * Primary key.
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Work experience.
     */
    private String workExperience;

    /**
     * Job position.
     */
    private String jobPosition;

    /**
     * Interview difficulty.
     */
    private String difficulty;

    /**
     * Message list in JSON string format.
     */
    private String messages;

    /**
     * Interview status: 0-pending, 1-in progress, 2-finished.
     */
    private Integer status;

    /**
     * Creator user id.
     */
    private Long userId;

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
