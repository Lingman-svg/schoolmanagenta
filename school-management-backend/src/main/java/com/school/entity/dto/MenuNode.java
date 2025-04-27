package com.school.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 前端路由配置项
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY) // 值为 null 或空的集合不序列化
public class MenuNode implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    private Long id;

    /** 菜单显示名称 */
    private String label;

    /** 父菜单ID */
    // private Long parentId; // 通常前端不需要父ID，通过 children 表达层级

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 是否为外链（0否 1是） */
    // private Integer isFrame; // Meta 信息中处理

    /** 菜单类型（M目录 C菜单 F按钮）*/
    private String menuType;

    /** 权限标识 */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 菜单状态：0显示，1隐藏 */
    private String visible;

    /** 菜单状态：0正常，1停用 */
    private String status;

    /** 子菜单 */
    private List<MenuNode> children;

    // 可以添加 Meta 对象来存储路由元信息 (title, icon, noCache 等)
    // private Meta meta;
    // @Data
    // public static class Meta {
    //     private String title;
    //     private String icon;
    //     private boolean noCache;
    //     private String link;
    // }
} 