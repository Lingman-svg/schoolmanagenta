package com.school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.annotation.Log;
import com.school.constant.BusinessType;
import com.school.entity.Course;
import com.school.entity.query.CourseQuery;
import com.school.service.CourseService;
import com.school.utils.R; // 引入统一响应结果类
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
 * 课程安排管理 Controller
 *
 * @author Gemini
 * @since 2024-05-12
 */
@Slf4j
@RestController
@RequestMapping("/resource/courses") // Corrected base path
@RequiredArgsConstructor
@Tag(name = "课程安排管理", description = "用于管理课程安排信息的接口")
@Validated // 开启方法级别的参数校验
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "根据条件分页查询课程安排")
    @GetMapping
    @PreAuthorize("hasAuthority('resource:course:list')") // Corrected permission
    public R<IPage<?>> listCoursesByPage(CourseQuery query) { // 返回 IPage<?>，因为 Service 可能返回 Course 或 VO
        log.info("根据条件分页查询课程安排，参数: {}", query);
        IPage<?> page = courseService.findCoursesByPage(query);
        return R.success(page);
    }

    @Operation(summary = "根据ID查询课程安排详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('resource:course:view')") // Corrected permission
    public R<Course> getCourseById(@PathVariable @Parameter(description = "课程安排ID") Long id) {
        log.info("根据ID查询课程安排详情，ID: {}", id);
        Course course = courseService.getById(id);
        // TODO: 如果需要返回包含关联名称的 VO/DTO，这里需要调用 Service 的特定方法
        return course != null ? R.success(course) : R.fail(R.NOT_FOUND_CODE, "课程安排未找到");
    }

    @Operation(summary = "新增课程安排")
    @PostMapping
    @PreAuthorize("hasAuthority('resource:course:add')") // Corrected permission
    @Log(title = "课程安排管理", businessType = BusinessType.INSERT)
    public R<Void> addCourse(@Validated @RequestBody Course course) { // 使用 @Validated 校验 Course 实体中的注解
        log.info("新增课程安排，参数: {}", course);
        boolean success = courseService.addCourse(course); // 调用包含校验的 Service 方法
        return R.result(success);
    }

    @Operation(summary = "修改课程安排")
    @PutMapping
    @PreAuthorize("hasAuthority('resource:course:edit')") // Corrected permission
    @Log(title = "课程安排管理", businessType = BusinessType.UPDATE)
    public R<Void> updateCourse(@Validated @RequestBody Course course) {
        log.info("修改课程安排，参数: {}", course);
        // Controller 层基础校验
        if (course.getId() == null) {
            return R.fail("更新课程安排时必须提供ID");
        }
        boolean success = courseService.updateCourse(course); // 调用包含校验的 Service 方法
        return R.result(success);
    }

    @Operation(summary = "根据ID删除课程安排")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('resource:course:delete')") // Corrected permission
    @Log(title = "课程安排管理", businessType = BusinessType.DELETE)
    public R<Void> deleteCourseById(@PathVariable @Parameter(description = "课程安排ID") Long id) {
        log.info("根据ID删除课程安排，ID: {}", id);
        // Service 层 removeById (来自 IService) 默认只做逻辑删除
        // 如果需要在删除前做校验 (如检查是否有关联成绩)，需要在 ServiceImpl 中覆盖 removeById
        boolean success = courseService.removeById(id);
        return R.result(success);
    }

    @Operation(summary = "批量删除课程安排")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('resource:course:delete')") // Corrected permission
    @Log(title = "课程安排管理", businessType = BusinessType.DELETE)
    public R<Void> deleteBatchCourses(@RequestBody @Parameter(description = "要删除的课程安排ID列表") List<Long> ids) {
        log.info("批量删除课程安排，IDs: {}", ids);
        if (ids == null || ids.isEmpty()) {
            return R.fail("请选择要删除的课程安排");
        }
        // 同样，如果需要批量删除前的校验，需覆盖 ServiceImpl 的 removeByIds
        boolean success = courseService.removeByIds(ids);
        return R.result(success);
    }

    // TODO: 根据需要添加导入导出的接口

} 