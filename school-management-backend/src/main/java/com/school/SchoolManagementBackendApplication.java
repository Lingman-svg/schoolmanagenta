package com.school;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot 主应用程序类
 */
@SpringBootApplication // 核心注解，启用 Spring Boot 自动配置、组件扫描等
@MapperScan("com.school.mapper") // 指定 Mybatis/MybatisPlus Mapper 接口所在的包
@EnableScheduling // Enable scheduling
public class SchoolManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolManagementBackendApplication.class, args);
        System.out.println("School Management Backend Application Started Successfully!");
    }

} 