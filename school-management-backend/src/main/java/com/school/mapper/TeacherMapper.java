package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师 Mapper 接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    // 基础 CRUD 由 BaseMapper 提供

    // 如果 TeacherQuery 中需要根据 subjectId 进行关联查询，
    // 可能需要在这里定义一个自定义的 selectPage 方法，并编写对应的 XML
    // 例如: IPage<Teacher> selectTeacherPageByQuery(Page<Teacher> page, @Param("query") TeacherQuery query);
} 