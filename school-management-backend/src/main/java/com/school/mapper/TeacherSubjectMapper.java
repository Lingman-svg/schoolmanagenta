package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.TeacherSubject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师-科目关联表 Mapper 接口
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Mapper
public interface TeacherSubjectMapper extends BaseMapper<TeacherSubject> {
    // BaseMapper 已提供基本 CRUD 方法
    // 可以根据需要在这里添加自定义的 SQL 方法
    // 例如：根据教师 ID 删除关联关系、根据科目 ID 删除关联关系等
} 