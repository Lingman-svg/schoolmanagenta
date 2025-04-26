package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Course;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程安排 Mapper 接口
 *
 * @author Gemini
 * @since 2024-05-12
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    // 基础 CRUD 由 BaseMapper 提供
    // 如果需要复杂的关联查询（例如，直接查询出包含班级名、教师名等的列表），
    // 可以在这里定义方法并在对应的 XML 文件中实现 SQL
    // 例如: IPage<CourseVo> selectCourseVoPage(Page<?> page, @Param("query") CourseQuery query);

} 