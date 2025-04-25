package com.school.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.school.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import cn.hutool.core.util.IdcardUtil; // For ID card utils

/**
 * 学生实体类
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sch_student")
public class Student extends BaseEntity {

    
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty("学生ID")
    private Long id;

    /**
     * 学生姓名
     */
    @NotBlank(message = "学生姓名不能为空")
    @Size(max = 100, message = "学生姓名长度不能超过100个字符")
    private String studentName;

    /**
     * 学号 (可以考虑生成规则或唯一性)
     */
    @NotBlank(message = "学号不能为空")
    @Size(max = 50, message = "学号长度不能超过50个字符")
    private String studentNumber;

    /**
     * 性别 (由身份证号计算，通常不直接校验输入)
     */
    private String gender;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}(\\d|X)$", message = "身份证号格式不正确", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String idCard;

    /**
     * 出生日期 (可从身份证提取，但也需校验)
     */
    @NotNull(message = "出生日期不能为空")
    private LocalDate birthDate;

    /**
     * 年龄 (可从出生日期计算)
     */
    @TableField(exist = false)
    private Integer age;

    /**
     * 联系方式
     */
    @NotBlank(message = "联系方式不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 家庭住址
     */
    @Size(max = 255, message = "家庭住址长度不能超过255个字符")
    private String address;

    /**
     * 户籍
     */
    @Size(max = 100, message = "户籍长度不能超过100个字符")
    private String nativePlace;

    /**
     * 民族
     */
    @Size(max = 50, message = "民族长度不能超过50个字符")
    private String nation;

    /**
     * 当前班级ID (外键关联 sch_clazz)
     */
    @NotNull(message = "请选择所在班级")
    private Long currentClazzId;

    // --- Custom setter/getter for automatic calculation ---

    /**
     * 设置身份证号时，自动提取并设置出生日期和性别
     * @param idCard 身份证号
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
        if (IdcardUtil.isValidCard(idCard)) {
            this.birthDate = IdcardUtil.getBirthDate(idCard).toJdkDate().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate();
            this.gender = IdcardUtil.getGenderByIdCard(idCard) == 1 ? "男" : "女";
            calculateAndSetAge(); // Calculate age after setting birthDate
        } else {
            // Handle invalid ID card case? Clear fields or log warning?
            this.birthDate = null;
            this.gender = null;
            this.age = null;
        }
    }

    /**
     * 设置出生日期时，自动计算年龄
     * @param birthDate 出生日期
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        calculateAndSetAge();
    }

    // Getter for age - calculate if birthDate is present
    public Integer getAge() {
        if (this.age == null && this.birthDate != null) {
            return Period.between(this.birthDate, LocalDate.now()).getYears();
        }
        return this.age;
    }

    // Helper method to calculate and set age internally
    private void calculateAndSetAge() {
        if (this.birthDate != null) {
            this.age = Period.between(this.birthDate, LocalDate.now()).getYears();
        } else {
            this.age = null;
        }
    }

} 