package com.school.controller;

import com.school.annotation.Log;
import com.school.constant.BusinessType;
import com.school.entity.SystemConfig;
import com.school.service.SystemConfigService;
import com.school.utils.R; // 假设 R 是统一响应类
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统参数配置 前端控制器
 *
 * @author Gemini
 * @since 2024-05-14
 */
@RestController
@RequestMapping("/system/config") // 前端路由是 /config，注意统一
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取所有系统配置列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:config:list')")
    public R<List<SystemConfig>> list() {
        return R.success(systemConfigService.list());
    }

    /**
     * 根据参数键名获取参数值
     * @param configKey 参数键名
     */
    @GetMapping("/key/{configKey}")
    @PreAuthorize("hasAuthority('system:config:view')")
    public R<SystemConfig> getConfigByKey(@PathVariable String configKey) {
        SystemConfig config = systemConfigService.lambdaQuery()
                .eq(SystemConfig::getConfigKey, configKey)
                .one();
        if (config == null) {
            return R.fail("参数键名不存在: " + configKey);
        }
        return R.success(config);
    }

    /**
     * 根据ID获取配置详情
     * @param id 配置ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:view')")
    public R<SystemConfig> getConfigById(@PathVariable Long id) {
        SystemConfig config = systemConfigService.getById(id);
         if (config == null) {
            return R.fail("配置不存在: ID " + id);
        }
        return R.success(config);
    }


    /**
     * 新增系统配置
     * @param systemConfig 系统配置实体
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:config:add')")
    @Log(title = "系统配置", businessType = BusinessType.INSERT)
    public R<Void> add(@Valid @RequestBody SystemConfig systemConfig) {
         // 校验 Key 是否已存在
        long count = systemConfigService.lambdaQuery()
                .eq(SystemConfig::getConfigKey, systemConfig.getConfigKey())
                .count();
        if (count > 0) {
             return R.fail("参数键名已存在: " + systemConfig.getConfigKey());
        }
        boolean success = systemConfigService.save(systemConfig);
        return success ? R.success() : R.fail("新增配置失败");
    }

    /**
     * 修改系统配置
     * @param systemConfig 系统配置实体
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:config:edit')")
    @Log(title = "系统配置", businessType = BusinessType.UPDATE)
    public R<Void> update(@Valid @RequestBody SystemConfig systemConfig) {
        if (systemConfig.getId() == null) {
            return R.fail("更新配置失败：ID 不能为空");
        }
         // 检查原始配置是否存在
        SystemConfig existingConfig = systemConfigService.getById(systemConfig.getId());
        if (existingConfig == null) {
            return R.fail("更新配置失败：配置不存在");
        }
         // 检查修改后的 Key 是否与其他配置冲突 (排除自身)
         long count = systemConfigService.lambdaQuery()
                .eq(SystemConfig::getConfigKey, systemConfig.getConfigKey())
                .ne(SystemConfig::getId, systemConfig.getId()) // 排除自身
                .count();
        if (count > 0) {
             return R.fail("更新配置失败：参数键名 " + systemConfig.getConfigKey() + " 已被其他配置使用");
        }

        // 系统内置参数不允许修改 key
        if (existingConfig.getConfigType() != null && existingConfig.getConfigType() == 1 && !existingConfig.getConfigKey().equals(systemConfig.getConfigKey())) {
             return R.fail("系统内置参数不允许修改键名");
        }

        boolean success = systemConfigService.updateById(systemConfig);
        return success ? R.success() : R.fail("更新配置失败");
    }

    /**
     * 删除系统配置
     * @param id 配置ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:delete')")
    @Log(title = "系统配置", businessType = BusinessType.DELETE)
    public R<Void> delete(@PathVariable Long id) {
        SystemConfig config = systemConfigService.getById(id);
         if (config == null) {
            // 或者可以直接返回成功，表示幂等性
            return R.success(); // 或者 R.fail("配置不存在");
        }
        // 系统内置参数不允许删除
        if (config.getConfigType() != null && config.getConfigType() == 1) {
            return R.fail("系统内置参数不允许删除");
        }

        boolean success = systemConfigService.removeById(id);
        return success ? R.success() : R.fail("删除配置失败");
    }
} 