/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 01:37:11
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 01:37:37
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\query\StudentQuery.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity.query;

import com.school.entity.query.PageDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生查询参数封装
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentQuery extends PageDomain {

    /**
     * 学生姓名 (模糊查询)
     */
    private String studentName;

    /**
     * 学号 (精确查询)
     */
    private String studentNumber;

    /**
     * 性别
     */
    private String gender;

    /**
     * 联系方式 (模糊查询)
     */
    private String phone;

    /**
     * 当前班级ID
     */
    private Long currentClazzId;

    // 可以根据需要添加更多查询条件，例如：
    // private String idCard;
    // private Integer minAge;
    // private Integer maxAge;
    // private String grade; // 如果需要按年级查询，可能需要关联班级表
} 