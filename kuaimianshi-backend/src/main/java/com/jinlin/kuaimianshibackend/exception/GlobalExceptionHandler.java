package com.jinlin.kuaimianshibackend.exception;

import com.jinlin.kuaimianshibackend.common.BaseResponse;
import com.jinlin.kuaimianshibackend.common.ErrorCode;
import com.jinlin.kuaimianshibackend.common.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.warn("business exception, code={}, message={}", e.getCode(), e.getMessage());
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> runtimeExceptionHandler(Exception e) {
        log.error("system exception", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "system error");
    }
}
