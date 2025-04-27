package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.User;
import com.school.mapper.UserMapper;
// import com.school.mapper.RoleMapper; // 可能需要注入
import com.school.entity.UserRole;
import com.school.mapper.UserRoleMapper;
import com.school.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入 Page
import com.baomidou.mybatisplus.core.metadata.IPage; // 导入 IPage
import com.school.entity.dto.UserDto;
import com.school.entity.query.UserQuery;
import com.school.entity.vo.UserVo;
import com.school.entity.Role; // 导入 Role
import com.school.mapper.RoleMapper; // 导入 RoleMapper
import org.springframework.beans.BeanUtils; // 用于对象拷贝
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder; // 导入 PasswordEncoder
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息 服务实现类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    // @Autowired
    // private RoleMapper roleMapper; // 注入 RoleMapper 用于校验角色等
    @Autowired
    private RoleMapper roleMapper; // 取消注释并注入

    @Autowired
    private UserRoleMapper userRoleMapper; // 注入 UserRoleMapper 用于处理用户角色关联

    @Autowired
    private PasswordEncoder passwordEncoder; // 注入 PasswordEncoder

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果 true 唯一 false 不唯一
     */
    @Override
    public boolean checkUserNameUnique(User user) {
        Long userId = user.getId() == null ? -1L : user.getId();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, user.getUserName());
        User info = userMapper.selectOne(queryWrapper);
        if (info != null && info.getId().longValue() != userId.longValue()) {
            return false; // 用户名已存在，且不是当前用户
        }
        return true; // 用户名唯一
    }

    /**
     * 新增用户信息
     *
     * @param userDto 用户信息 DTO
     * @return 结果
     */
    @Override
    @Transactional // 涉及用户表和用户角色关联表的操作，需要事务管理
    public boolean insertUser(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user); // 简单拷贝
        // roleIds 会被拷贝过来

        // 1. 密码加密
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // 使用注入的 encoder 加密

        // 2. 插入用户基本信息
        int rows = userMapper.insert(user); // user 对象现在有 ID 了
        if (rows <= 0) {
            return false;
        }

        // 3. 插入用户与角色关联信息 (使用拷贝过来的 roleIds)
        insertUserRole(user);
        return true;
    }

    /**
     * 修改用户信息
     *
     * @param userDto 用户信息 DTO
     * @return 结果
     */
    @Override
    @Transactional
    public boolean updateUser(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user); // 简单拷贝
        // roleIds 会被拷贝过来

        // 注意：通常更新操作不直接更新密码，除非是专门的重置密码接口
        // 这里我们假设如果 DTO 中密码为 null 或空，则不更新密码
        if (!StringUtils.hasText(userDto.getPassword())) {
            user.setPassword(null); // 设置为 null，MybatisPlus 默认不更新 null 字段
        }
        // 如果密码需要加密，在此处处理

        Long userId = user.getId();
        // 1. 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 2. 新增用户与角色关联 (使用拷贝过来的 roleIds)
        insertUserRole(user);
        // 3. 更新用户基本信息
        return userMapper.updateById(user) > 0;
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息 (只需要 id 和 isValid 字段)
     * @return 结果
     */
    @Override
    public boolean updateUserStatus(User user) {
        // 这里假设 User 对象只包含 id 和需要更新的 isValid 状态
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setIsValid(user.getIsValid()); // 使用 BaseEntity 的 isValid 字段
        return userMapper.updateById(updateUser) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 新密码
     * @return 结果
     */
    @Override
    public boolean resetUserPwd(Long userId, String password) {
        User user = new User();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(password)); // 使用注入的 encoder 加密新密码
        return userMapper.updateById(user) > 0;
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteUserById(Long userId) {
        // 1. 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 2. 删除用户
        return userMapper.deleteById(userId) > 0; // 或者逻辑删除
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID数组
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteUserByIds(Long[] userIds) {
        if (userIds == null || userIds.length == 0) {
            return false;
        }
        List<Long> userIdList = Arrays.asList(userIds);
        // 1. 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserIds(userIdList);
        // 2. 删除用户
        return userMapper.deleteBatchIds(userIdList) > 0; // 或者逻辑删除
    }

    // --- 辅助方法 --- 

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(User user) {
        Long[] roles = user.getRoleIds();
        if (roles != null && roles.length > 0) {
            // 创建用户角色关联列表
            List<UserRole> list = new ArrayList<UserRole>();
            for (Long roleId : roles) {
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (!list.isEmpty()) {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 分页查询用户列表
     *
     * @param page      分页对象
     * @param userQuery 查询条件
     * @return 分页结果
     */
    @Override
    public IPage<UserVo> selectUserPage(Page<User> page, UserQuery userQuery) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 添加查询条件
        wrapper.like(StringUtils.hasText(userQuery.getUserName()), User::getUserName, userQuery.getUserName());
        wrapper.eq(StringUtils.hasText(userQuery.getPhoneNumber()), User::getPhonenumber, userQuery.getPhoneNumber());
        wrapper.eq(userQuery.getIsValid() != null, User::getIsValid, userQuery.getIsValid());
        // 添加排序等其他逻辑...
        wrapper.orderByDesc(User::getCreateTime); // 例如按创建时间降序

        IPage<User> userPage = userMapper.selectPage(page, wrapper);

        // 转换为 UserVo Page
        IPage<UserVo> userVoPage = userPage.convert(user -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            List<Role> roles = selectRolesByUserId(user.getId());
            userVo.setRoles(roles);
            userVo.setRoleIds(roles.stream().map(Role::getId).toArray(Long[]::new)); // 填充 roleIds 数组
            return userVo;
        });

        return userVoPage;
    }

    /**
     * 根据用户ID查询用户详细信息 (包含角色)
     *
     * @param userId 用户ID
     * @return UserVo 用户视图对象
     */
    @Override
    public UserVo selectUserVoById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);

        List<Role> roles = selectRolesByUserId(userId);
        userVo.setRoles(roles);
        userVo.setRoleIds(roles.stream().map(Role::getId).toArray(Long[]::new)); // 填充 roleIds 数组

        return userVo;
    }

    /**
     * 根据用户ID查询角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    private List<Role> selectRolesByUserId(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        // 根据 roleIds 查询角色信息
        return roleMapper.selectBatchIds(roleIds);
    }
} 