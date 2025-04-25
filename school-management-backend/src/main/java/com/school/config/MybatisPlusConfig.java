/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 21:43:07
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-25 21:44:23
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\config\MybatisPlusConfig.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MybatisPlus 配置类
 */
@Configuration
public class MybatisPlusConfig {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlusConfig.class);

    /**
     * MybatisPlus 拦截器配置
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 指定数据库类型为 MySQL
        log.info("MybatisPlus PaginationInnerInterceptor added.");
        return interceptor;
    }

    /**
     * 元数据对象处理器，用于自动填充字段
     * 需要在 application.yml 中配置: mybatis-plus.global-config.meta-object-handler=com.school.config.MyMetaObjectHandler
     * 或者直接将该类注册为 Spring Bean (@Component)
     */
    @Component
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        private static final Logger handlerLog = LoggerFactory.getLogger(MyMetaObjectHandler.class);

        @Override
        public void insertFill(MetaObject metaObject) {
            handlerLog.debug("Start insert fill ....");
            // 起始版本 3.3.0(推荐使用)
            this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class); // 创建时间
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class); // 修改时间
            this.strictInsertFill(metaObject, "createBy", this::getCurrentUserId, Long.class); // 创建人ID (需要实现 getCurrentUserId 方法)
            this.strictInsertFill(metaObject, "updateBy", this::getCurrentUserId, Long.class); // 修改人ID (需要实现 getCurrentUserId 方法)
            // 如果实体类中没有这些字段，MybatisPlus 不会报错，但也不会填充

            // 旧版兼容方式 (3.3.0 以下)
            // this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
            // this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
            // this.setFieldValByName("createBy", getCurrentUserId(), metaObject);
            // this.setFieldValByName("updateBy", getCurrentUserId(), metaObject);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            handlerLog.debug("Start update fill ....");
            // 起始版本 3.3.0(推荐使用)
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class); // 修改时间
            this.strictUpdateFill(metaObject, "updateBy", this::getCurrentUserId, Long.class); // 修改人ID

            // 旧版兼容方式 (3.3.0 以下)
            // this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
            // this.setFieldValByName("updateBy", getCurrentUserId(), metaObject);
        }

        /**
         * 获取当前用户ID - 需要根据你的认证/授权框架实现
         * 例如，从 Spring Security Context, Shiro Session, 或请求头中获取
         * 这里返回一个示例值 0L, **请务必替换为实际的获取逻辑**
         * @return 当前用户ID，如果未登录或无法获取，可以返回 null 或特定值
         */
        private Long getCurrentUserId() {
            // TODO: Replace with actual logic to get current user ID
            // Example using Spring Security (if configured):
            /*
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                // Assuming your UserDetails implementation has a method to get the ID
                // return userDetails.getId();
                return 1L; // Placeholder ID
            }
            */
            handlerLog.warn("Returning default user ID (0L) for audit fields. Please implement getCurrentUserId().");
            return 0L; // Placeholder: 系统默认用户或未登录用户
        }
    }
} 