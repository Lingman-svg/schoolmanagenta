/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 02:44:10
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 15:53:23
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\handler\GlobalExceptionHandler.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.handler;

import com.school.utils.R;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @Valid @RequestBody 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回 400 状态码
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Request Body Validation Failed: {}", e.getMessage());
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return R.fail(HttpStatus.BAD_REQUEST.value(), "参数校验失败: " + errorMsg);
    }

    /**
     * 处理 @Valid 对象绑定参数校验异常（Form Data / Query Params）
     * 与 MethodArgumentNotValidException 类似，但通常用于非 JSON 请求体
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleBindException(BindException e) {
        log.warn("Binding Failed: {}", e.getMessage());
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return R.fail(HttpStatus.BAD_REQUEST.value(), "参数绑定失败: " + errorMsg);
    }

    /**
     * 处理 @Validated @PathVariable, @RequestParam 等参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("Constraint Violation Failed: {}", e.getMessage());
        String errorMsg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return R.fail(HttpStatus.BAD_REQUEST.value(), "参数校验失败: " + errorMsg);
    }
    
    /**
     * 处理业务逻辑校验异常 (例如 Service 层手动抛出的 ValidationException)
     */
    @ExceptionHandler(jakarta.validation.ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleValidationException(jakarta.validation.ValidationException e) {
        log.warn("Business Validation Failed: {}", e.getMessage());
        return R.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage()); // Directly use the exception message
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP Method Not Supported: {}", e.getMessage());
        return R.fail(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 处理请求路径不存在异常 (需要配置 spring.mvc.throw-exception-if-no-handler-found=true 和 spring.web.resources.add-mappings=false)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("Handler Not Found: {}", e.getMessage());
        return R.fail(HttpStatus.NOT_FOUND.value(), "请求的资源不存在: " + e.getRequestURL());
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 返回 500 状态码
    public R<?> handleGenericException(Exception e) {
        log.error("An unexpected error occurred: ", e);
        // 可以根据 e 的类型细化处理，例如区分数据库异常、网络异常等
        // 为了安全，不直接暴露详细的异常信息给前端
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误，请联系管理员");
    }

} 