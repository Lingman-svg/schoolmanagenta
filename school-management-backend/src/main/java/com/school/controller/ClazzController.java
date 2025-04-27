/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 00:28:40
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 21:54:48
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\controller\ClazzController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Clazz;
import com.school.entity.query.ClazzQuery;
import com.school.service.ClazzService;
import com.school.utils.ExcelUtil;
import com.school.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 班级管理 前端控制器
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@RestController
@RequestMapping("/clazz")
@RequiredArgsConstructor
@Slf4j
public class ClazzController {

    private final ClazzService clazzService;

    /**
     * 分页查询班级列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('base:class:list') or hasAuthority('base:class:view')")
    public R<Page<Clazz>> list(ClazzQuery query) { // Changed return type to R
        Page<Clazz> page = clazzService.listClazzes(query);
        return R.success(page); // Use R.success(data)
    }

    /**
     * 根据ID获取班级详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('base:class:view')")
    public R<Clazz> getInfo(@PathVariable Long id) { // Changed return type to R
        Clazz clazz = clazzService.getClazzInfo(id);
        // Consider returning R.fail if clazz is null?
        return R.success(clazz); // Use R.success(data)
    }

    /**
     * 新增班级
     */
    @PostMapping
    @PreAuthorize("hasAuthority('base:class:add')")
    public R<Void> add(@RequestBody Clazz clazz) { // Changed return type to R
        boolean success = clazzService.addClazz(clazz);
        return R.result(success); // Use R.result(boolean)
    }

    /**
     * 修改班级
     */
    @PutMapping
    @PreAuthorize("hasAuthority('base:class:edit')")
    public R<Void> edit(@RequestBody Clazz clazz) { // Changed return type to R
        // Basic validation: ensure ID is present for update
        if (clazz.getId() == null) {
           return R.fail("更新操作必须提供班级ID"); // Use R.fail(msg)
        }
        boolean success = clazzService.updateClazz(clazz);
        return R.result(success); // Use R.result(boolean)
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('base:class:delete')")
    public R<Void> remove(@PathVariable Long id) { // Changed return type to R
        boolean success = clazzService.deleteClazzLogically(id);
        return R.result(success); // Use R.result(boolean)
    }

    /**
     * 批量删除班级
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('base:class:delete')")
    public R<Void> removeBatch(@RequestBody List<Long> ids) { // Changed return type to R
         if (ids == null || ids.isEmpty()) {
            return R.fail("批量删除时ID列表不能为空"); // Use R.fail(msg)
        }
        boolean success = clazzService.deleteClazzesLogicallyBatch(ids);
        return R.result(success); // Use R.result(boolean)
    }

    /**
     * 导出班级数据
     */
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('base:class:export') or hasAuthority('base:class:list')")
    public void exportClazzes(@RequestBody ClazzQuery query, HttpServletResponse response) {
        List<Clazz> list = clazzService.listClazzesForExport(query);
        try {
            ExcelUtil.exportExcel(response, "班级数据.xlsx", "班级列表", Clazz.class, list);
        } catch (IOException e) {
            System.err.println("Error exporting Clazz data: " + e.getMessage());
        }
    }

    /**
     * 导入班级数据
     */
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('base:class:import') or hasAuthority('base:class:add') or hasAuthority('base:class:edit')")
    public R<Void> importClazzes(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("上传的文件不能为空");
        }
        try {
            clazzService.importClazzes(file);
            return R.success("班级数据导入成功");
        } catch (Exception e) {
            System.err.println("Error importing Clazz data: " + e.getMessage());
            e.printStackTrace();
            return R.fail("导入失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取所有有效班级列表 (用于下拉)")
    @GetMapping("/valid")
    public R<List<Clazz>> listValid() {
        log.info("获取所有有效班级列表");
        List<Clazz> list = clazzService.listValidClasses();
        return R.success(list);
    }

} 