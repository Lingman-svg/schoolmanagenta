package com.school.entity.dto;

import com.school.entity.Grade;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 成绩数据传输对象 (包含关联名称)
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承 Grade
public class GradeDto extends Grade {

    private static final long serialVersionUID = 1L;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 科目名称
     */
    private String subjectName;

    /**
     * 班级名称
     */
    private String clazzName;

    /**
     * 教师姓名 (可选)
     */
    private String teacherName;

    // 可以根据需要添加其他 DTO 特有的字段
} 