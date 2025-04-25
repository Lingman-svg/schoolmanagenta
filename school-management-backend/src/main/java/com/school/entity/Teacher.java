package com.school.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdcardUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

/**
 * 教师实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sch_teacher")
public class Teacher extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty("教师ID")
    private Long id;

    @NotBlank(message = "教师名称不能为空")
    @ExcelProperty("教师名称")
    private String teacherName;

    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证号码格式不正确")
    @ExcelProperty("身份证号")
    private String idCard;

    /**
     * 出生日期 - 优先从身份证获取，否则允许手动输入
     * 注意：数据库存储 LocalDate， Excel 导出/导入时需要格式化
     */
    @ExcelProperty("出生日期")
    @DateTimeFormat("yyyy-MM-dd") // EasyExcel 格式化
    private LocalDate birthDate;

    /**
     * 年龄 - 根据出生日期动态计算，不直接映射数据库
     */
    @TableField(exist = false) // 表示该字段不为数据库表字段
    @ExcelProperty("年龄")
    private Integer age;

    @ExcelProperty("联系方式")
    private String phone;

    @ExcelProperty("当前住址")
    private String address;

    @ExcelProperty("户籍")
    private String nativePlace; // 原为 String籍贯

    @ExcelProperty("民族")
    private String nation;

    @ExcelProperty("性别")
    private String gender; // 男 / 女 / 其他

    /**
     * 关联的科目ID列表 (非数据库字段)
     * 用于前端传递数据和 Service 层处理关联关系
     */
    @TableField(exist = false)
    private List<Long> subjectIds;

    // --- Getter/Setter 增强 ---

    /**
     * 重写 set 方法，当设置身份证时，尝试自动填充出生日期、年龄、性别
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
        if (IdcardUtil.isValidCard(idCard)) {
            this.birthDate = IdcardUtil.getBirthDate(idCard).toLocalDateTime().toLocalDate();
            // this.age = IdcardUtil.getAgeByIdCard(idCard); // Hutool getAgeByIdCard 返回的是周岁
            this.gender = IdcardUtil.getGenderByIdCard(idCard) == 1 ? "男" : "女";
            // 计算精确年龄
            if (this.birthDate != null) {
                 this.age = Period.between(this.birthDate, LocalDate.now()).getYears();
            }
        } else {
            // 如果身份证无效，清空自动填充的字段，允许手动设置
            // this.birthDate = null; // 或者不清空，让用户可以覆盖
             this.gender = null;
             this.age = null;
        }
    }

    /**
     * 如果手动设置了出生日期，也更新年龄
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        if (this.birthDate != null) {
             this.age = Period.between(this.birthDate, LocalDate.now()).getYears();
        }
         else {
            // 如果身份证有效，但手动清空生日，年龄也清空
            if (!IdcardUtil.isValidCard(this.idCard)) {
                 this.age = null;
            }
           
        }
    }

    /**
     * 获取年龄时，如果未计算，尝试根据生日计算
     * @return 年龄
     */
    public Integer getAge() {
        if (this.age == null && this.birthDate != null) {
             return Period.between(this.birthDate, LocalDate.now()).getYears();
        }
        return this.age;
    }

    // 继承自 BaseEntity 的字段... (remark, isValid, deleted, createBy, createTime, updateBy, updateTime)
} 