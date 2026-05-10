package com.jinlin.kuaimianshibackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Request for editing a question.
 */
@Data
public class QuestionEditRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}
