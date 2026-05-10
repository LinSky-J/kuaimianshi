package com.jinlin.kuaimianshibackend.model.dto;

import com.jinlin.kuaimianshibackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * Request for querying mock interviews with pagination.
 */
@Data
public class MockInterviewQueryRequest extends PageRequest implements Serializable {

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
     * Interview status.
     */
    private Integer status;

    /**
     * Creator user id.
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
