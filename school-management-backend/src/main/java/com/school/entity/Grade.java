/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 16:09:13
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 17:20:11
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\Grade.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承 BaseEntity 时需要注意 equals 和 hashCode
@Accessors(chain = true) // 支持链式调用
@TableName("sch_grade") // 显式指定表名
public class Grade extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

    /**
     * 科目ID
     */
    @TableField("subject_id")
    private Long subjectId;

    /**
     * 班级ID (学生参加考试时的班级)
     */
    @TableField("clazz_id")
    private Long clazzId;

    /**
     * 任课老师ID (可选)
     */
    @TableField("teacher_id")
    private Long teacherId;

    /**
     * 考试名称/描述
     */
    @TableField("exam_name")
    private String examName;

    /**
     * 考试时间
     */
    @TableField("exam_time")
    private LocalDateTime examTime;

    /**
     * 成绩分数
     */
    @TableField("score")
    private BigDecimal score;

    /**
     * 成绩录入时间
     */
    @TableField("record_time")
    private LocalDateTime recordTime;


    // create_by, create_time, update_by, update_time, deleted 由 BaseEntity 提供

} 