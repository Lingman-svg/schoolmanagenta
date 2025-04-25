/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 21:47:37
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-25 21:48:13
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\Subject.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科目实体类
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承自 BaseEntity，需要调用 super 的 equals/hashCode
@TableName("sch_subject") // 明确指定表名，避免使用关键字
public class Subject extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO) // 假设主键是自增的
    @ExcelProperty("科目ID") // EasyExcel 导出注解
    private Long id;

    /**
     * 科目代号
     */
    @NotBlank(message = "科目代号不能为空") // JSR 303 Validation
    @ExcelProperty("科目代号")
    private String subjectCode;

    /**
     * 科目名称
     */
    @NotBlank(message = "科目名称不能为空")
    @ExcelProperty("科目名称")
    private String subjectName;

    // 继承自 BaseEntity 的字段: createBy, createTime, updateBy, updateTime, isValid, deleted, remark
    // 如果需要在 Excel 中导出/导入这些字段，也需要加上 @ExcelProperty 注解
    // 例如:
    // @ExcelProperty("备注")
    // private String remark;
} 