package com.school.entity.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师查询条件实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherQuery extends PageDomain {

    /**
     * 教师名称 (模糊查询)
     */
    private String teacherName;

    /**
     * 联系方式 (模糊查询)
     */
    private String phone;

    /**
     * 身份证号 (精确或模糊查询，根据业务定)
     */
    private String idCard;

    /**
     * 性别 (精确匹配)
     */
    private String gender;

    /**
     * 是否有效 (精确匹配)
     */
    private Integer isValid;

    /**
     * 所教科目ID (用于查询教特定科目的老师)
     * 注意：这需要 Service 层进行关联查询
     */
    private Long subjectId;

    // 继承自分页类 PageDomain 的字段: pageSize, pageNum, orderByColumn, isAsc
} 