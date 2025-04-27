package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Role;

import java.util.List;
import java.util.Set;

/**
 * 角色信息表 服务类
 *
 * @author Gemini
 * @since 2024-05-14
 */
public interface RoleService extends IService<Role> {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(Long userId);

     /**
     * 根据用户ID查询角色权限字符串集合
     *
     * @param userId 用户ID
     * @return 权限集合 (e.g. {"admin", "teacher"})
     */
    Set<String> selectRoleKeysByUserId(Long userId);

    /**
     * 查询所有角色列表 (通常用于给用户分配角色时)
     *
     * @return 角色列表
     */
    List<Role> selectRoleAll();

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    Role selectRoleById(Long roleId);


    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果 true 唯一 false 不唯一
     */
    boolean checkRoleNameUnique(Role role);

    /**
     * 校验角色权限字符串是否唯一
     *
     * @param role 角色信息
     * @return 结果 true 唯一 false 不唯一
     */
    boolean checkRoleKeyUnique(Role role);

    /**
     * 检查角色是否允许被操作 (例如 admin 角色通常不允许修改或删除)
     *
     * @param role 角色信息
     */
    void checkRoleAllowed(Role role);

     /**
     * 通过角色ID查询角色使用数量 (检查是否被用户关联)
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean insertRole(Role role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    boolean updateRole(Role role);

    /**
     * 修改角色状态
     *
     * @param role 角色信息 (包含 id 和 isValid)
     * @return 结果
     */
    boolean updateRoleStatus(Role role);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     * @throws RuntimeException 删除过程中的异常
     */
    boolean deleteRoleByIds(Long[] roleIds);

     /**
     * 取消用户角色授权
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 结果
     */
    // boolean deleteAuthUser(Long userId, Long roleId); // 需 UserRoleService

    /**
     * 批量取消用户角色授权
     *
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户ID数组
     * @return 结果
     */
    // boolean deleteAuthUsers(Long roleId, Long[] userIds); // 需 UserRoleService

     /**
     * 批量选择用户授权
     *
     * @param roleId 角色ID
     * @param userIds 需要授权的用户ID数组
     * @return 结果
     */
    // int insertAuthUsers(Long roleId, Long[] userIds); // 需 UserRoleService

} 