package com.school.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.school.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 班级实体类
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sch_clazz")
public class Clazz extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO) // 假设主键是自增的
    @ExcelProperty("科目ID") // EasyExcel 导出注解
    private Long id;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 年级 (例如: "一年级", "二年级", "初一")
     */
    private String grade;

    /**
     * 班主任教师ID
     */
    private Long teacherId;

    /**
     * 开班日期
     */
    private LocalDate startDate;

    /**
     * 结业日期 (可根据开班日期和学制自动计算)
     */
    private LocalDate endDate;

    /**
     * 班级状态 (例如: 0: 未开班, 1: 进行中, 2: 已结业)
     */
    private Integer status;
} 