package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表实体类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sch_menu") // 表名建议为 sch_menu
public class Menu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID (主键)
     * 使用 TableId 注解，但 Menu ID 通常不由数据库自增，可能需要手动管理或UUID
     * 这里暂定为 Long，由 BaseEntity 提供
     */
    @TableId
    private Long id; // 使用 BaseEntity 的 id

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @TableField("menu_name")
    private String menuName;

    /**
     * 父菜单ID (根目录为 0)
     */
    @NotNull(message = "父菜单ID不能为空")
    @TableField("parent_id")
    private Long parentId;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 路由地址 (对于菜单类型)
     */
    @Size(max = 200, message = "路由地址不能超过200个字符")
    @TableField("path")
    private String path;

    /**
     * 组件路径 (对于菜单类型)
     */
    @Size(max = 255, message = "组件路径不能超过255个字符")
    @TableField("component")
    private String component;

    /**
     * 是否为外链（0否 1是）
     */
    @TableField("is_frame")
    private Integer isFrame = 0; // 默认为内部链接

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @NotBlank(message = "菜单类型不能为空")
    @TableField("menu_type")
    private String menuType;

    /**
     * 菜单状态（0显示 1隐藏）-- 控制菜单在侧边栏是否显示
     */
    @TableField("visible")
    private Integer visible = 0; // 默认显示

     /**
     * 菜单状态（0正常 1停用）-- 控制菜单本身是否可用
     * 继承自 BaseEntity 的 isValid 字段 (假设 1=有效/正常, 0=无效/停用)
     */
    // private Integer status; // 使用 BaseEntity 的 isValid

    /**
     * 权限标识 (例如 system:user:list)
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @TableField("perms")
    private String perms;

    /**
     * 菜单图标
     */
    @Size(max = 100, message = "图标名称长度不能超过100个字符")
    @TableField("icon")
    private String icon;

    // --- 非数据库字段 --- (用于构建树形结构)

    /**
     * 子菜单
     */
    @TableField(exist = false)
    private List<Menu> children = new ArrayList<>();

    // id, remark, is_deleted, create_by, create_time, update_by, update_time, isValid (status) 继承自 BaseEntity
} 