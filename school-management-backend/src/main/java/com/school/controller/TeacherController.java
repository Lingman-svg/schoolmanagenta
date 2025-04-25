package com.school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.entity.Teacher;
import com.school.entity.query.TeacherQuery;
import com.school.service.TeacherService;
import com.school.utils.R;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 教师管理 Controller
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
@Validated
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 获取教师分页列表
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public R<IPage<Teacher>> list(TeacherQuery query) {
        IPage<Teacher> page = teacherService.findTeachersByPage(query);
        return R.success(page);
    }

    /**
     * 根据 ID 获取教师详情
     * @param id 教师ID
     * @return 教师详情
     */
    @GetMapping("/{id}")
    public R<Teacher> getInfo(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherDetailById(id);
        return R.result(teacher != null, teacher);
    }

    /**
     * 新增教师
     * @param teacher 教师信息 (包含 subjectIds)
     * @return 操作结果
     */
    @PostMapping
    public R<Void> add(@Validated @RequestBody Teacher teacher) {
        boolean success = teacherService.addTeacher(teacher);
        return R.result(success);
    }

    /**
     * 修改教师
     * @param teacher 教师信息 (包含 subjectIds)
     * @return 操作结果
     */
    @PutMapping
    public R<Void> edit(@Validated @RequestBody Teacher teacher) {
        if (teacher.getId() == null) {
            return R.fail("修改失败：教师ID不能为空");
        }
        boolean success = teacherService.updateTeacher(teacher);
        return R.result(success);
    }

    /**
     * 删除教师
     * @param id 教师ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        boolean success = teacherService.deleteTeacherById(id);
        return R.result(success);
    }

    /**
     * 批量删除教师
     * @param ids ID列表 (以逗号分隔)
     * @return 操作结果
     */
    @DeleteMapping("/batch/{ids}")
    public R<Void> removeBatch(@PathVariable List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.fail("请选择要删除的教师");
        }
        boolean success = teacherService.deleteTeachersByIds(ids);
        return R.result(success);
    }

    /**
     * 获取所有有效教师列表 (用于下拉选择)
     */
    @GetMapping("/valid")
    public R<List<Teacher>> listValid() {
        List<Teacher> list = teacherService.listValidTeachersForSelection();
        return R.success(list);
    }

    /**
     * 导出教师数据
     * @param response HttpServletResponse
     * @param query 查询条件
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, TeacherQuery query) {
        try {
            teacherService.exportTeachers(response, query);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.err.println("导出教师数据失败: " + e.getMessage());
        }
    }

    /**
     * 导入教师数据
     * @param file Excel 文件
     * @return 导入结果信息
     */
    @PostMapping("/import")
    public R<String> importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        try {
            String resultMsg = teacherService.importTeachers(file);
            if (resultMsg.contains("失败")) {
                return R.fail(resultMsg);
            } else {
                return R.success(resultMsg);
            }
        } catch (IOException e) {
            return R.fail("导入教师数据失败: " + e.getMessage());
        } catch (Exception e) {
             return R.fail("导入失败: " + e.getMessage());
        }
    }
} 