/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 21:23:45
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 21:24:38
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\query\CourseQuery.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程查询条件封装
 *
 * @author Gemini
 * @since 2024-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseQuery extends PageDomain {

    /**
     * 班级ID (用于筛选)
     */
    private Long classId;

    /**
     * 教师ID (用于筛选)
     */
    private Long teacherId;

    /**
     * 科目ID (用于筛选)
     */
    private Long subjectId;
    
    /**
     * 星期几 (用于筛选)
     */
    private Integer dayOfWeek;
    
    /**
     * 状态 (1:有效, 0:无效) (用于筛选)
     */
    private Integer isValid;

    // 可以添加其他查询字段，例如根据地点、介绍关键字等
    // private String locationKeyword;
    // private String introKeyword;

    // PageDomain 中包含了 pageNum, pageSize, orderByColumn, isAsc 等分页和排序字段
} 