/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 20:06:21
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 20:08:08
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\CourseTime.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalTime; // Use LocalTime for TIME type

/**
 * 节课时间实体类
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sch_course_time") // 显式指定表名
public class CourseTime extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 节课名称 (例如: "第一节", "大课间")
     */
    @TableField("period_name")
    private String periodName;

    /**
     * 开始时间 (格式: HH:mm:ss)
     */
    @TableField("start_time")
    @JsonFormat(pattern = "HH:mm:ss") // Format for JSON
    private LocalTime startTime;

    /**
     * 结束时间 (格式: HH:mm:ss)
     */
    @TableField("end_time")
    @JsonFormat(pattern = "HH:mm:ss") // Format for JSON
    private LocalTime endTime;

    // create_by, create_time, update_by, update_time, deleted 由 BaseEntity 提供
} 