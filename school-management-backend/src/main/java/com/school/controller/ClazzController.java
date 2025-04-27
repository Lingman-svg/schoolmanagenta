/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 00:28:40
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:29:17
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\controller\ClazzController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.annotation.Log;
import com.school.constant.BusinessType;
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
@RequestMapping("/resource/classes")
@RequiredArgsConstructor
@Slf4j
public class ClazzController {

    private final ClazzService clazzService;

    /**
     * 分页查询班级列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('resource:class:list')")
    public R<Page<Clazz>> list(ClazzQuery query) {
        Page<Clazz> page = clazzService.listClazzes(query);
        return R.success(page);
    }

    /**
     * 根据ID获取班级详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('resource:class:view')")
    public R<Clazz> getInfo(@PathVariable Long id) {
        Clazz clazz = clazzService.getClazzInfo(id);
        return R.success(clazz);
    }

    /**
     * 新增班级
     */
    @PostMapping
    @PreAuthorize("hasAuthority('resource:class:add')")
    @Log(title = "班级管理", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody Clazz clazz) {
        boolean success = clazzService.addClazz(clazz);
        return R.result(success);
    }

    /**
     * 修改班级
     */
    @PutMapping
    @PreAuthorize("hasAuthority('resource:class:edit')")
    @Log(title = "班级管理", businessType = BusinessType.UPDATE)
    public R<Void> edit(@RequestBody Clazz clazz) {
        if (clazz.getId() == null) {
           return R.fail("更新操作必须提供班级ID");
        }
        boolean success = clazzService.updateClazz(clazz);
        return R.result(success);
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('resource:class:delete')")
    @Log(title = "班级管理", businessType = BusinessType.DELETE)
    public R<Void> remove(@PathVariable Long id) {
        boolean success = clazzService.deleteClazzLogically(id);
        return R.result(success);
    }

    /**
     * 批量删除班级
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('resource:class:delete')")
    @Log(title = "班级管理", businessType = BusinessType.DELETE)
    public R<Void> removeBatch(@RequestBody List<Long> ids) {
         if (ids == null || ids.isEmpty()) {
            return R.fail("批量删除时ID列表不能为空");
        }
        boolean success = clazzService.deleteClazzesLogicallyBatch(ids);
        return R.result(success);
    }

    /**
     * 导出班级数据
     */
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('resource:class:export')")
    @Log(title = "班级管理", businessType = BusinessType.EXPORT)
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
    @PreAuthorize("hasAuthority('resource:class:import')")
    @Log(title = "班级管理", businessType = BusinessType.IMPORT)
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
    @PreAuthorize("hasAuthority('resource:class:list')")
    public R<List<Clazz>> listValid() {
        log.info("获取所有有效班级列表");
        List<Clazz> list = clazzService.listValidClasses();
        return R.success(list);
    }

} 