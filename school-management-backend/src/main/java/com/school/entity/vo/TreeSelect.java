package com.school.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.school.entity.Menu; // 假设 Menu 在此包结构下
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类 (用于 Element Plus TreeSelect 等组件)
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY) // 值为 null 或空的集合不序列化
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private Long id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    private List<TreeSelect> children;

    public TreeSelect() {
    }

    /**
     * 通过 Menu 实体构建 TreeSelect 节点
     * @param menu 菜单实体
     */
    public TreeSelect(Menu menu) {
        this.id = menu.getId();
        this.label = menu.getMenuName(); // 使用菜单名称作为标签
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            this.children = menu.getChildren().stream()
                                .map(TreeSelect::new) // 递归构建子节点
                                .collect(Collectors.toList());
        }
    }
} 