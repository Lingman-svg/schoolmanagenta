/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 22:08:02
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:09:03
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\annotation\Log.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.annotation;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 *
 * @author LingMeng
 */
@Target({ElementType.PARAMETER, ElementType.METHOD}) // 注解作用于参数和方法
@Retention(RetentionPolicy.RUNTIME) // 运行时可见
@Documented
public @interface Log {
    /**
     * 模块标题
     */
    String title() default "";

    /**
     * 功能（业务类型）
     * 建议定义一个枚举或常量类来表示不同的业务类型
     * 例如：0=OTHER, 1=INSERT, 2=UPDATE, 3=DELETE
     */
    int businessType() default 0; // 默认为其它

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;
} 