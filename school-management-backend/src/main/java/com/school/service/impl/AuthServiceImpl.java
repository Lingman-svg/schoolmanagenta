package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.school.entity.User;
import com.school.mapper.UserMapper;
import com.school.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException; // 用于密码错误
import org.springframework.security.core.userdetails.UsernameNotFoundException; // 用于用户名不存在
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.school.utils.JwtTokenUtil; // 导入 JWT 工具类
import org.springframework.security.core.userdetails.UserDetails; // 需要 UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 用于创建权限
import java.util.ArrayList; // 用于创建权限列表
import java.util.List; // 用于创建权限列表

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // TODO: 后续注入 JWT 工具类用于生成 Token - 已完成
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public String login(String username, String password) throws Exception {
        // 1. 根据用户名查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = userMapper.selectOne(queryWrapper);

        // 2. 判断用户是否存在
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 3. 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }

        // 4. 检查用户状态 (可选)
        if (user.getIsValid() != null && user.getIsValid() == 1) { // 假设 1 为停用
             throw new RuntimeException("账号已停用"); // 或自定义异常
        }

        // 5. 认证成功, 生成 Token
        // 需要将 User 转换为 UserDetails 类型才能生成 Token
        // 这里我们临时创建一个 UserDetails 对象，后续会通过 UserDetailsService 获取
        // TODO: 权限列表需要根据用户的角色/权限实际查询
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(); 
        // authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); // 示例权限
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            user.getUserName(),
            user.getPassword(), // 密码字段在 UserDetails 中是必须的，但生成 Token 时不一定使用
            authorities);

        String token = jwtTokenUtil.generateToken(userDetails);
        // String token = "dummy-token-for-" + user.getUserName(); // 移除临时 Token

        // TODO: 可以在这里记录登录日志，更新用户最后登录时间/IP 等

        return token;
    }
} 