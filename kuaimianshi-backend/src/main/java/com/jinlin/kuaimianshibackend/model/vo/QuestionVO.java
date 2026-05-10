package com.jinlin.kuaimianshibackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Question view object.
 */
@Data
public class QuestionVO implements Serializable {

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
     * Edit time.
     */
    private Date editTime;

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
