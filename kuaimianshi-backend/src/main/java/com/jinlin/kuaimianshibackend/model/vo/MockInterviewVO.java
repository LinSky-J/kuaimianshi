package com.jinlin.kuaimianshibackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Mock interview view object.
 */
@Data
public class MockInterviewVO implements Serializable {

    /**
     * Mock interview id.
     */
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
     * Interview status.
     */
    private Integer status;

    /**
     * Creator user id.
     */
    private Long userId;

    /**
     * Create time.
     */
    private Date createTime;

    /**
     * Update time.
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
