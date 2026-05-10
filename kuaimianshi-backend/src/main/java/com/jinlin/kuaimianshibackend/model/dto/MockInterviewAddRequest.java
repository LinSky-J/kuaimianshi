package com.jinlin.kuaimianshibackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Request for creating a mock interview.
 */
@Data
public class MockInterviewAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}
