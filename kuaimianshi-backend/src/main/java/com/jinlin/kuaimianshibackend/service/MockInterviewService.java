package com.jinlin.kuaimianshibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinlin.kuaimianshibackend.model.dto.MockInterviewEditRequest;
import com.jinlin.kuaimianshibackend.model.dto.MockInterviewQueryRequest;
import com.jinlin.kuaimianshibackend.model.entity.MockInterview;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Mock interview service.
 */
public interface MockInterviewService extends IService<MockInterview> {

    /**
     * Add a mock interview.
     *
     * @param mockInterview mock interview entity
     * @param request http request
     * @return mock interview id
     */
    long addMockInterview(MockInterview mockInterview, HttpServletRequest request);

    /**
     * Delete a mock interview.
     *
     * @param id mock interview id
     * @param request http request
     * @return success flag
     */
    boolean deleteMockInterview(Long id, HttpServletRequest request);

    /**
     * Update a mock interview by admin.
     *
     * @param mockInterview mock interview entity
     * @return success flag
     */
    boolean updateMockInterview(MockInterview mockInterview);

    /**
     * Edit a mock interview by owner or admin.
     *
     * @param editRequest edit request
     * @param request http request
     * @return success flag
     */
    boolean editMockInterview(MockInterviewEditRequest editRequest, HttpServletRequest request);

    /**
     * Get mock interview by id.
     *
     * @param id mock interview id
     * @param request http request
     * @return mock interview entity
     */
    MockInterview getMockInterviewById(Long id, HttpServletRequest request);

    /**
     * List mock interviews visible to current user.
     *
     * @param request http request
     * @return mock interview list
     */
    List<MockInterview> listMockInterviews(HttpServletRequest request);

    /**
     * List mock interviews by page.
     *
     * @param queryRequest query request
     * @param request http request
     * @return mock interview list
     */
    List<MockInterview> listMockInterviewsByPage(MockInterviewQueryRequest queryRequest,
                                                 HttpServletRequest request);

    /**
     * Count mock interviews by condition.
     *
     * @param queryRequest query request
     * @param request http request
     * @return total count
     */
    long countMockInterviewsByCondition(MockInterviewQueryRequest queryRequest, HttpServletRequest request);
}
