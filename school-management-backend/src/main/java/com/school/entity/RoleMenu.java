package com.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色和菜单关联表实体类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sch_role_menu") // 表名建议为 sch_role_menu
public class RoleMenu {

    /** 角色ID */
    @TableId(type = IdType.INPUT) // 联合主键的一部分，非自增
    @TableField("role_id")
    private Long roleId;

    /** 菜单ID */
    @TableId(type = IdType.INPUT) // 联合主键的一部分，非自增
    @TableField("menu_id")
    private Long menuId;

    // 注意: MybatisPlus 对联合主键的支持有限，通常只将一个字段标记为 @TableId。
    // 在 Service 层处理时需要注意。
    // 或者不使用实体类，直接在 Mapper XML 中操作。
    // 如果确实需要实体，可以不继承 BaseEntity，因为它不需要那些公共字段。
} 