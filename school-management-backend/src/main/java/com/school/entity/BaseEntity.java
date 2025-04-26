/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 21:42:33
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 17:07:24
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\BaseEntity.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类，包含公共字段
 */
@Data
@EqualsAndHashCode // 如果需要比较对象，建议添加
public abstract class BaseEntity implements Serializable {

    /**
     * 创建人ID (假设用户ID是 Long 类型)
     * 自动填充: INSERT
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     * 自动填充: INSERT
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改人ID
     * 自动填充: INSERT_UPDATE
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 修改时间
     * 自动填充: INSERT_UPDATE
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否有效 (1:有效, 0:无效)
     * 通常需要默认值或在业务中设置
     */
    @TableField("is_valid") // 明确指定数据库列名
    private Integer isValid = 1; // 默认为有效

    /**
     * 是否删除 (逻辑删除标志, 0:未删除, 1:已删除)
     * MybatisPlus 会根据 application.yml 中的配置自动处理
     */
    @TableLogic
    @TableField("is_deleted") // 明确指定数据库列名
    private Integer deleted = 0; // 默认为未删除

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
} 