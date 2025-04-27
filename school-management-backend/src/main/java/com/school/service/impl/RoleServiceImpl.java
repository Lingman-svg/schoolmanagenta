package com.school.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.Role;
import com.school.entity.RoleMenu;
import com.school.mapper.RoleMapper;
import com.school.mapper.RoleMenuMapper;
import com.school.mapper.UserRoleMapper; // 需要创建 UserRoleMapper
import com.school.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色信息表 服务实现类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    // 假设 "admin" 为超级管理员角色 Key，不允许操作
    private static final String ADMIN_ROLE_KEY = "admin";

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper; // 用于检查角色是否被用户关联

    @Override
    public List<Role> selectRolesByUserId(Long userId) {
        // 如果用户是管理员，返回所有角色 (简化逻辑，实际应根据权限)
        // if (SecurityUtils.isAdmin(userId)) {
        //     return baseMapper.selectList(new LambdaQueryWrapper<Role>().orderByAsc(Role::getRoleSort));
        // }
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    public Set<String> selectRoleKeysByUserId(Long userId) {
        List<String> perms = baseMapper.selectRoleKeysByUserId(userId);
        return perms.stream().filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
    }

    @Override
    public List<Role> selectRoleAll() {
        // 通常需要按 roleSort 排序
        return baseMapper.selectList(new LambdaQueryWrapper<Role>().orderByAsc(Role::getRoleSort));
    }

    @Override
    public Role selectRoleById(Long roleId) {
        return baseMapper.selectById(roleId);
    }

    @Override
    public boolean checkRoleNameUnique(Role role) {
        Long roleId = Optional.ofNullable(role.getId()).orElse(-1L);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, role.getRoleName());
        Role info = baseMapper.selectOne(wrapper);
        return (info == null || Objects.equals(info.getId(), roleId));
    }

    @Override
    public boolean checkRoleKeyUnique(Role role) {
        Long roleId = Optional.ofNullable(role.getId()).orElse(-1L);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, role.getRoleKey());
        Role info = baseMapper.selectOne(wrapper);
        return (info == null || Objects.equals(info.getId(), roleId));
    }

    @Override
    public void checkRoleAllowed(Role role) {
        if (role.getId() != null && isAdminRole(role.getId())) {
            throw new RuntimeException("不允许操作超级管理员角色");
        }
         // 或者检查 roleKey
        // if (ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
        //    throw new RuntimeException("不允许操作超级管理员角色");
        // }
    }

    /**
     * 判断是否为管理员角色 (简化判断，假设 ID=1 或 Key=admin)
     */
    private boolean isAdminRole(Long roleId) {
        // 在实际应用中，可能需要更可靠的方式来识别管理员角色
        return roleId != null && roleId == 1L;
    }

    @Override
    public int countUserRoleByRoleId(Long roleId) {
        // return userRoleMapper.countUserRoleByRoleId(roleId);
         // TODO: 待 UserRoleMapper 实现后完善
         return 0;
    }

    @Override
    @Transactional
    public boolean insertRole(Role role) {
        if (!checkRoleNameUnique(role)) {
            throw new RuntimeException("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!checkRoleKeyUnique(role)) {
            throw new RuntimeException("新增角色'" + role.getRoleName() + "'失败，权限字符已存在");
        }

        // 1. 新增角色信息
        baseMapper.insert(role);
        // 2. 新增角色与菜单关联信息
        return insertRoleMenu(role);
    }

    @Override
    @Transactional
    public boolean updateRole(Role role) {
        checkRoleAllowed(role);
        if (!checkRoleNameUnique(role)) {
            throw new RuntimeException("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        if (!checkRoleKeyUnique(role)) {
            throw new RuntimeException("修改角色'" + role.getRoleName() + "'失败，权限字符已存在");
        }

        // 1. 修改角色信息
        baseMapper.updateById(role);
        // 2. 删除旧的角色与菜单关联信息
        roleMenuMapper.deleteRoleMenuByRoleId(role.getId());
        // 3. 新增新的角色与菜单关联信息
        return insertRoleMenu(role);
    }

    @Override
    @Transactional // 只需要更新状态，也要考虑是否允许操作 admin
    public boolean updateRoleStatus(Role role) {
         checkRoleAllowed(role); // 检查是否允许操作
         // 仅更新 isValid 字段
         Role updateRole = new Role();
         updateRole.setId(role.getId());
         updateRole.setIsValid(role.getIsValid());
        return baseMapper.updateById(updateRole) > 0;
    }

     /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    private boolean insertRoleMenu(Role role) {
        int rows = 1;
        // 新增用户与角色管理
        Long[] menus = role.getMenuIds();
        if (menus != null && menus.length > 0) {
            // 新增角色与菜单管理
            List<RoleMenu> list = new ArrayList<>();
            for (Long menuId : menus) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                list.add(rm);
            }
            if (CollUtil.isNotEmpty(list)) { // 使用 Hutool CollUtil
                rows = roleMenuMapper.batchRoleMenu(list);
            }
        }
        return rows > 0;
    }

    @Override
    @Transactional
    public boolean deleteRoleByIds(Long[] roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            return true;
        }
        for (Long roleId : roleIds) {
             Role role = selectRoleById(roleId);
             if(role == null) continue; // 如果角色不存在，跳过
            checkRoleAllowed(role); // 检查是否允许删除
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new RuntimeException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        // 1. 删除角色与菜单的关联
        for (Long roleId : roleIds) {
             roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        }

        // 2. 删除角色本身 (逻辑删除或物理删除取决于 BaseEntity 配置)
        int rows = baseMapper.deleteBatchIds(Arrays.asList(roleIds));
        return rows > 0;
    }

    // --- 用户角色授权相关方法 (需要 UserRoleService) ---
    // @Override
    // public boolean deleteAuthUser(Long userId, Long roleId) { ... }
    // @Override
    // public boolean deleteAuthUsers(Long roleId, Long[] userIds) { ... }
    // @Override
    // public int insertAuthUsers(Long roleId, Long[] userIds) { ... }
} 