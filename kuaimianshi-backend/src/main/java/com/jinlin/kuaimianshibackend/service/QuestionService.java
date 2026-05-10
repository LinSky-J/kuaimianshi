package com.jinlin.kuaimianshibackend.service;

import com.jinlin.kuaimianshibackend.model.dto.QuestionEditRequest;
import com.jinlin.kuaimianshibackend.model.entity.Question;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Question service.
 */
public interface QuestionService {

    /**
     * Add a question.
     *
     * @param question question entity
     * @param request http request
     * @return question id
     */
    long addQuestion(Question question, HttpServletRequest request);

    /**
     * Delete a question.
     *
     * @param id question id
     * @param request http request
     * @return success flag
     */
    boolean deleteQuestion(Long id, HttpServletRequest request);

    /**
     * Update a question by admin.
     *
     * @param question question entity
     * @return success flag
     */
    boolean updateQuestion(Question question);

    /**
     * Edit a question by owner or admin.
     *
     * @param editRequest edit request
     * @param request http request
     * @return success flag
     */
    boolean editQuestion(QuestionEditRequest editRequest, HttpServletRequest request);

    /**
     * Get question by id.
     *
     * @param id question id
     * @return question entity
     */
    Question getQuestionById(Long id);

    /**
     * List all questions.
     *
     * @return question list
     */
    List<Question> listQuestions();

    /**
     * List questions by page.
     *
     * @param title title
     * @param content content
     * @param tags tags
     * @param userId user id
     * @param current current page
     * @param pageSize page size
     * @return question list
     */
    List<Question> listQuestionsByPage(String title, String content, String tags, Long userId, long current, long pageSize);

    /**
     * Count questions by condition.
     *
     * @param title title
     * @param content content
     * @param tags tags
     * @param userId user id
     * @return total count
     */
    long countQuestionsByCondition(String title, String content, String tags, Long userId);
}
