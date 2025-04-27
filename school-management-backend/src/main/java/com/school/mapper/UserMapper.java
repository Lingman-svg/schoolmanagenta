/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 16:18:39
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 16:20:37
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\mapper\UserMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 数据层
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 可以在这里添加自定义的 SQL 方法
    // 例如，根据用户名查询用户（包含已删除）
    // User selectUserByUserName(String userName);

    // 例如，根据用户 ID 查询关联的角色信息
    // List<Role> selectRolesByUserId(Long userId);
} 