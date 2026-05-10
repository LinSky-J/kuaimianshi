package com.jinlin.kuaimianshibackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Request for querying questions with pagination.
 */
@Data
public class QuestionQueryRequest implements Serializable {

    /**
     * Question id.
     */
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
     * Current page number.
     */
    private Long current = 1L;

    /**
     * Page size.
     */
    private Long pageSize = 10L;

    private static final long serialVersionUID = 1L;
}
