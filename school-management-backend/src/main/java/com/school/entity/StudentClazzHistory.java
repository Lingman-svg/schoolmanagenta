package com.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime; // Using LocalDateTime for more precision

/**
 * 学生班级分配历史记录 实体类
 *
 * @author Gemini
 * @since 2024-05-07 
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sch_student_clazz_history")
public class StudentClazzHistory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 历史记录主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID (关联 sch_student.id)
     */
    private Long studentId;

    /**
     * 班级ID (关联 sch_clazz.id)
     */
    private Long clazzId;

    /**
     * 分配到该班级的日期时间
     */
    private LocalDateTime assignDate;

    /**
     * 从该班级移除的日期时间 (null表示当前仍在该班级)
     */
    private LocalDateTime removeDate;

} 