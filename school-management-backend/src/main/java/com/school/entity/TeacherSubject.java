/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 23:08:07
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-25 23:08:14
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\entity\TeacherSubject.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师与科目关联实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sch_teacher_subject") // 关联表名
public class TeacherSubject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 可以使用复合主键，或者单独的自增 ID，这里用自增 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 科目ID
     */
    private Long subjectId;
} 