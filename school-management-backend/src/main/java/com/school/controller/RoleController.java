package com.school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage; // 用于分页
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // MP 分页
import com.school.entity.Role;
import com.school.entity.query.RoleQuery; // 假设有 RoleQuery 用于分页和条件查询
import com.school.service.RoleService;
import com.school.utils.R;
import com.school.annotation.Log; // Import Log annotation
import com.school.constant.BusinessType; // Import BusinessType constants
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // 导入
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息 前端控制器
 *
 * @author Gemini
 * @since 2024-05-14
 */
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表 (分页)
     * @param query 查询条件 (roleName, roleKey, status, dateRange) + 分页参数
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')") // Corrected permission key
    public R<IPage<Role>> list(RoleQuery query) { // 使用 RoleQuery 接收查询参数
        Page<Role> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<Role> rolePage = roleService.page(page, query.buildQueryWrapper()); // 假设 RoleQuery 有 buildQueryWrapper 方法
        return R.success(rolePage);
    }

    /**
     * 根据角色编号获取详细信息
     * @param roleId 角色ID
     */
    @GetMapping(value = "/{roleId}")
    @PreAuthorize("hasAuthority('system:role:view')") // Corrected permission key
    public R<Role> getInfo(@PathVariable Long roleId) {
        Role role = roleService.selectRoleById(roleId);
        return R.success(role);
    }

    /**
     * 新增角色
     * @param role 角色实体 (包含 menuIds)
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')") // Corrected permission key
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody Role role) {
        // 可以在 Controller 层校验角色权限
        // checkRolePermission();
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，权限字符已存在");
        }
        boolean success = roleService.insertRole(role);
        return success ? R.success() : R.fail("新增角色失败");
    }

    /**
     * 修改保存角色
     * @param role 角色实体 (包含 menuIds)
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')") // Corrected permission key
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    public R<Void> edit(@RequestBody Role role) {
        // 可以在 Controller 层校验角色权限
        // checkRolePermission();
        roleService.checkRoleAllowed(role); // 检查是否允许操作 admin
        if (!roleService.checkRoleNameUnique(role)) {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!roleService.checkRoleKeyUnique(role)) {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，权限字符已存在");
        }
        boolean success = roleService.updateRole(role);
        return success ? R.success() : R.fail("修改角色失败");
    }

    /**
     * 修改保存数据权限 (如果需要单独的接口)
     * @param role 角色实体 (只包含 id 和 dataScope/deptIds)
     */
    // @PutMapping("/dataScope")
    // @PreAuthorize("hasAuthority('system:role:edit')") // 假设需要编辑权限
    // public R<Void> dataScope(@RequestBody Role role) {
    //     roleService.checkRoleAllowed(role);
    //     // ... 调用 service 更新数据权限
    //     return R.success();
    // }

    /**
     * 状态修改
     * @param role 角色实体 (包含 id 和 isValid)
     */
    @PutMapping("/changeStatus")
    @PreAuthorize("hasAuthority('system:role:edit')") // Corrected permission key
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    public R<Void> changeStatus(@RequestBody Role role) {
        // 可以在 Controller 层校验角色权限
        // checkRolePermission();
        roleService.checkRoleAllowed(role); // 检查是否允许操作 admin
        boolean success = roleService.updateRoleStatus(role);
        return success ? R.success() : R.fail("修改状态失败");
    }

    /**
     * 删除角色
     * @param roleIds 角色ID数组
     */
    @DeleteMapping("/{roleIds}")
    @PreAuthorize("hasAuthority('system:role:delete')") // Corrected permission key
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    public R<Void> remove(@PathVariable Long[] roleIds) {
        // 可以在 Controller 层校验角色权限
        // checkRolePermission();
        boolean success = roleService.deleteRoleByIds(roleIds);
        return success ? R.success() : R.fail("删除角色失败");
    }

    /**
     * 获取角色选择框列表 (获取所有角色)
     * 通常需要查看角色或分配用户的权限
     */
    @GetMapping("/optionselect")
    @PreAuthorize("hasAuthority('system:role:list') or hasAuthority('system:user:edit')") // Corrected permission keys
    public R<List<Role>> optionselect() {
        return R.success(roleService.selectRoleAll());
    }

    // --- 用户分配角色相关接口 (通常放在用户管理或专门的授权 Controller) ---
    /**
     * 查询已分配用户角色列表
     */
    // @GetMapping("/authUser/allocatedList")
    // public R<?> allocatedList(...) { ... }

    /**
     * 查询未分配用户角色列表
     */
    // @GetMapping("/authUser/unallocatedList")
    // public R<?> unallocatedList(...) { ... }

    /**
     * 取消用户授权角色
     */
    // @PutMapping("/authUser/cancel")
    // public R<?> cancelAuthUser(...) { ... }

    /**
     * 批量取消用户授权角色
     */
    // @PutMapping("/authUser/cancelAll")
    // public R<?> cancelAuthUserAll(...) { ... }

    /**
     * 批量选择用户授权
     */
    // @PutMapping("/authUser/selectAll")
    // public R<?> selectAuthUserAll(...) { ... }
} 