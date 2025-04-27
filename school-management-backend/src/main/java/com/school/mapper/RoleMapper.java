package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色信息表 Mapper 接口
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色权限字符串列表
     *
     * @param userId 用户ID
     * @return 角色权限字符串列表 (e.g. ["admin", "teacher"])
     */
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);

    // 可以添加其他查询，例如查询所有未被逻辑删除的角色等

} 