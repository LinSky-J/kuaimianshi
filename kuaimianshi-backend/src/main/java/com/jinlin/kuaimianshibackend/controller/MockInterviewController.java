package com.jinlin.kuaimianshibackend.controller;

import com.jinlin.kuaimianshibackend.common.BaseResponse;
import com.jinlin.kuaimianshibackend.common.DeleteRequest;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.common.ResultUtils;
import com.jinlin.kuaimianshibackend.exception.BusinessException;
import com.jinlin.kuaimianshibackend.model.dto.MockInterviewAddRequest;
import com.jinlin.kuaimianshibackend.model.dto.MockInterviewEditRequest;
import com.jinlin.kuaimianshibackend.model.dto.MockInterviewQueryRequest;
import com.jinlin.kuaimianshibackend.model.dto.MockInterviewUpdateRequest;
import com.jinlin.kuaimianshibackend.model.entity.MockInterview;
import com.jinlin.kuaimianshibackend.model.vo.MockInterviewVO;
import com.jinlin.kuaimianshibackend.service.MockInterviewService;
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
 * Mock interview controller.
 */
@RestController
@RequestMapping("/mock_interview")
public class MockInterviewController {

    @Resource
    private MockInterviewService mockInterviewService;

    @Resource
    private UserService userService;

    /**
     * Add a mock interview.
     *
     * @param addRequest add request
     * @param request http request
     * @return mock interview id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addMockInterview(@RequestBody MockInterviewAddRequest addRequest,
                                               HttpServletRequest request) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "request body is null");
        }
        MockInterview mockInterview = new MockInterview();
        BeanUtils.copyProperties(addRequest, mockInterview);
        return ResultUtils.success(mockInterviewService.addMockInterview(mockInterview, request));
    }

    /**
     * Delete a mock interview.
     *
     * @param deleteRequest delete request
     * @param request http request
     * @return success flag
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMockInterview(@RequestBody DeleteRequest deleteRequest,
                                                     HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id is required");
        }
        return ResultUtils.success(mockInterviewService.deleteMockInterview(deleteRequest.getId(), request));
    }

    /**
     * Update a mock interview by admin.
     *
     * @param updateRequest update request
     * @param request http request
     * @return success flag
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateMockInterview(@RequestBody MockInterviewUpdateRequest updateRequest,
                                                     HttpServletRequest request) {
        checkAdmin(request);
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "request body is null");
        }
        MockInterview mockInterview = new MockInterview();
        BeanUtils.copyProperties(updateRequest, mockInterview);
        return ResultUtils.success(mockInterviewService.updateMockInterview(mockInterview));
    }

    /**
     * Edit a mock interview.
     *
     * @param editRequest edit request
     * @param request http request
     * @return success flag
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editMockInterview(@RequestBody MockInterviewEditRequest editRequest,
                                                   HttpServletRequest request) {
        return ResultUtils.success(mockInterviewService.editMockInterview(editRequest, request));
    }

    /**
     * Get mock interview by id.
     *
     * @param id mock interview id
     * @param request http request
     * @return mock interview vo
     */
    @GetMapping("/get")
    public BaseResponse<MockInterviewVO> getMockInterviewById(@RequestParam("id") Long id,
                                                              HttpServletRequest request) {
        MockInterview mockInterview = mockInterviewService.getMockInterviewById(id, request);
        return ResultUtils.success(getMockInterviewVO(mockInterview));
    }

    /**
     * List mock interviews visible to current user.
     *
     * @param request http request
     * @return mock interview vo list
     */
    @GetMapping("/list")
    public BaseResponse<List<MockInterviewVO>> listMockInterviews(HttpServletRequest request) {
        return ResultUtils.success(getMockInterviewVOList(mockInterviewService.listMockInterviews(request)));
    }

    /**
     * Page mock interviews by condition.
     *
     * @param queryRequest query request
     * @param request http request
     * @return paged mock interview data
     */
    @PostMapping("/page")
    public BaseResponse<Map<String, Object>> pageMockInterviews(
            @RequestBody(required = false) MockInterviewQueryRequest queryRequest,
            HttpServletRequest request) {
        MockInterviewQueryRequest finalQuery = queryRequest == null ? new MockInterviewQueryRequest() : queryRequest;
        long current = finalQuery.getCurrent() == null ? 1L : finalQuery.getCurrent();
        long pageSize = finalQuery.getPageSize() == null ? 10L : finalQuery.getPageSize();
        List<MockInterview> records = mockInterviewService.listMockInterviewsByPage(finalQuery, request);
        long total = mockInterviewService.countMockInterviewsByCondition(finalQuery, request);
        Map<String, Object> result = new HashMap<>(4);
        result.put("records", getMockInterviewVOList(records));
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

    private MockInterviewVO getMockInterviewVO(MockInterview mockInterview) {
        if (mockInterview == null) {
            return null;
        }
        MockInterviewVO mockInterviewVO = new MockInterviewVO();
        BeanUtils.copyProperties(mockInterview, mockInterviewVO);
        return mockInterviewVO;
    }

    private List<MockInterviewVO> getMockInterviewVOList(List<MockInterview> mockInterviewList) {
        return mockInterviewList.stream().map(this::getMockInterviewVO).collect(Collectors.toList());
    }
}
