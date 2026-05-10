package com.jinlin.kuaimianshibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.exception.BusinessException;
import com.jinlin.kuaimianshibackend.mapper.MockInterviewMapper;
import com.jinlin.kuaimianshibackend.model.dto.MockInterviewEditRequest;
import com.jinlin.kuaimianshibackend.model.dto.MockInterviewQueryRequest;
import com.jinlin.kuaimianshibackend.model.entity.MockInterview;
import com.jinlin.kuaimianshibackend.model.entity.User;
import com.jinlin.kuaimianshibackend.service.MockInterviewService;
import com.jinlin.kuaimianshibackend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Mock interview service implementation.
 */
@Service
public class MockInterviewServiceImpl extends ServiceImpl<MockInterviewMapper, MockInterview>
        implements MockInterviewService {

    private static final long DEFAULT_CURRENT = 1L;

    private static final long DEFAULT_PAGE_SIZE = 10L;

    private static final long MAX_PAGE_SIZE = 100L;

    private static final int STATUS_PENDING = 0;

    private static final int STATUS_IN_PROGRESS = 1;

    private static final int STATUS_FINISHED = 2;

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addMockInterview(MockInterview mockInterview, HttpServletRequest request) {
        if (mockInterview == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "mockInterview is null");
        }
        User loginUser = userService.getLoginUser(request);
        fillMockInterviewFields(mockInterview, true);
        mockInterview.setId(null);
        mockInterview.setUserId(loginUser.getId());
        mockInterview.setIsDelete(null);
        if (!this.save(mockInterview)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "add mock interview failed");
        }
        return mockInterview.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMockInterview(Long id, HttpServletRequest request) {
        validateId(id);
        MockInterview oldMockInterview = getRequiredMockInterview(id);
        checkOwnerOrAdmin(oldMockInterview, request);
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMockInterview(MockInterview mockInterview) {
        if (mockInterview == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "mockInterview is null");
        }
        validateId(mockInterview.getId());
        getRequiredMockInterview(mockInterview.getId());
        fillMockInterviewFields(mockInterview, false);
        mockInterview.setCreateTime(null);
        mockInterview.setIsDelete(null);
        return this.updateById(mockInterview);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean editMockInterview(MockInterviewEditRequest editRequest, HttpServletRequest request) {
        if (editRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "editRequest is null");
        }
        validateId(editRequest.getId());
        MockInterview oldMockInterview = getRequiredMockInterview(editRequest.getId());
        checkOwnerOrAdmin(oldMockInterview, request);

        MockInterview updateMockInterview = new MockInterview();
        updateMockInterview.setId(editRequest.getId());
        updateMockInterview.setWorkExperience(normalize(editRequest.getWorkExperience()));
        updateMockInterview.setJobPosition(normalize(editRequest.getJobPosition()));
        updateMockInterview.setDifficulty(normalize(editRequest.getDifficulty()));
        updateMockInterview.setMessages(normalize(editRequest.getMessages()));
        updateMockInterview.setStatus(editRequest.getStatus());
        validateStatus(updateMockInterview.getStatus(), false);
        return this.updateById(updateMockInterview);
    }

    @Override
    public MockInterview getMockInterviewById(Long id, HttpServletRequest request) {
        validateId(id);
        MockInterview mockInterview = getRequiredMockInterview(id);
        checkOwnerOrAdmin(mockInterview, request);
        return mockInterview;
    }

    @Override
    public List<MockInterview> listMockInterviews(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        LambdaQueryWrapper<MockInterview> queryWrapper = new LambdaQueryWrapper<>();
        if (!userService.isAdmin(request)) {
            queryWrapper.eq(MockInterview::getUserId, loginUser.getId());
        }
        queryWrapper.orderByDesc(MockInterview::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<MockInterview> listMockInterviewsByPage(MockInterviewQueryRequest queryRequest,
                                                        HttpServletRequest request) {
        MockInterviewQueryRequest finalQuery = queryRequest == null ? new MockInterviewQueryRequest() : queryRequest;
        long current = finalQuery.getCurrent() == null ? DEFAULT_CURRENT : finalQuery.getCurrent();
        long pageSize = finalQuery.getPageSize() == null ? DEFAULT_PAGE_SIZE : finalQuery.getPageSize();
        current = current <= 0 ? DEFAULT_CURRENT : current;
        pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);

        LambdaQueryWrapper<MockInterview> queryWrapper = buildMockInterviewQueryWrapper(
                finalQuery, request);
        applySort(queryWrapper, finalQuery.getSortField(), finalQuery.getSortOrder());
        Page<MockInterview> page = new Page<>(current, pageSize);
        this.page(page, queryWrapper);
        return page.getRecords();
    }

    @Override
    public long countMockInterviewsByCondition(MockInterviewQueryRequest queryRequest, HttpServletRequest request) {
        MockInterviewQueryRequest finalQuery = queryRequest == null ? new MockInterviewQueryRequest() : queryRequest;
        return this.count(buildMockInterviewQueryWrapper(finalQuery, request));
    }

    private LambdaQueryWrapper<MockInterview> buildMockInterviewQueryWrapper(MockInterviewQueryRequest queryRequest,
                                                                             HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        LambdaQueryWrapper<MockInterview> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryRequest.getWorkExperience())) {
            queryWrapper.like(MockInterview::getWorkExperience, normalize(queryRequest.getWorkExperience()));
        }
        if (StringUtils.hasText(queryRequest.getJobPosition())) {
            queryWrapper.like(MockInterview::getJobPosition, normalize(queryRequest.getJobPosition()));
        }
        if (StringUtils.hasText(queryRequest.getDifficulty())) {
            queryWrapper.eq(MockInterview::getDifficulty, normalize(queryRequest.getDifficulty()));
        }
        if (queryRequest.getStatus() != null) {
            validateStatus(queryRequest.getStatus(), false);
            queryWrapper.eq(MockInterview::getStatus, queryRequest.getStatus());
        }
        Long effectiveUserId = userService.isAdmin(request) ? queryRequest.getUserId() : loginUser.getId();
        if (effectiveUserId != null && effectiveUserId > 0) {
            queryWrapper.eq(MockInterview::getUserId, effectiveUserId);
        }
        return queryWrapper;
    }

    private void applySort(LambdaQueryWrapper<MockInterview> queryWrapper, String sortField, String sortOrder) {
        boolean isAsc = "asc".equalsIgnoreCase(sortOrder) || "ascend".equalsIgnoreCase(sortOrder);
        if (!StringUtils.hasText(sortField)) {
            queryWrapper.orderByDesc(MockInterview::getCreateTime);
            return;
        }
        String normalizedSortField = sortField.trim();
        switch (normalizedSortField) {
            case "id":
                if (isAsc) {
                    queryWrapper.orderByAsc(MockInterview::getId);
                } else {
                    queryWrapper.orderByDesc(MockInterview::getId);
                }
                break;
            case "status":
                if (isAsc) {
                    queryWrapper.orderByAsc(MockInterview::getStatus);
                } else {
                    queryWrapper.orderByDesc(MockInterview::getStatus);
                }
                break;
            case "createTime":
                if (isAsc) {
                    queryWrapper.orderByAsc(MockInterview::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(MockInterview::getCreateTime);
                }
                break;
            case "updateTime":
                if (isAsc) {
                    queryWrapper.orderByAsc(MockInterview::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(MockInterview::getUpdateTime);
                }
                break;
            default:
                queryWrapper.orderByDesc(MockInterview::getCreateTime);
                break;
        }
    }

    private MockInterview getRequiredMockInterview(Long id) {
        MockInterview mockInterview = this.getById(id);
        if (mockInterview == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "mock interview not found");
        }
        return mockInterview;
    }

    private void fillMockInterviewFields(MockInterview mockInterview, boolean setDefaultStatus) {
        mockInterview.setWorkExperience(normalize(mockInterview.getWorkExperience()));
        mockInterview.setJobPosition(normalize(mockInterview.getJobPosition()));
        mockInterview.setDifficulty(normalize(mockInterview.getDifficulty()));
        mockInterview.setMessages(normalize(mockInterview.getMessages()));
        validateRequiredText(mockInterview.getWorkExperience(), "workExperience");
        validateRequiredText(mockInterview.getJobPosition(), "jobPosition");
        validateRequiredText(mockInterview.getDifficulty(), "difficulty");
        if (setDefaultStatus && mockInterview.getStatus() == null) {
            mockInterview.setStatus(STATUS_PENDING);
        }
        validateStatus(mockInterview.getStatus(), true);
    }

    private void checkOwnerOrAdmin(MockInterview mockInterview, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (!mockInterview.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "no auth");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id is invalid");
        }
    }

    private void validateRequiredText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + " is required");
        }
    }

    private void validateStatus(Integer status, boolean required) {
        if (status == null) {
            if (required) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "status is required");
            }
            return;
        }
        if (status < STATUS_PENDING || status > STATUS_FINISHED) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "status is invalid");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
