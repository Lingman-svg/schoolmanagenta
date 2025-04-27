package com.school.controller;

import com.school.annotation.Log;
import com.school.constant.BusinessType;
import com.school.entity.Menu;
import com.school.entity.vo.TreeSelect;
import com.school.service.MenuService;
import com.school.utils.R; // 假设 R 是统一响应类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // 导入
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单信息 前端控制器
 *
 * @author Gemini
 * @since 2024-05-14
 */
@RestController
@RequestMapping("/system/menu") // 统一使用 /system 前缀
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取菜单列表 (树形)
     * @param menu 查询条件 (menuName, isValid/status)
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public R<List<Menu>> list(Menu menu) {
        List<Menu> menus = menuService.selectMenuList(menu, null); // 暂时不按用户过滤
        // 注意：selectMenuList 返回的是扁平列表，前端可能需要树形结构
        // 如果前端需要直接获得树，这里应该调用 buildMenuTree
        // List<MenuNode> menuTree = menuService.buildMenuTree(menus);
        // return R.success(menuTree);
        // --- 或者，如果前端自行构建树 --- (当前实现)
        return R.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     * @param menuId 菜单ID
     */
    @GetMapping(value = "/{menuId}")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public R<Menu> getInfo(@PathVariable Long menuId) {
        return R.success(menuService.getById(menuId));
    }

    /**
     * 获取菜单下拉树列表 (TreeSelect)
     * 通常需要查看菜单或编辑角色的权限
     */
    @GetMapping("/treeselect")
    @PreAuthorize("hasAuthority('system:menu:list') or hasAuthority('system:role:edit')")
    public R<List<TreeSelect>> treeselect(Menu menu) {
        List<Menu> menus = menuService.selectMenuList(menu, null); // 获取扁平列表
        List<TreeSelect> treeSelects = menuService.buildMenuTreeSelect(menus); // 构建下拉树
        return R.success(treeSelects);
    }

    /**
     * 加载对应角色菜单列表树
     * 需要查看菜单或编辑角色的权限
     * @param roleId 角色ID
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    @PreAuthorize("hasAuthority('system:menu:list') or hasAuthority('system:role:edit')")
    public R<Map<String, Object>> roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        List<Menu> menus = menuService.selectMenuList(new Menu(), null); // 获取所有菜单
        List<TreeSelect> treeSelects = menuService.buildMenuTreeSelect(menus);
        List<Long> checkedKeys = menuService.selectMenuListByRoleId(roleId);

        Map<String, Object> result = new HashMap<>();
        result.put("checkedKeys", checkedKeys);
        result.put("menus", treeSelects);
        return R.success(result);
    }

    /**
     * 新增菜单
     * @param menu 菜单实体
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody Menu menu) {
        // Controller 层可以做一些基本校验，但核心校验在 Service 完成
        boolean success = menuService.insertMenu(menu);
        return success ? R.success() : R.fail("新增菜单失败");
    }

    /**
     * 修改菜单
     * @param menu 菜单实体
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    public R<Void> edit(@RequestBody Menu menu) {
        boolean success = menuService.updateMenu(menu);
        return success ? R.success() : R.fail("修改菜单失败");
    }

    /**
     * 删除菜单
     * @param menuId 菜单ID
     */
    @DeleteMapping("/{menuId}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    public R<Void> remove(@PathVariable("menuId") Long menuId) {
        boolean success = menuService.deleteMenuById(menuId);
        return success ? R.success() : R.fail("删除菜单失败");
    }
} 