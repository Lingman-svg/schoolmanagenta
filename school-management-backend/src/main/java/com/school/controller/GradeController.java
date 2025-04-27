/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 16:10:22
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 16:26:26
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\controller\GradeController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Grade;
import com.school.entity.dto.GradeDto;
import com.school.entity.query.GradeQuery;
import com.school.entity.vo.GradeExcelVo;
import com.school.service.GradeService;
import com.school.utils.ExcelUtil;
import com.school.utils.R; // 引入统一返回结果类
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize; // 导入
import org.springframework.web.bind.annotation.*; // 引入必要的注解
import org.springframework.web.multipart.MultipartFile; // Import MultipartFile

import java.util.Collections; // Import Collections
import java.util.List; // Import List

/**
 * 成绩管理 前端控制器
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Slf4j
@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    /**
     * 分页查询成绩列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('base:grade:list') or hasAuthority('base:grade:view')")
    public R<Page<GradeDto>> listGrades(GradeQuery query) {
        log.info("查询成绩列表，参数: {}", query);
        Page<GradeDto> pageResult = gradeService.listGrades(query);
        return R.success(pageResult);
    }

    /**
     * 获取成绩详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('base:grade:view')")
    public R<Grade> getGradeInfo(@PathVariable Long id) {
        log.info("获取成绩详情，ID: {}", id);
        Grade grade = gradeService.getGradeInfo(id);
        if (grade != null) {
            return R.success(grade);
        } else {
            return R.fail("未找到指定ID的成绩记录");
        }
    }

    /**
     * 新增成绩
     */
    @PostMapping
    @PreAuthorize("hasAuthority('base:grade:add')")
    public R<Void> addGrade(@RequestBody Grade grade) { // POST 请求体接收 Grade 对象
        log.info("新增成绩: {}", grade);
        // 在 Service 层已有校验
        boolean success = gradeService.addGrade(grade);
        return success ? R.success() : R.fail("新增成绩失败");
    }

    /**
     * 修改成绩
     */
    @PutMapping
    @PreAuthorize("hasAuthority('base:grade:edit')")
    public R<Void> updateGrade(@RequestBody Grade grade) { // PUT 请求体接收 Grade 对象
        log.info("修改成绩: {}", grade);
        // 在 Service 层已有校验
        boolean success = gradeService.updateGrade(grade);
        return success ? R.success() : R.fail("修改成绩失败");
    }

    /**
     * 删除成绩
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('base:grade:delete')")
    public R<Void> deleteGrade(@PathVariable Long id) {
        log.info("删除成绩，ID: {}", id);
        boolean success = gradeService.deleteGrade(id);
        return success ? R.success() : R.fail("删除成绩失败");
    }

    /**
     * 批量删除成绩
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('base:grade:delete')")
    public R<Void> deleteGradesBatch(@RequestBody List<Long> ids) { // DELETE 请求体接收 ID 列表
        log.info("批量删除成绩，IDs: {}", ids);
        boolean success = gradeService.deleteGradesBatch(ids);
        return success ? R.success() : R.fail("批量删除成绩失败");
    }

    /**
     * 下载成绩导入模板
     */
    @GetMapping("/template")
    @PreAuthorize("hasAuthority('base:grade:import') or hasAuthority('base:grade:list')")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            // 使用 GradeExcelVo 生成模板
            ExcelUtil.exportExcel(response, "成绩导入模板", "成绩数据", GradeExcelVo.class, Collections.emptyList());
        } catch (Exception e) {
            log.error("下载成绩导入模板失败", e);
            // Consider setting response status to indicate error
             response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 导入成绩数据
     */
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('base:grade:import') or hasAuthority('base:grade:add') or hasAuthority('base:grade:edit')")
    public R<String> importGrades(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        try {
            String resultMsg = gradeService.importGrades(file);
            // Service 层会在失败时抛出异常
            return R.success(resultMsg);
        } catch (IllegalArgumentException e) {
             log.warn("成绩导入校验失败: {}", e.getMessage());
             return R.fail(e.getMessage()); // 返回校验失败信息给前端
        } catch (Exception e) {
            log.error("导入成绩数据时发生未知错误", e);
            return R.fail("导入失败，请检查文件格式或联系管理员。");
        }
    }

    /**
     * 导出成绩数据
     */
    @PostMapping("/export") // Use POST to allow query body
    @PreAuthorize("hasAuthority('base:grade:export') or hasAuthority('base:grade:list')")
    public void exportGrades(HttpServletResponse response, @RequestBody GradeQuery query) {
         // Note: Query might be null if frontend sends empty body for exporting all
        log.info("导出成绩数据，查询条件: {}", query);
        try {
            gradeService.exportGrades(response, query);
        } catch (Exception e) {
            log.error("导出成绩数据失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Optionally write error message to response body
            // response.getWriter().write("导出失败: " + e.getMessage());
        }
    }
} 