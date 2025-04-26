package com.school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.CourseTime;
import com.school.entity.query.CourseTimeQuery;

import java.util.List;
import java.util.Collection;
import java.io.Serializable;

/**
 * <p>
 * 节课时间表 服务类
 * </p>
 *
 * @author Gemini
 * @since 2024-05-10 // 这里可以更新为当前日期，或者保持生成日期
 */
public interface CourseTimeService extends IService<CourseTime> {

    /**
     * 根据条件分页查询节课时间
     * @param query 查询条件对象
     * @return 分页结果
     */
    IPage<CourseTime> findCourseTimesByPage(CourseTimeQuery query);

    boolean existsById(Long id);

    /**
     * 获取所有有效的节课时间列表 (用于下拉选择)
     * @return 有效节课时间列表 (通常包含 ID, 名称, 开始/结束时间)
     */
    List<CourseTime> listValidCourseTimes();

    // 可以在这里定义 Service 层特有的方法
    // 例如：获取所有有效的节课时间列表 (用于下拉)

} 