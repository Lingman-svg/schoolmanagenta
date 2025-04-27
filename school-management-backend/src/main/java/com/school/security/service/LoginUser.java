/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 19:57:50
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 19:58:28
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\security\service\LoginUser.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.security.service;

import com.school.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 需要的 UserDetails 实现类
 */
@Getter // 使用 Lombok 简化 getter
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final User user;

    // 权限列表 (可以从 UserVo 或其他地方获取)
    // 这里暂时存储角色名称，可以根据需要存储权限标识符
    private final Set<String> permissions;

    public LoginUser(User user, Set<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将权限标识字符串集合转换为 GrantedAuthority 集合
        // 角色标识已经包含了 "ROLE_" 前缀
        return permissions.stream()
                .map(SimpleGrantedAuthority::new) // 直接使用权限标识创建
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // 返回数据库中存储的加密密码
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    /**
     * 账户是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 暂不实现账户过期逻辑
    }

    /**
     * 账户是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 暂不实现账户锁定逻辑
    }

    /**
     * 凭证是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 暂不实现凭证过期逻辑
    }

    /**
     * 账户是否启用
     */
    @Override
    public boolean isEnabled() {
        return user.getIsValid() != null && user.getIsValid() == 0; // 假设 0 为启用
    }

    // 可以添加获取原始 User 对象的方法
    public User getUser() {
        return user;
    }
} 