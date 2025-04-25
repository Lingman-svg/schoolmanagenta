package com.school.utils; // 放置在 utils 包下

import lombok.Data;
import java.io.Serializable;

/**
 * 通用响应结果封装类
 * @param <T> 响应数据的类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    // --- 预定义状态码常量 (可以根据需要扩展) ---
    public static final int SUCCESS_CODE = 200;
    public static final String SUCCESS_MSG = "操作成功";

    public static final int FAIL_CODE = 500;
    public static final String FAIL_MSG = "操作失败";

    public static final int UNAUTHORIZED_CODE = 401;
    public static final String UNAUTHORIZED_MSG = "未授权";

    public static final int FORBIDDEN_CODE = 403;
    public static final String FORBIDDEN_MSG = "禁止访问";

    public static final int NOT_FOUND_CODE = 404;
    public static final String NOT_FOUND_MSG = "资源未找到";

    // --- 构造方法 (私有，通过静态方法创建实例) ---
    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // --- 静态工厂方法 ---

    /**
     * 返回成功结果 (无数据)
     * @return R<Void>
     */
    public static <T> R<T> success() {
        return new R<>(SUCCESS_CODE, SUCCESS_MSG, null);
    }

    /**
     * 返回成功结果 (带数据)
     * @param data 响应数据
     * @return R<T>
     */
    public static <T> R<T> success(T data) {
        return new R<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

        /**
     * 返回成功结果 (信息)
     * @param msg 信息
     * @return R<T>
     */
    public static <T> R<T> success(String msg) {
        return new R<>(SUCCESS_CODE, msg, null);
    }


    /**
     * 返回成功结果 (自定义消息和数据)
     * @param msg 成功消息
     * @param data 响应数据
     * @return R<T>
     */
    public static <T> R<T> success(String msg, T data) {
        return new R<>(SUCCESS_CODE, msg, data);
    }

    /**
     * 返回失败结果 (使用默认失败消息)
     * @return R<Void>
     */
    public static <T> R<T> fail() {
        return new R<>(FAIL_CODE, FAIL_MSG, null);
    }

    /**
     * 返回失败结果 (自定义失败消息)
     * @param msg 失败消息
     * @return R<Void>
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(FAIL_CODE, msg, null);
    }

    /**
     * 返回失败结果 (自定义状态码和消息)
     * @param code 状态码
     * @param msg 失败消息
     * @return R<Void>
     */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }

    /**
     * 返回失败结果 (自定义状态码、消息和数据)
     * @param code 状态码
     * @param msg 失败消息
     * @param data 响应数据 (例如，错误详情)
     * @return R<T>
     */
    public static <T> R<T> fail(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    /**
     * 根据布尔值决定返回成功或失败 (无数据)
     * @param success 布尔值
     * @return R<Void>
     */
    public static <T> R<T> result(boolean success) {
        return success ? success() : fail();
    }

    /**
     * 根据布尔值决定返回成功或失败 (带数据)
     * @param success 布尔值
     * @param data 成功时的数据
     * @return R<T>
     */
    public static <T> R<T> result(boolean success, T data) {
        return success ? success(data) : fail();
    }

    /**
     * 判断是否成功
     * @return boolean
     */
    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }
} 