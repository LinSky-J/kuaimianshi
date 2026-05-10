package com.jinlin.kuaimianshibackend.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * sql工具
 */
public class SqlUtils {
    public static boolean isValidSortFiled(String sortFiled) {
        /**
         * 校验排序字段时候合法（防止SQL注入）
         */
        if (StringUtils.isBlank(sortFiled)) {
            return false;
        }
        return StringUtils.containsAny(sortFiled, "=", "(", ")", " ");
    }
}
