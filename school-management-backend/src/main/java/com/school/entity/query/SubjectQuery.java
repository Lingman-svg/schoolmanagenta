package com.school.entity.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科目查询条件实体类
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承自 PageDomain
public class SubjectQuery extends PageDomain {

    /**
     * 科目代号 (用于模糊查询)
     */
    private String subjectCode;

    /**
     * 科目名称 (用于模糊查询)
     */
    private String subjectName;

    /**
     * 是否有效 (精确匹配)
     */
    private Integer isValid;

    // 继承自分页类 PageDomain 的字段: pageSize, pageNum, orderByColumn, isAsc
} 