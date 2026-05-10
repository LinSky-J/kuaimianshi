package com.jinlin.kuaimianshibackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Request for updating a mock interview.
 */
@Data
public class MockInterviewUpdateRequest implements Serializable {

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
     * Interview status: 0-pending, 1-in progress, 2-finished.
     */
    private Integer status;

    /**
     * Creator user id.
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
