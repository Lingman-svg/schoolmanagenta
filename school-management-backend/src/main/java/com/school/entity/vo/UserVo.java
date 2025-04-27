package com.school.entity.vo;

import com.school.entity.Role; // 假设 Role 实体存在
import com.school.entity.User; // 导入 User 实体
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承 User 的字段
public class UserVo extends User {

    /**
     * 用户拥有的角色列表
     */
    private List<Role> roles;

    // 如果只需要角色 ID，可以定义为：
    // private List<Long> roleIds;

    // 如果前端还需要所有角色的列表以供选择，可以添加
    // private List<Role> allRoles;
} 