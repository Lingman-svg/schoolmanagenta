package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Clazz;
import org.apache.ibatis.annotations.Mapper;

/**
 * 班级 Mapper 接口
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Mapper
public interface ClazzMapper extends BaseMapper<Clazz> {

    // BaseMapper 已提供基本 CRUD 方法
    // 可根据需要添加自定义方法，例如：根据班主任ID查询班级列表等

} 