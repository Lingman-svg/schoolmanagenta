/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 16:20:16
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 19:30:46
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\UserService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.entity.dto.UserDto;
import com.school.entity.query.UserQuery;
import com.school.entity.vo.UserVo;

/**
 * 用户信息 服务层
 *
 * @author Gemini
 * @since 2024-05-14
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     *
     * @param page      分页对象 (MybatisPlus)
     * @param userQuery 查询条件
     * @return 分页结果 (包含 UserVo)
     */
    IPage<UserVo> selectUserPage(Page<User> page, UserQuery userQuery);

    /**
     * 根据用户ID查询用户详细信息 (包含角色)
     *
     * @param userId 用户ID
     * @return UserVo 用户视图对象
     */
    UserVo selectUserVoById(Long userId);

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果 true 唯一 false 不唯一
     */
    boolean checkUserNameUnique(User user);

    /**
     * 新增用户信息
     *
     * @param user 用户信息 DTO
     * @return 结果
     */
    boolean insertUser(UserDto user);

    /**
     * 修改用户信息
     *
     * @param user 用户信息 DTO
     * @return 结果
     */
    boolean updateUser(UserDto user);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean updateUserStatus(User user);

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 新密码
     * @return 结果
     */
    boolean resetUserPwd(Long userId, String password);

    /**
     * 通过用户ID删除用户 (需要处理用户角色关联)
     *
     * @param userId 用户ID
     * @return 结果
     */
    boolean deleteUserById(Long userId);

    /**
     * 批量删除用户信息 (需要处理用户角色关联)
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    boolean deleteUserByIds(Long[] userIds);

} 