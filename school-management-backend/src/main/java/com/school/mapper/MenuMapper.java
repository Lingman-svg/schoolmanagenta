/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 15:46:20
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 20:05:18
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\mapper\MenuMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单权限表 Mapper 接口
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户ID查询菜单权限列表 (后续权限管理实现)
     * @param userId 用户ID
     * @return 菜单列表
     */
    // List<Menu> selectMenusByUserId(Long userId);

    /**
     * 根据用户ID查询权限标识列表 (后续权限管理实现)
     * @param userId 用户ID
     * @return 权限标识列表
     */
    // List<String> selectPermsByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID列表查询菜单列表 (用于权限加载)
     *
     * @param roleIds 角色ID列表
     * @return 菜单列表 (包含 perms 字段)
     */
    List<Menu> selectMenusByRoleIds(@Param("roleIds") List<Long> roleIds);

     /**
     * 查询系统菜单列表 (用于构建完整菜单树)
     *
     * @param menu 查询条件封装 (例如按菜单名称、状态查询)
     * @return 菜单列表
     */
    // List<Menu> selectMenuList(Menu menu);

    // 可以根据需要添加更多自定义查询方法
    // 例如：查询指定父ID下的子菜单
    // List<Menu> selectChildrenMenuByParentId(Long parentId);
} 