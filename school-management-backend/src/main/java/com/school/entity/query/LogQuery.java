package com.school.entity.query;

import com.school.entity.query.PageDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 日志查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogQuery extends PageDomain {

    /**
     * 操作模块
     */
    private String title;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 业务类型
     */
    private Integer businessType;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 操作开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    /**
     * 操作结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

} 