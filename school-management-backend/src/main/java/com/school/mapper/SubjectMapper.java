package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Subject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科目 Mapper 接口
 */
@Mapper // 标记为 Mybatis Mapper 接口
public interface SubjectMapper extends BaseMapper<Subject> {

    // BaseMapper 已经提供了常用的 CRUD 方法
    // 例如: insert, deleteById, updateById, selectById, selectList, selectPage 等

    // 如果需要自定义复杂的 SQL 查询，可以在这里定义方法
    // 例如: List<Subject> findSubjectsByCustomCondition(Map<String, Object> params);
    // 对应的 SQL 语句写在 src/main/resources/mapper/SubjectMapper.xml 文件中
} 