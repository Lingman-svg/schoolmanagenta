package com.school.entity.dto;

import com.school.entity.StudentClazzHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 学生班级历史记录数据传输对象 (包含班级名称)
 *
 * @author Gemini
 * @since 2024-05-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StudentClazzHistoryDto extends StudentClazzHistory {

    private static final long serialVersionUID = 1L;

    /**
     * 班级名称 (从关联查询获取)
     */
    private String className;

} 