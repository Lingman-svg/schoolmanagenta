/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 21:23:15
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 21:23:55
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\Course.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程安排实体类
 *
 * @author Gemini
 * @since 2024-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承 BaseEntity 时需要
@TableName("sch_course") // 指定数据库表名
public class Course extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty("学生ID")
    private Long id;


    /**
     * 班级ID
     */
    @NotNull(message = "班级不能为空")
    @TableField("class_id")
    private Long classId;

    /**
     * 教师ID
     */
    @NotNull(message = "授课教师不能为空")
    @TableField("teacher_id")
    private Long teacherId;

    /**
     * 科目ID
     */
    @NotNull(message = "授课科目不能为空")
    @TableField("subject_id")
    private Long subjectId;

    /**
     * 节课ID (关联 sch_course_time)
     */
    @NotNull(message = "上课节次不能为空")
    @TableField("course_time_id")
    private Long courseTimeId;

    /**
     * 星期几 (1-7)
     */
    @NotNull(message = "星期不能为空")
    @Min(value = 1, message = "星期必须在 1-7 之间")
    @Max(value = 7, message = "星期必须在 1-7 之间")
    @TableField("day_of_week")
    private Integer dayOfWeek;

    /**
     * 课程介绍
     */
    @TableField("introduction")
    private String introduction;

    /**
     * 上课地点
     */
    @TableField("location")
    private String location;
    

    // remark, isDeleted, createBy, createTime, updateBy, updateTime 继承自 BaseEntity
} 