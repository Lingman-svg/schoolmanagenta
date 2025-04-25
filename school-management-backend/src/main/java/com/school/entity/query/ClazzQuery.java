package com.school.entity.query;

import com.school.entity.query.PageDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 班级查询参数封装
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClazzQuery extends PageDomain {

    /**
     * 班级名称 (模糊查询)
     */
    private String className;

    /**
     * 年级
     */
    private String grade;

    /**
     * 班主任教师ID
     */
    private Long teacherId;

    /**
     * 班级状态
     */
    private Integer status;

    /**
     * 开班日期范围 - 开始
     */
    private LocalDate startDateBegin;

    /**
     * 开班日期范围 - 结束
     */
    private LocalDate startDateEnd;

} 