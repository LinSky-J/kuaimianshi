package com.jinlin.kuaimianshibackend.controller;

import com.jinlin.kuaimianshibackend.common.BaseResponse;
import com.jinlin.kuaimianshibackend.common.DeleteRequest;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.common.ResultUtils;
import com.jinlin.kuaimianshibackend.exception.BusinessException;
import com.jinlin.kuaimianshibackend.model.dto.QuestionAddRequest;
import com.jinlin.kuaimianshibackend.model.dto.QuestionEditRequest;
import com.jinlin.kuaimianshibackend.model.dto.QuestionQueryRequest;
import com.jinlin.kuaimianshibackend.model.dto.QuestionUpdateRequest;
import com.jinlin.kuaimianshibackend.model.entity.Question;
import com.jinlin.kuaimianshibackend.model.vo.QuestionVO;
import com.jinlin.kuaimianshibackend.service.QuestionService;
import com.jinlin.kuaimianshibackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Question controller.
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * Add a question.
     *
     * @param addRequest add request
     * @param request http request
     * @return question id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest addRequest, HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "request body is null");
        }
        Question question = new Question();
        BeanUtils.copyProperties(addRequest, question);
        return ResultUtils.success(questionService.addQuestion(question, request));
    }

    /**
     * Delete a question.
     *
     * @param deleteRequest delete request
     * @param request http request
     * @return success flag
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id is required");
        }
        return ResultUtils.success(questionService.deleteQuestion(deleteRequest.getId(), request));
    }

    /**
     * Update a question by admin.
     *
     * @param updateRequest update request
     * @param request http request
     * @return success flag
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest updateRequest,
                                                HttpServletRequest request) {
        checkAdmin(request);
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "request body is null");
        }
        Question question = new Question();
        BeanUtils.copyProperties(updateRequest, question);
        return ResultUtils.success(questionService.updateQuestion(question));
    }

    /**
     * Edit a question.
     *
     * @param editRequest edit request
     * @param request http request
     * @return success flag
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest editRequest, HttpServletRequest request) {
        return ResultUtils.success(questionService.editQuestion(editRequest, request));
    }

    /**
     * Get question by id.
     *
     * @param id question id
     * @return question vo
     */
    @GetMapping("/get")
    public BaseResponse<QuestionVO> getQuestionById(@RequestParam("id") Long id) {
        Question question = questionService.getQuestionById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "question not found");
        }
        return ResultUtils.success(getQuestionVO(question));
    }

    /**
     * List all questions.
     *
     * @return question vo list
     */
    @GetMapping("/list")
    public BaseResponse<List<QuestionVO>> listQuestions() {
        return ResultUtils.success(getQuestionVOList(questionService.listQuestions()));
    }

    /**
     * Page questions by condition.
     *
     * @param queryRequest query request
     * @return paged question data
     */
    @PostMapping("/page")
    public BaseResponse<Map<String, Object>> pageQuestions(@RequestBody(required = false) QuestionQueryRequest queryRequest) {
        QuestionQueryRequest finalQuery = queryRequest == null ? new QuestionQueryRequest() : queryRequest;
        long current = finalQuery.getCurrent() == null ? 1L : finalQuery.getCurrent();
        long pageSize = finalQuery.getPageSize() == null ? 10L : finalQuery.getPageSize();
        List<Question> records = questionService.listQuestionsByPage(
                finalQuery.getTitle(),
                finalQuery.getContent(),
                finalQuery.getTags(),
                finalQuery.getUserId(),
                current,
                pageSize
        );
        long total = questionService.countQuestionsByCondition(
                finalQuery.getTitle(),
                finalQuery.getContent(),
                finalQuery.getTags(),
                finalQuery.getUserId()
        );
        Map<String, Object> result = new HashMap<>(4);
        result.put("records", getQuestionVOList(records));
        result.put("total", total);
        result.put("current", current);
        result.put("pageSize", pageSize);
        return ResultUtils.success(result);
    }

    private void checkAdmin(HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "no auth");
        }
    }

    private QuestionVO getQuestionVO(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        return questionVO;
    }

    private List<QuestionVO> getQuestionVOList(List<Question> questionList) {
        return questionList.stream().map(this::getQuestionVO).collect(Collectors.toList());
    }
}
