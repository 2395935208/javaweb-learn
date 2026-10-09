package com.example.seckill.common;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus status;

    public BusinessException(int status, String message) {
        this(HttpStatus.valueOf(status), message);
    }

    public BusinessException(HttpStatus status, String message) {
        super(message);
        if (!status.isError()) {
            throw new IllegalArgumentException("业务异常必须使用错误状态码");
        }
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
