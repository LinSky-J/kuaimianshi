package com.jinlin.kuaimianshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.exception.BusinessException;
import com.jinlin.kuaimianshibackend.mapper.QuestionMapper;
import com.jinlin.kuaimianshibackend.model.dto.QuestionEditRequest;
import com.jinlin.kuaimianshibackend.model.entity.Question;
import com.jinlin.kuaimianshibackend.model.entity.User;
import com.jinlin.kuaimianshibackend.service.QuestionService;
import com.jinlin.kuaimianshibackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Question service implementation.
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private static final long DEFAULT_CURRENT = 1L;

    private static final long DEFAULT_PAGE_SIZE = 10L;

    private static final long MAX_PAGE_SIZE = 100L;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addQuestion(Question question, HttpServletRequest request) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "question is null");
        }
        User loginUser = userService.getLoginUser(request);
        fillQuestionFields(question);
        question.setId(null);
        question.setUserId(loginUser.getId());
        question.setIsDelete(null);
        if (!this.save(question)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "add question failed");
        }
        return question.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestion(Long id, HttpServletRequest request) {
        validateId(id);
        Question oldQuestion = getRequiredQuestion(id);
        User loginUser = userService.getLoginUser(request);
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "no auth");
        }
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuestion(Question question) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "question is null");
        }
        validateId(question.getId());
        getRequiredQuestion(question.getId());
        fillQuestionFields(question);
        question.setCreateTime(null);
        question.setIsDelete(null);
        return this.updateById(question);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editQuestion(QuestionEditRequest editRequest, HttpServletRequest request) {
        if (editRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "editRequest is null");
        }
        validateId(editRequest.getId());
        Question oldQuestion = getRequiredQuestion(editRequest.getId());
        User loginUser = userService.getLoginUser(request);
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "no auth");
        }
        Question updateQuestion = new Question();
        updateQuestion.setId(editRequest.getId());
        updateQuestion.setTitle(normalize(editRequest.getTitle()));
        updateQuestion.setContent(normalize(editRequest.getContent()));
        updateQuestion.setTags(normalize(editRequest.getTags()));
        updateQuestion.setAnswer(normalize(editRequest.getAnswer()));
        updateQuestion.setEditTime(new Date());
        return this.updateById(updateQuestion);
    }

    @Override
    public Question getQuestionById(Long id) {
        validateId(id);
        return this.getById(id);
    }

    @Override
    public List<Question> listQuestions() {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Question::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<Question> listQuestionsByPage(String title, String content, String tags, Long userId,
                                              long current, long pageSize) {
        current = current <= 0 ? DEFAULT_CURRENT : current;
        pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);

        LambdaQueryWrapper<Question> queryWrapper = buildQuestionQueryWrapper(title, content, tags, userId);
        queryWrapper.orderByDesc(Question::getCreateTime);
        Page<Question> page = new Page<>(current, pageSize);
        this.page(page, queryWrapper);
        return page.getRecords();
    }

    @Override
    public long countQuestionsByCondition(String title, String content, String tags, Long userId) {
        return this.count(buildQuestionQueryWrapper(title, content, tags, userId));
    }

    private LambdaQueryWrapper<Question> buildQuestionQueryWrapper(String title, String content, String tags,
                                                                   Long userId) {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            queryWrapper.like(Question::getTitle, normalize(title));
        }
        if (StringUtils.hasText(content)) {
            queryWrapper.like(Question::getContent, normalize(content));
        }
        if (StringUtils.hasText(tags)) {
            queryWrapper.like(Question::getTags, normalize(tags));
        }
        if (userId != null && userId > 0) {
            queryWrapper.eq(Question::getUserId, userId);
        }
        return queryWrapper;
    }

    private Question getRequiredQuestion(Long id) {
        Question question = this.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "question not found");
        }
        return question;
    }

    private void fillQuestionFields(Question question) {
        question.setTitle(normalize(question.getTitle()));
        question.setContent(normalize(question.getContent()));
        question.setTags(normalize(question.getTags()));
        question.setAnswer(normalize(question.getAnswer()));
        question.setEditTime(new Date());
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id is invalid");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
