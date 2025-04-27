/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-05-10 17:08:13
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-05-10 17:16:35
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\ai\IntentRouter.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.ai;

import com.school.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 意图到Service的自动路由与调用
 */
@Component
public class IntentRouter {
    @Resource
    private StudentService studentService;
    @Resource private TeacherService teacherService;
    @Resource private ClazzService clazzService;
    @Resource private CourseService courseService;
    @Resource private GradeService gradeService;
    @Resource private SubjectService subjectService;

    /**
     * 根据意图类型和参数自动调用对应Service方法
     * @param intentResult 解析结果
     * @return 查询结果（结构化数据或表格，实际可用DTO/VO）
     */
    public Object route(IntentParseResult intentResult) {
        IntentType type = intentResult.getIntentType();
        Map<String, Object> params = intentResult.getParams();
        if (type == null) return buildError("无法识别您的意图，请重新描述。");
        // TODO: 权限校验可在此处扩展
        try {
            switch (type) {
                case QUERY_STUDENT_GRADE:
                    return buildSuccess(gradeService.getStudentGrade(params));
                case QUERY_CLASS_SCHEDULE:
                    // 没有getClassSchedule，统一用getCourseInfo
                    return buildSuccess(courseService.getCourseInfo(params));
                case QUERY_TEACHER_SUBJECTS:
                    return buildSuccess(teacherService.getTeacherSubjects(params));
                case QUERY_STUDENT_COUNT:
                    return buildSuccess(studentService.getStudentCount(params));
                case QUERY_STUDENT_INFO:
                    return buildSuccess(studentService.getStudentInfo(params));
                case QUERY_TEACHER_INFO:
                    return buildSuccess(teacherService.getTeacherInfo(params));
                case QUERY_CLASS_INFO:
                    return buildSuccess(clazzService.getClazzInfo(params));
                case QUERY_COURSE_INFO:
                    return buildSuccess(courseService.getCourseInfo(params));
                case QUERY_SUBJECT_INFO:
                    return buildSuccess(subjectService.getSubjectInfo(params));
                default:
                    return buildError("暂不支持该类型的查询。");
            }
        } catch (Exception e) {
            return buildError("查询过程中发生异常: " + e.getMessage());
        }
    }

    /**
     * 统一结构化响应：成功
     */
    private Map<String, Object> buildSuccess(Object data) {
        return Map.of(
                "success", true,
                "data", data
        );
    }
    /**
     * 统一结构化响应：失败
     */
    private Map<String, Object> buildError(String msg) {
        return Map.of(
                "success", false,
                "error", msg
        );
    }
} 