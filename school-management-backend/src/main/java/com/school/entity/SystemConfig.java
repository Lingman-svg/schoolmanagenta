package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置实体类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sch_system_config") // 表名建议为 sch_system_config
public class SystemConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 参数名称 (用于显示)
     */
    @NotBlank(message = "参数名称不能为空")
    @TableField("config_name")
    private String configName;

    /**
     * 参数键名 (唯一)
     */
    @NotBlank(message = "参数键名不能为空")
    @TableField("config_key")
    private String configKey;

    /**
     * 参数键值
     */
    @NotBlank(message = "参数键值不能为空")
    @TableField("config_value")
    private String configValue;

    /**
     * 系统内置（Y是 N否）(可以用 0/1 或 Y/N)
     * 系统内置参数通常不允许删除或修改键名
     */
    @TableField("config_type")
    private Integer configType; // 0=用户定义, 1=系统内置

    // id, remark, is_deleted, create_by, create_time, update_by, update_time 继承自 BaseEntity
    // is_valid 也可以从 BaseEntity 继承，如果需要的话
} 