/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 19:58:13
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 20:00:31
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\security\service\UserDetailsServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.security.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.school.entity.Role;
import com.school.entity.User;
import com.school.entity.UserRole;
import com.school.mapper.RoleMapper;
import com.school.mapper.UserMapper;
import com.school.mapper.UserRoleMapper;
import com.school.mapper.MenuMapper;
import com.school.entity.Menu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserDetailsService 实现类
 * 用于从数据库加载用户信息以供 Spring Security 使用
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户信息
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUserName, username);
        User user = userMapper.selectOne(userWrapper);

        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        }
        // 2. 查询用户权限信息 (角色 + 菜单权限)
        Set<String> permissions = getUserPermissions(user);

        // 3. 创建 LoginUser 对象
        return new LoginUser(user, permissions);
    }

    /**
     * 根据用户对象查询权限集合 (角色标识 + 菜单权限标识)
     * @param user 用户对象
     * @return 权限标识 Set 集合
     */
    private Set<String> getUserPermissions(User user) {
        Set<String> permsSet = new HashSet<>();
        // 1. 管理员拥有所有权限
        if (user.isAdmin()) { // 假设 User 实体有 isAdmin() 方法判断是否为管理员 (例如 userId=1)
            permsSet.add("*:*:*"); // 使用通配符表示所有权限
        } else {
            // 2. 查询用户角色 ID 列表
            List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId())
            );
            if (!CollectionUtils.isEmpty(userRoles)) {
                List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
                
                // 3. 查询角色信息 (获取角色标识 roleKey)
                List<Role> roles = roleMapper.selectBatchIds(roleIds);
                if (!CollectionUtils.isEmpty(roles)) {
                    roles.stream()
                        .map(Role::getRoleKey) // 获取角色标识符，例如 "admin", "teacher"
                        .filter(StringUtils::hasText) // 过滤空的角色标识
                        .forEach(roleKey -> permsSet.add("ROLE_" + roleKey)); // 添加 "ROLE_" 前缀
                }

                // 4. 根据角色 ID 查询菜单权限标识 (perms)
                List<Menu> menus = menuMapper.selectMenusByRoleIds(roleIds); // 需要在 MenuMapper 中实现此方法
                 if (!CollectionUtils.isEmpty(menus)) {
                     menus.stream()
                         .map(Menu::getPerms) // 获取权限标识符，例如 "system:user:list"
                         .filter(StringUtils::hasText) // 过滤空的权限标识
                         .forEach(permsSet::add);
                 }
            }
        }
     * 根据用户ID查询角色名称列表
     * @param userId 用户ID
     * @return 角色名称列表
     */
    private List<String> getUserRoles(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        
        // 查询角色信息
        List<Role> roleList = roleMapper.selectBatchIds(roleIds);
        if (roleList.isEmpty()) {
            return new ArrayList<>();
        }
        // 返回角色名称列表 (或者角色标识符 roleKey)
        return roleList.stream().map(Role::getRoleName).collect(Collectors.toList());
        // return roleList.stream().map(Role::getRoleKey).collect(Collectors.toList());
    }
} 