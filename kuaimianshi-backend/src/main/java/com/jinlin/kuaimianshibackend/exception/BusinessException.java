package com.jinlin.kuaimianshibackend.exception;

import com.jinlin.kuaimianshibackend.common.ErrorCode;

/**
 * 自定义业务异常
 *
 * 用于在业务逻辑中抛出带有错误码的异常，
 * 便于全局异常处理器统一转换为前端可识别的响应结构。
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
