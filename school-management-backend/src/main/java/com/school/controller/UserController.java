package com.school.controller;

import com.school.entity.User;
import com.school.service.UserService;
import com.school.utils.R;
import com.school.entity.query.UserQuery;
import com.school.entity.dto.UserDto;
import com.school.entity.vo.UserVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户信息 Controller
 *
 * @author Gemini
 * @since 2024-05-15
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表
     * TODO: 添加分页和条件查询逻辑 - 已完成
     */
    @PreAuthorize("hasAuthority('base:user:list') or hasAuthority('base:user:view')")
    @GetMapping("/list")
    public R<IPage<UserVo>> list(UserQuery query, 
                                  @RequestParam(defaultValue = "1") Integer pageNum, 
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<UserVo> userPage = userService.selectUserPage(page, query);
        return R.success(userPage);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("hasAuthority('base:user:view')")
    @GetMapping(value = "/{userId}")
    public R<UserVo> getInfo(@PathVariable Long userId) {
        UserVo userVo = userService.selectUserVoById(userId);
        return userVo != null ? R.success(userVo) : R.fail("用户不存在");
    }

    /**
     * 新增用户
     */
    @PreAuthorize("hasAuthority('base:user:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody UserDto userDto) {
        // 用户名唯一性校验
        User checkUser = new User();
        checkUser.setUserName(userDto.getUserName());
        if (!userService.checkUserNameUnique(checkUser)) {
            return R.fail("新增用户'" + userDto.getUserName() + "'失败，登录账号已存在");
        }
        // TODO: 密码加密处理
        boolean success = userService.insertUser(userDto);
        return success ? R.success() : R.fail("新增用户失败");
    }

    /**
     * 修改用户
     */
    @PreAuthorize("hasAuthority('base:user:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody UserDto userDto) {
        // 用户名唯一性校验 (排除自身)
         User checkUser = new User();
         checkUser.setId(userDto.getId());
         checkUser.setUserName(userDto.getUserName());
         if (!userService.checkUserNameUnique(checkUser)) {
             return R.fail("修改用户'" + userDto.getUserName() + "'失败，登录账号已存在");
        }
        // TODO: 如果允许修改密码，需要处理 (UserServiceImpl 中已做基本处理)
        boolean success = userService.updateUser(userDto);
        return success ? R.success() : R.fail("修改用户失败");
    }

    /**
     * 删除用户
     */
    @PreAuthorize("hasAuthority('base:user:delete')")
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        boolean success = userService.deleteUserByIds(userIds);
        return success ? R.success() : R.fail("删除用户失败");
    }

     /**
     * 重置密码
     */
    @PreAuthorize("hasAuthority('base:user:resetPwd')")
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody UserDto userDto) {
        // TODO: 校验用户是否存在等
        // TODO: 密码加密处理
        // 校验密码是否为空 (如果 password 字段可以为空)
        if (!StringUtils.hasText(userDto.getPassword())) {
             return R.fail("密码不能为空");
        }
        boolean success = userService.resetUserPwd(userDto.getId(), userDto.getPassword());
        return success ? R.success() : R.fail("重置密码失败");
    }

    /**
     * 状态修改
     */
    @PreAuthorize("hasAuthority('base:user:edit')")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody UserDto userDto) {
        // 只需传递 userId 和 isValid 状态
        User statusUser = new User();
        statusUser.setId(userDto.getId());
        statusUser.setIsValid(userDto.getIsValid());
        boolean success = userService.updateUserStatus(statusUser);
        return success ? R.success() : R.fail("修改状态失败");
    }
} 