package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Grade;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成绩 Mapper 接口
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

    // 可以在这里添加自定义的 SQL 方法

} 