/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 21:54:30
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:10:00
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\SchoolManagementBackendApplication.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot 主应用程序类
 */
@SpringBootApplication // 核心注解，启用 Spring Boot 自动配置、组件扫描等
@MapperScan("com.school.mapper") // 指定 Mybatis/MybatisPlus Mapper 接口所在的包
@EnableScheduling // Enable scheduling
@EnableAsync // 启用异步方法执行支持
public class SchoolManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolManagementBackendApplication.class, args);
        System.out.println("School Management Backend Application Started Successfully!");
    }

} 