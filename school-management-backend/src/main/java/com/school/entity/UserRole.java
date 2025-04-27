package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户和角色关联表实体类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sch_user_role") // 表名建议为 sch_user_role
public class UserRole {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

} 