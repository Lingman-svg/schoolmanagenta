package com.school.handler;

/**
 * 自定义业务异常类
 */
public class BusinessException extends RuntimeException {

    private Integer code; // 可以添加错误码字段，如果需要的话

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
} 