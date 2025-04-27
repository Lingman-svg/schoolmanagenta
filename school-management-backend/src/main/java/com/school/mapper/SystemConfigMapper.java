package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统参数配置 Mapper 接口
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {
    // 通常不需要在这里添加额外的方法，除非有特殊的查询需求
    // MybatisPlus 提供的基础 CRUD 已经足够
} 