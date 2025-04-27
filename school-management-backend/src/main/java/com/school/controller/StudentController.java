package com.school.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.annotation.Log;
import com.school.constant.BusinessType;
import com.school.entity.Student;
import com.school.entity.StudentClazzHistory;
import com.school.entity.dto.StudentClazzHistoryDto;
import com.school.entity.query.StudentQuery;
import com.school.service.StudentService;
import com.school.utils.ExcelUtil;
import com.school.utils.R;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid; // For validating request body
import jakarta.validation.constraints.*; // Import validation constraints
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize; // 导入
import org.springframework.validation.annotation.Validated; // For controller level validation
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 学生管理 前端控制器
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@RestController
@RequestMapping("/resource/students")
@RequiredArgsConstructor
@Validated // Enable validation for path variables, request params etc. if needed
public class StudentController {

    private final StudentService studentService;

    /**
     * 分页查询学生列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('resource:student:list')")
    public R<Page<Student>> list(StudentQuery query) {
        // TODO: Consider returning a DTO with class name instead of just Student entity
        Page<Student> page = studentService.listStudents(query);
        return R.success(page);
    }

    /**
     * 根据ID获取学生详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('resource:student:view')")
    public R<Student> getInfo(@NotNull @Min(value = 1, message = "ID必须为正整数") @PathVariable Long id) {
        // TODO: Consider returning a DTO with class name
        Student student = studentService.getStudentInfo(id);
        if (student != null) {
            return R.success(student);
        } else {
            // Use R.fail with custom message or NOT_FOUND code
            return R.fail(R.NOT_FOUND_CODE, "未找到指定学生 (ID: " + id + ")"); 
        }
    }

    /**
     * 新增学生
     */
    @PostMapping
    @PreAuthorize("hasAuthority('resource:student:add')")
    @Log(title = "学生管理", businessType = BusinessType.INSERT)
    public R<Void> add(@Valid @RequestBody Student student) {
        // @Valid triggers validation based on annotations in Student entity (if any)
        boolean success = studentService.addStudent(student);
        return R.result(success);
    }

    /**
     * 修改学生
     */
    @PutMapping
    @PreAuthorize("hasAuthority('resource:student:edit')")
    @Log(title = "学生管理", businessType = BusinessType.UPDATE)
    public R<Void> edit(@Valid @RequestBody Student student) {
        // @Valid triggers validation
        if (student.getId() == null || student.getId() <= 0) {
            return R.fail("更新学生时必须提供有效的ID");
        }
        boolean success = studentService.updateStudent(student);
        return R.result(success);
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('resource:student:delete')")
    @Log(title = "学生管理", businessType = BusinessType.DELETE)
    public R<Void> remove(@NotNull @Min(value = 1, message = "ID必须为正整数") @PathVariable Long id) {
        boolean success = studentService.deleteStudentLogically(id);
        return R.result(success);
    }

    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('resource:student:delete')")
    @Log(title = "学生管理", businessType = BusinessType.DELETE)
    public R<Void> removeBatch(@NotEmpty(message = "删除ID列表不能为空") @RequestBody List<@NotNull @Min(value = 1, message = "ID必须为正整数") Long> ids) {
        boolean success = studentService.deleteStudentsLogicallyBatch(ids);
        return R.result(success);
    }

    /**
     * 导出学生数据
     */
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('resource:student:export')")
    @Log(title = "学生管理", businessType = BusinessType.EXPORT)
    public void export(@RequestBody(required = false) StudentQuery query, HttpServletResponse response) {
        // Allow query to be optional or empty for exporting all
        if (query == null) {
             query = new StudentQuery(); // Create default query if none provided
        }
        List<Student> list = studentService.listStudentsForExport(query);
        try {
             // TODO: Create a StudentExportVo class with @ExcelProperty if needed (e.g., for class name)
            ExcelUtil.exportExcel(response, "学生数据.xlsx", "学生列表", Student.class, list);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.err.println("导出学生数据失败: " + e.getMessage());
        }
    }

    /**
     * 导入学生数据
     */
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('resource:student:import')")
    @Log(title = "学生管理", businessType = BusinessType.IMPORT)
    public R<String> importData(@NotNull(message = "上传文件不能为空") @RequestPart("file") MultipartFile file) {
        try {
            String resultMsg = studentService.importStudents(file);
            // Check result message for success/failure indication
            if (resultMsg.contains("失败") || resultMsg.contains("error")) { // Basic check
                 return R.fail(resultMsg);
            } else {
                return R.success(resultMsg);
            }
        } catch (IllegalArgumentException e) {
             // Catch validation exceptions specifically
             return R.fail(e.getMessage()); // Return validation error messages
        } catch (Exception e) {
             System.err.println("导入学生数据时发生未预期错误: " + e.getMessage());
             e.printStackTrace();
             return R.fail("导入失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生班级变更历史
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAuthority('resource:student:view')")
    public R<List<StudentClazzHistoryDto>> getHistory(@NotNull @Min(value = 1, message = "学生ID必须为正整数") @PathVariable Long id) {
        List<StudentClazzHistoryDto> historyList = studentService.getClassChangeHistory(id);
        return R.success(historyList);
    }

} 