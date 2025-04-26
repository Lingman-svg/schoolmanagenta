package com.school.entity.query;

import com.school.entity.query.PageDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 成绩查询参数
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GradeQuery extends PageDomain { // Inherit from PageDomain

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 科目ID
     */
    private Long subjectId;

    /**
     * 班级ID
     */
    private Long clazzId;

    /**
     * 教师ID (录入人/任课老师)
     */
    private Long teacherId;

    /**
     * 考试名称 (支持模糊查询)
     */
    private String examName;

    // 可以根据需要添加其他查询条件，例如分数范围、考试时间范围等

} 