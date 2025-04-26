package com.school.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 节课时间查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true) // 确保调用父类的 equals 和 hashCode
@Schema(description = "节课时间查询参数")
public class CourseTimeQuery extends PageDomain {

    @Schema(description = "节课名称 (模糊查询)")
    private String periodName;

    // 可以在这里添加其他查询条件，例如按时间范围查询等
    // private LocalTime startTimeAfter;
    // private LocalTime endTimeBefore;

} 