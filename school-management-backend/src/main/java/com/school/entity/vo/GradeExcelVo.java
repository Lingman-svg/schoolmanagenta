package com.school.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩 Excel 导入导出视图对象
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Data
@HeadRowHeight(20) // 表头行高
@ColumnWidth(20) // 默认列宽
public class GradeExcelVo {

    // ID 通常不参与导入导出
    // @ExcelIgnore
    // private Long id;

    @ExcelProperty("学生ID")
    @ExcelIgnore
    private Long studentId;

    @ExcelProperty("学生姓名") // 导入时可选，用于辅助核对，但不建议作为查找依据
    private String studentName;

    @ExcelProperty("科目ID")
    @ExcelIgnore
    private Long subjectId;

    @ExcelProperty("科目名称") // 导入时可选
    private String subjectName;

    @ExcelProperty("班级ID")
    @ExcelIgnore
    private Long clazzId;

    @ExcelProperty("班级名称") // 导入时可选
    private String clazzName;

    @ExcelProperty("任课老师ID") // 可选
    @ExcelIgnore
    private Long teacherId;

    @ExcelProperty("任课老师姓名") // 导入时可选
    private String teacherName;

    @ExcelProperty("考试名称")
    private String examName;

    @ExcelProperty("考试时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss") // 定义日期时间格式
    private LocalDateTime examTime;

    @ExcelProperty("成绩分数")
    @ColumnWidth(15)
    private BigDecimal score;

    @ExcelProperty("成绩录入时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(25)
    private LocalDateTime recordTime;

    @ExcelProperty("备注")
    @ColumnWidth(30)
    private String remark;

} 