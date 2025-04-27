package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Menu;
import com.school.entity.dto.MenuNode;
import com.school.entity.vo.TreeSelect;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限表 服务类
 *
 * @author Gemini
 * @since 2024-05-14
 */
public interface MenuService extends IService<Menu> {

    /**
     * 根据用户ID查询菜单权限列表
     * @param userId 用户ID
     * @return 菜单列表 (树形结构)
     */
    List<MenuNode> selectMenuTreeByUserId(Long userId);

    /**
     * 根据用户ID查询权限标识集合
     * @param userId 用户ID
     * @return 权限标识集合 (e.g., ["system:user:list", "system:role:add"])
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 查询系统菜单列表
     *
     * @param menu 查询条件 (例如按名称、状态)
     * @param userId (可选) 根据用户ID过滤 (暂不实现)
     * @return 菜单列表 (树形结构)
     */
    List<Menu> selectMenuList(Menu menu, Long userId);

    /**
     * 构建前端所需的路由列表
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    List<MenuNode> buildMenuTree(List<Menu> menus);

     /**
     * 构建前端下拉树结构
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildMenuTreeSelect(List<Menu> menus);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    boolean insertMenu(Menu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    boolean updateMenu(Menu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    boolean deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果 true 唯一 false 不唯一
     */
    boolean checkMenuNameUnique(Menu menu);

     /**
     * 校验菜单路径是否唯一 (对于菜单和目录)
     *
     * @param menu 菜单信息
     * @return 结果 true 唯一 false 不唯一
     */
    boolean checkPathUnique(Menu menu);

     /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色关联
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkMenuExistRole(Long menuId);

    // 可能需要的其他方法，例如获取所有权限标识等
} 