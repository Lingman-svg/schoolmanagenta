package com.school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Course;
import com.school.entity.query.CourseQuery;
// import com.school.entity.vo.CourseVo; // 如果需要 VO/DTO，取消注释

import java.util.List;

/**
 * 课程安排 Service 接口
 *
 * @author Gemini
 * @since 2024-05-12
 */
public interface CourseService extends IService<Course> {

    /**
     * 根据条件分页查询课程安排列表
     * @param query 查询条件对象
     * @return 分页结果 (可能是 Course 实体，也可能是包含关联信息的 VO/DTO)
     */
    IPage<?> findCoursesByPage(CourseQuery query);

    // IService 已包含 save, updateById, removeById, removeByIds 等基础方法
    // 实现类将重写这些方法以添加校验逻辑

    /**
     * 添加课程安排，包含冲突校验
     * @param course 课程信息
     * @return 是否成功
     */
    boolean addCourse(Course course);

    /**
     * 更新课程安排，包含冲突校验
     * @param course 课程信息
     * @return 是否成功
     */
    boolean updateCourse(Course course);

    // 可以根据需要添加其他业务方法
    // 例如：根据班级和星期获取课程列表
    // List<Course> findCoursesByClassAndDay(Long classId, Integer dayOfWeek);

    /**
     * AI路由专用：根据参数Map查询课程信息
     */
    Object getCourseInfo(java.util.Map<String, Object> params);

    /**
     * AI路由专用：根据参数Map统计课程数量
     */
    Object getCourseCount(java.util.Map<String, Object> params);
} 