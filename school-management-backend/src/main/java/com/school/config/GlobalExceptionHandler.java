package com.school.config; // 放置在 config 包下

import com.school.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理所有未捕获的 Exception 异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 返回 500 状态码
    public R<Void> handleException(Exception e) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误，请联系管理员");
    }

    /**
     * 处理自定义业务异常 (如果定义了 BusinessException)
     * 需要创建一个继承自 RuntimeException 的 BusinessException 类
     *
     * @ExceptionHandler(BusinessException.class)
     * public R<Void> handleBusinessException(BusinessException e) {
     *     log.warn("Business exception: {}", e.getMessage());
     *     return R.fail(e.getCode() != null ? e.getCode() : HttpStatus.BAD_REQUEST.value(), e.getMessage());
     * }
     */

    /**
     * 处理 @Valid 注解校验失败的异常 (通常用于 @RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回 400 状态码
    public R<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed (RequestBody): {}", errorMsg);
        return R.fail(HttpStatus.BAD_REQUEST.value(), "参数校验失败: " + errorMsg, errorMsg);
    }

    /**
     * 处理 @Validated 注解校验失败的异常 (通常用于方法参数或 Form 表单绑定)
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回 400 状态码
    public R<String> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed (Form/Param): {}", errorMsg);
        return R.fail(HttpStatus.BAD_REQUEST.value(), "参数校验失败: " + errorMsg, errorMsg);
    }

    /**
     * 处理请求方法不支持的异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED) // 返回 405 状态码
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP method not supported: {}", e.getMessage());
        return R.fail(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 处理 404 Not Found 异常 (需要 spring.mvc.throw-exception-if-no-handler-found=true 和 spring.web.resources.add-mappings=false 配置)
     * 注意：默认情况下 Spring Boot 可能不会抛出此异常，而是返回默认的 404 页面。
     * 如果需要全局捕获 404，需要在 application.yml 中配置：
     * spring:
     *   mvc:
     *     throw-exception-if-no-handler-found: true
     *   web:
     *     resources:
     *       add-mappings: false # 禁用默认的静态资源映射，否则静态资源请求也会被认为是 404
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 返回 404 状态码
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("Handler not found for request: {}", e.getRequestURL());
        return R.fail(HttpStatus.NOT_FOUND.value(), "请求的资源不存在");
    }

    // 可以根据需要添加更多特定异常的处理方法，例如数据库异常、权限异常等
} 