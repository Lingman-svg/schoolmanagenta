/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 19:53:19
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:37:26
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\controller\LoginController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.controller;

import com.school.annotation.Log;
import com.school.constant.BusinessType;
import com.school.entity.dto.LoginDto;
import com.school.service.AuthService; // 假设我们创建一个 AuthService
import com.school.utils.R;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping; // 导入 GetMapping
import org.springframework.security.core.Authentication; // 导入 Authentication
import org.springframework.security.core.context.SecurityContextHolder; // 导入 SecurityContextHolder
import com.school.security.service.LoginUser; // 导入 LoginUser
import com.school.entity.User; // 导入 User 实体
import java.util.HashMap; // 用于构建返回数据
import java.util.Map; // 用于构建返回数据
import java.util.Set; // 用于权限集合
import java.util.stream.Collectors; // 用于权限转换

/**
 * 登录认证 Controller
 */
@RestController
public class LoginController {

    @Autowired
    private AuthService authService; // 注入认证服务

    /**
     * 登录方法
     *
     * @param loginDto 登录信息
     * @return 结果 (后续会返回 Token)
     */
    @PostMapping("/login")
    @Log(title = "用户登录", businessType = BusinessType.LOGIN, isSaveRequestData = false, isSaveResponseData = false) // 不记录请求体(密码)和返回体(token)
    public R<?> login(@Valid @RequestBody LoginDto loginDto) {
        // 调用认证服务进行登录
        // 这里暂时只返回成功与否，后续会返回 Token
        try {
            String token = authService.login(loginDto.getUsername(), loginDto.getPassword());
            // 假设 token 是生成的 JWT 或其他凭证
            // 为了方便，暂时直接返回成功消息，或者返回一个包含 token 的 Map
            // Map<String, String> result = new HashMap<>();
            // result.put("token", token);
            // return R.success("登录成功", result);
            return R.success("登录成功，Token: " + token); // 简化返回
        } catch (Exception e) {
            // 处理认证异常 (例如 用户名不存在、密码错误)
            // AuthService 中应该抛出特定的异常，这里可以捕获并返回相应的错误信息
            return R.fail(e.getMessage());
        }
    }

    /**
     * 登出方法
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Log(title = "用户登出", businessType = BusinessType.LOGOUT)
    public R<?> logout() {
        // TODO: 后续可以添加逻辑，例如记录登出日志，或者将 Token 加入黑名单 (如果需要)
        return R.success("退出成功");
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息 (包含角色、权限等)
     */
    @GetMapping("/getInfo")
    public R<?> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof LoginUser)) {
            return R.fail(401, "用户未登录或认证信息无效"); // 或者抛出异常由全局处理器处理
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        User user = loginUser.getUser();
        Set<String> permissions = loginUser.getPermissions(); // 获取包含角色和权限的集合

        // 构建返回给前端的数据结构
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user); // 可以只选择部分字段返回，避免敏感信息泄露
        // userInfo.put("name", user.getNickName()); // 例如返回昵称
        // userInfo.put("avatar", user.getAvatar());
        
        // 提取角色信息 (从 permissions 中过滤以 ROLE_ 开头的)
        Set<String> roles = permissions.stream()
                                     .filter(p -> p.startsWith("ROLE_"))
                                     .map(p -> p.substring(5)) // 去掉 ROLE_ 前缀
                                     .collect(Collectors.toSet());
        userInfo.put("roles", roles);
        
        // 提取权限标识 (从 permissions 中过滤掉角色)
        Set<String> perms = permissions.stream()
                                    .filter(p -> !p.startsWith("ROLE_"))
                                    .collect(Collectors.toSet());
        userInfo.put("permissions", perms);

        return R.success(userInfo);
    }

} 