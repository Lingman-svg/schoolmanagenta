/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 20:07:57
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 20:38:37
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\impl\CourseTimeServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 20:07:57
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 20:13:18
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\impl\CourseTimeServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.CourseTime;
import com.school.entity.query.CourseTimeQuery;
import com.school.handler.BusinessException;
import com.school.mapper.CourseTimeMapper;
import com.school.service.CourseTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalTime;
import java.util.Collection;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 节课时间表 服务实现类
 * </p>
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseTimeServiceImpl extends ServiceImpl<CourseTimeMapper, CourseTime> implements CourseTimeService {

    // Mapper 通过 @RequiredArgsConstructor 注入
    private final CourseTimeMapper courseTimeMapper;

    @Override
    public IPage<CourseTime> findCourseTimesByPage(CourseTimeQuery query) {
        Page<CourseTime> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CourseTime> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件 - 根据 CourseTimeQuery 中的字段添加
        wrapper.like(StrUtil.isNotBlank(query.getPeriodName()), CourseTime::getPeriodName, query.getPeriodName());
        // 可以添加其他查询条件，例如根据时间范围等
        // wrapper.ge(query.getStartTimeBegin() != null, CourseTime::getStartTime, query.getStartTimeBegin());
        // wrapper.le(query.getStartTimeEnd() != null, CourseTime::getStartTime, query.getStartTimeEnd());
        wrapper.eq( CourseTime::getIsValid, 1); // 添加是否有效条件

        // 添加排序 (与 SubjectServiceImpl 保持一致)
        if (StrUtil.isNotBlank(query.getOrderByColumn())) {
            boolean isAsc = "asc".equalsIgnoreCase(query.getIsAsc());
            // 假设按 startTime 排序
            wrapper.orderBy(true, isAsc, CourseTime::getStartTime);
        } else {
            // 默认排序，例如按开始时间升序
            wrapper.orderByAsc(CourseTime::getStartTime);
        }

        return courseTimeMapper.selectPage(page, wrapper);
    }


    @Override
    @Transactional
    public boolean save(CourseTime entity) {
        log.info("新增节课时间，参数: {}", entity);
        validateCourseTime(entity);
        checkTimeOverlap(entity);
        return super.save(entity);
    }

    @Override
    @Transactional
    public boolean updateById(CourseTime entity) {
        log.info("修改节课时间，参数: {}", entity);
        if (ObjectUtils.isEmpty(entity.getId())) {
            throw new BusinessException("更新时必须提供ID");
        }
        if (getById(entity.getId()) == null) {
             throw new BusinessException("未找到对应的节课时间记录，无法更新");
        }
        validateCourseTime(entity);
        checkTimeOverlap(entity);
        return super.updateById(entity);
    }

     @Override
    @Transactional
    public boolean removeById(Serializable id) {
        log.info("删除节课时间，ID: {}", id);
        if (ObjectUtils.isEmpty(id)) {
            throw new BusinessException("删除时必须提供ID");
        }
        // 检查记录是否存在
        if (getById(id) == null) {
            throw new BusinessException("未找到对应的节课时间记录，无法删除");
        }
        // TODO: 检查是否有其他地方引用了此节课时间，例如课表
        // boolean isInUse = checkCourseTimeUsage(id);
        return super.removeById(id);
    }


    @Override
    @Transactional
    public boolean removeByIds(Collection<?> list) {
        log.info("批量删除节课时间，IDs: {}", list);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException("请提供要删除的ID列表");
        }
        // 可以选择性地检查每个ID是否存在，但批量操作通常假设ID有效，或在数据库层面处理不存在的ID
        return super.removeByIds(list);
    }

    /**
     * 校验节课时间数据的有效性
     * @param courseTime 节课时间实体
     */
    private void validateCourseTime(CourseTime courseTime) {
        if (courseTime == null) {
             throw new BusinessException("节课时间信息不能为空");
        }
        if (courseTime.getStartTime() != null && courseTime.getEndTime() != null && courseTime.getStartTime().isAfter(courseTime.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        // 可以根据需要添加更多校验，例如非空校验
        if (ObjectUtils.isEmpty(courseTime.getPeriodName())) {
             throw new BusinessException("节课名称不能为空");
        }
        if (ObjectUtils.isEmpty(courseTime.getStartTime())) {
             throw new BusinessException("开始时间不能为空");
        }
        if (ObjectUtils.isEmpty(courseTime.getEndTime())) {
            throw new BusinessException("结束时间不能为空");
        }
    }

    /**
     * 检查节课时间段是否与现有记录重叠
     * @param courseTime 要检查的节课时间实体 (新建或更新)
     */
    private void checkTimeOverlap(CourseTime courseTime) {
        LocalTime newStartTime = courseTime.getStartTime();
        LocalTime newEndTime = courseTime.getEndTime();

        LambdaQueryWrapper<CourseTime> wrapper = new LambdaQueryWrapper<>();
        // 条件： !(existing.endTime <= new.startTime OR existing.startTime >= new.endTime)
        // 等价于： existing.endTime > new.startTime AND existing.startTime < new.endTime
        wrapper.lt(CourseTime::getStartTime, newEndTime); // existing.startTime < new.endTime
        wrapper.gt(CourseTime::getEndTime, newStartTime); // existing.endTime > new.startTime
        
        // 如果是更新操作，需要排除自身
        if (courseTime.getId() != null) {
            wrapper.ne(CourseTime::getId, courseTime.getId());
        }
        
        // 检查是否存在任何满足条件的记录
        boolean overlaps = courseTimeMapper.exists(wrapper);
        if (overlaps) {
            throw new BusinessException("节课时间段 [" + newStartTime + " - " + newEndTime + "] 与现有记录重叠");
        }
    }

    @Override
    public boolean existsById(Long id) {
        return this.getById(id) != null;
    }

    @Override
    public List<CourseTime> listValidCourseTimes() {
        log.info("获取所有有效节课时间列表");
        LambdaQueryWrapper<CourseTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTime::getIsValid, 1);
        // 按开始时间排序
        wrapper.orderByAsc(CourseTime::getStartTime);
        return this.list(wrapper);
    }

} 