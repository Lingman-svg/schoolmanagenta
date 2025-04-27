/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 15:48:52
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 16:01:41
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\mapper\RoleMenuMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色和菜单关联表 Mapper 接口
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> { // 让它继承 BaseMapper<RoleMenu> 以便使用 MP 功能

    /**
     * 检查菜单是否存在角色关联
     * @param menuId 菜单ID
     * @return 关联的角色数量
     */
    int checkMenuExistRole(@Param("menuId") Long menuId);

    /**
     * 根据菜单ID删除角色菜单关联
     * @param menuId 菜单ID
     * @return 删除的行数
     */
    int deleteRoleMenuByMenuId(@Param("menuId") Long menuId);

     /**
     * 根据角色ID删除角色菜单关联信息
     * @param roleId 角色ID
     * @return 影响的行数
     */
    int deleteRoleMenuByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量新增角色菜单信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    int batchRoleMenu(@Param("list") List<RoleMenu> roleMenuList);

    /**
     * 查询指定角色关联的菜单数量
     * @param roleId 角色ID
     * @return 菜单数量
     */
    // int countMenuByRoleId(@Param("roleId") Long roleId); // 可以通过 selectCount 实现

} 