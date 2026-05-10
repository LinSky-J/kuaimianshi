package com.jinlin.kuaimianshibackend.common;

import com.jinlin.kuaimianshibackend.constant.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求
 */
@Data
public class PageRequest {

    /**
     * Current page number.
     */
    private int current = 1;

    /**
     * Page size.
     */
    private int pageSize = 10;

    /**
     * Sort field.
     */
    private String sortField;

    /**
     * 排序顺序/默认升序
     */
    private String sortOrder= CommonConstant.SORT_ORDER_ASC;
}
