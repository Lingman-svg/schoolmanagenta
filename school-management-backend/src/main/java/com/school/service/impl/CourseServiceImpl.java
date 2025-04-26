package com.school.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.Course;
import com.school.entity.query.CourseQuery;
import com.school.handler.BusinessException;
import com.school.mapper.CourseMapper;
import com.school.service.*; // 引入需要的 Service 接口
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 课程安排 Service 实现类
 *
 * @author Gemini
 * @since 2024-05-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseMapper courseMapper;
    // 可能需要注入其他 Service 用于校验外键是否存在
    private final ClazzService clazzService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final CourseTimeService courseTimeService;

    @Override
    public IPage<?> findCoursesByPage(CourseQuery query) {
        Page<Course> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Course> wrapper = buildQueryWrapper(query);

        // TODO: 如果前端需要显示关联名称 (班级名、教师名等)，这里需要进行 DTO/VO 转换
        // 1. 查询 Course 分页数据
        IPage<Course> coursePage = courseMapper.selectPage(page, wrapper);
        // 2. (如果需要) 查询关联信息并组装成 VO/DTO
        // List<CourseVo> voList = convertToVo(coursePage.getRecords());
        // IPage<CourseVo> voPage = new Page<>(coursePage.getCurrent(), coursePage.getSize(), coursePage.getTotal());
        // voPage.setRecords(voList);
        // return voPage;
        
        // 当前仅返回 Course 实体
        return coursePage;
    }

    private LambdaQueryWrapper<Course> buildQueryWrapper(CourseQuery query) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getClassId() != null, Course::getClassId, query.getClassId());
        wrapper.eq(query.getTeacherId() != null, Course::getTeacherId, query.getTeacherId());
        wrapper.eq(query.getSubjectId() != null, Course::getSubjectId, query.getSubjectId());
        wrapper.eq(query.getDayOfWeek() != null, Course::getDayOfWeek, query.getDayOfWeek());
        wrapper.eq(query.getIsValid() != null, Course::getIsValid, query.getIsValid());
        
        // 默认排序，例如按星期、节次排序
        if (StrUtil.isBlank(query.getOrderByColumn())) {
             wrapper.orderByAsc(Course::getDayOfWeek).orderByAsc(Course::getCourseTimeId);
        }
        // else { 处理前端传递的排序字段 }
        
        return wrapper;
    }
    
    @Override
    @Transactional
    public boolean addCourse(Course course) {
        log.info("新增课程安排: {}", course);
        validateCourse(course); // 基本校验 + 外键校验
        checkConflict(course); // 冲突校验
        return this.save(course);
    }

    @Override
    @Transactional
    public boolean updateCourse(Course course) {
        log.info("更新课程安排: {}", course);
        if (course.getId() == null) {
            throw new BusinessException("更新时必须提供课程ID");
        }
        // 检查课程是否存在
        if (this.getById(course.getId()) == null) {
             throw new BusinessException("未找到对应的课程安排记录，无法更新");
        }
        validateCourse(course); // 基本校验 + 外键校验
        checkConflict(course); // 冲突校验
        return this.updateById(course);
    }

    /**
     * 校验课程数据的基本有效性和外键存在性
     */
    private void validateCourse(Course course) {
        if (course == null) throw new BusinessException("课程信息不能为空");
        // @NotNull 等注解已校验部分非空
        
        // 校验外键是否存在 - 使用 getById != null 替代不存在的 existsById
        if (course.getClassId() == null || clazzService.getById(course.getClassId()) == null) {
            throw new BusinessException("指定的班级不存在");
        }
        if (course.getTeacherId() == null || teacherService.getById(course.getTeacherId()) == null) {
            throw new BusinessException("指定的教师不存在");
        }
        if (course.getSubjectId() == null || subjectService.getById(course.getSubjectId()) == null) {
            throw new BusinessException("指定的科目不存在");
        }
        if (course.getCourseTimeId() == null || courseTimeService.getById(course.getCourseTimeId()) == null) {
             throw new BusinessException("指定的节课时间不存在");
        }
        // 可根据需要添加 isValid 校验等
    }

    /**
     * 核心校验逻辑：检查时间地点冲突
     * 1. 同一个班级在同一个时间（星期几 + 节课）不能有两门课
     * 2. 同一个教师在同一个时间（星期几 + 节课）不能有两门课
     * 3. 同一个地点在同一个时间（星期几 + 节课）不能被占用 (如果 location 非空)
     */
    private void checkConflict(Course course) {
        Long courseId = course.getId(); // 用于更新时排除自身
        Long classId = course.getClassId();
        Long teacherId = course.getTeacherId();
        Long courseTimeId = course.getCourseTimeId();
        Integer dayOfWeek = course.getDayOfWeek();
        String location = course.getLocation();

        // 1. 检查班级时间冲突
        LambdaQueryWrapper<Course> classConflictWrapper = new LambdaQueryWrapper<>();
        classConflictWrapper.eq(Course::getClassId, classId)
                            .eq(Course::getDayOfWeek, dayOfWeek)
                            .eq(Course::getCourseTimeId, courseTimeId);
        if (courseId != null) {
            classConflictWrapper.ne(Course::getId, courseId);
        }
        if (courseMapper.exists(classConflictWrapper)) {
            throw new BusinessException("该班级在此时间已安排其他课程");
        }

        // 2. 检查教师时间冲突
        LambdaQueryWrapper<Course> teacherConflictWrapper = new LambdaQueryWrapper<>();
        teacherConflictWrapper.eq(Course::getTeacherId, teacherId)
                              .eq(Course::getDayOfWeek, dayOfWeek)
                              .eq(Course::getCourseTimeId, courseTimeId);
        if (courseId != null) {
            teacherConflictWrapper.ne(Course::getId, courseId);
        }
        if (courseMapper.exists(teacherConflictWrapper)) {
            throw new BusinessException("该教师在此时间已安排其他课程");
        }

        // 3. 检查地点时间冲突 (仅当地点非空时)
        if (StrUtil.isNotBlank(location)) {
            LambdaQueryWrapper<Course> locationConflictWrapper = new LambdaQueryWrapper<>();
            locationConflictWrapper.eq(Course::getLocation, location)
                                   .eq(Course::getDayOfWeek, dayOfWeek)
                                   .eq(Course::getCourseTimeId, courseTimeId);
             if (courseId != null) {
                locationConflictWrapper.ne(Course::getId, courseId);
            }
            if (courseMapper.exists(locationConflictWrapper)) {
                throw new BusinessException("该地点在此时间已被占用: " + location);
            }
        }
    }

    // IService 提供的 save, updateById, removeById, removeByIds 会被调用
    // 如果需要对删除操作也添加校验（例如检查是否有关联成绩），可以覆盖 remove 方法
} 