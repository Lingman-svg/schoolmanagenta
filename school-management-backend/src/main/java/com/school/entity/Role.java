package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set; // 用于存储关联的菜单ID

/**
 * 角色信息表实体类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sch_role") // 表名建议为 sch_role
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID (主键)
     */
    @TableId // 使用 BaseEntity 的 id
    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    @TableField("role_name")
    private String roleName;

    /**
     * 角色权限字符串 (例如 'admin', 'teacher')
     */
    @NotBlank(message = "权限字符不能为空")
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    @TableField("role_key")
    private String roleKey;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    @TableField("role_sort")
    private Integer roleSort;

     /**
     * 角色状态（0正常 1停用）
     * 继承自 BaseEntity 的 isValid 字段
     */
    // @NotNull(message = "角色状态不能为空")
    // @TableField("status")
    // private Integer status; // 使用 BaseEntity 的 isValid

    // --- 非数据库字段 --- (用于关联查询或前端交互)

    /**
     * 菜单组 (编辑角色时使用，存放选中的菜单ID)
     */
    @TableField(exist = false)
    private Long[] menuIds;

     /**
     * 用户是否存在此角色标识 默认不存在
     * (用于用户管理分配角色时)
     */
    @TableField(exist = false)
    private boolean flag = false;

    // id, remark, is_deleted, create_by, create_time, update_by, update_time, isValid (status) 继承自 BaseEntity
} 