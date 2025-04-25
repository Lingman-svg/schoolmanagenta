package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Student;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper 接口
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    // BaseMapper 已提供基本 CRUD 方法
    // 可根据需要添加自定义 SQL 方法，例如：涉及多表关联的复杂查询

} 