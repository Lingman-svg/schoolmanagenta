/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 02:44:10
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 02:44:18
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