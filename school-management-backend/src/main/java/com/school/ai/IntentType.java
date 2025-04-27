package com.school.ai;

/**
 * AI自然语言指令意图类型
 */
public enum IntentType {
    // 查询类意图
    QUERY_STUDENT_GRADE,         // 查询学生成绩
    QUERY_CLASS_SCHEDULE,        // 查询班级课程表
    QUERY_TEACHER_SUBJECTS,      // 查询教师所授科目
    QUERY_STUDENT_COUNT,         // 查询学生数量
    QUERY_STUDENT_INFO,          // 查询学生信息
    QUERY_TEACHER_INFO,          // 查询教师信息
    QUERY_CLASS_INFO,            // 查询班级信息
    QUERY_COURSE_INFO,           // 查询课程信息
    QUERY_SUBJECT_INFO,          // 查询科目信息
    // ...后续可扩展
} 