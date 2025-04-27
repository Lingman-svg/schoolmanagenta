package com.school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.annotation.Log;
import com.school.constant.BusinessType;
import com.school.entity.CourseTime;
import com.school.entity.query.CourseTimeQuery;
import com.school.handler.BusinessException;
import com.school.service.CourseTimeService;
import com.school.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 节课时间表 前端控制器
 * </p>
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Slf4j
@RestController
@RequestMapping("/resource/course-times")
@RequiredArgsConstructor
@Tag(name = "节课时间管理", description = "用于管理节课时间信息的接口")
@Validated
public class CourseTimeController {

    private final CourseTimeService courseTimeService;

    @Operation(summary = "获取所有节课时间列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('resource:courseTime:list')")
    public R<List<CourseTime>> listCourseTimes() {
        log.info("开始获取所有节课时间列表");
        List<CourseTime> list = courseTimeService.list();
        return R.success(list);
    }

    @Operation(summary = "根据条件分页查询节课时间")
    @GetMapping
    @PreAuthorize("hasAuthority('resource:courseTime:list')")
    public R<IPage<CourseTime>> listCourseTimesByPage(CourseTimeQuery query) {
        log.info("根据条件分页查询节课时间，参数: {}", query);
        IPage<CourseTime> page = courseTimeService.findCourseTimesByPage(query);
        return R.success(page);
    }

    @Operation(summary = "根据ID查询节课时间")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('resource:courseTime:view')")
    public R<CourseTime> getCourseTimeById(@PathVariable @Parameter(description = "节课时间ID") Long id) {
        log.info("根据ID查询节课时间，ID: {}", id);
        CourseTime courseTime = courseTimeService.getById(id);
        if (courseTime == null) {
            throw new BusinessException("未找到对应的节课时间");
        }
        return R.success(courseTime);
    }

    @Operation(summary = "新增节课时间")
    @PostMapping
    @PreAuthorize("hasAuthority('resource:courseTime:add')")
    @Log(title = "节课时间管理", businessType = BusinessType.INSERT)
    public R<Void> addCourseTime(@Validated @RequestBody CourseTime courseTime) {
        log.info("新增节课时间，参数: {}", courseTime);
        boolean success = courseTimeService.save(courseTime);
        return R.result(success);
    }

    @Operation(summary = "修改节课时间")
    @PutMapping
    @PreAuthorize("hasAuthority('resource:courseTime:edit')")
    @Log(title = "节课时间管理", businessType = BusinessType.UPDATE)
    public R<Void> updateCourseTime(@Validated @RequestBody CourseTime courseTime) {
        log.info("修改节课时间，参数: {}", courseTime);
        if (courseTime.getId() == null) {
            return R.fail("更新节课时间时必须提供ID");
        }
        boolean success = courseTimeService.updateById(courseTime);
        return R.result(success);
    }

    @Operation(summary = "根据ID删除节课时间")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('resource:courseTime:delete')")
    @Log(title = "节课时间管理", businessType = BusinessType.DELETE)
    public R<Void> deleteCourseTimeById(@PathVariable @Parameter(description = "节课时间ID") Long id) {
        log.info("根据ID删除节课时间，ID: {}", id);
        boolean success = courseTimeService.removeById(id);
        return R.result(success);
    }

    @Operation(summary = "批量删除节课时间")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('resource:courseTime:delete')")
    @Log(title = "节课时间管理", businessType = BusinessType.DELETE)
    public R<Void> deleteBatchCourseTimes(@RequestBody @Parameter(description = "要删除的节课时间ID列表") List<Long> ids) {
        log.info("批量删除节课时间，IDs: {}", ids);
        if (ids == null || ids.isEmpty()) {
            return R.fail("请选择要删除的节课时间");
        }
        boolean success = courseTimeService.removeByIds(ids);
        return R.result(success);
    }

    @Operation(summary = "获取所有有效节课时间列表 (用于下拉)")
    @GetMapping("/valid")
    @PreAuthorize("hasAuthority('resource:courseTime:list')")
    public R<List<CourseTime>> listValid() {
        log.info("获取所有有效节课时间列表");
        List<CourseTime> list = courseTimeService.listValidCourseTimes();
        return R.success(list);
    }

} 