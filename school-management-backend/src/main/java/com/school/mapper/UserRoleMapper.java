/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 19:11:37
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 19:12:13
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\mapper\UserRoleMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户和角色关联表 Mapper 接口
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID删除用户与角色关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户与角色关联
     *
     * @param userIds 需要删除的用户ID集合
     * @return 结果
     */
    int deleteUserRoleByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 批量新增用户角色信息
     *
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    int batchUserRole(@Param("userRoleList") List<UserRole> userRoleList);
} 