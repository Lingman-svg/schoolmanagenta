/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 02:27:41
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 02:34:39
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\mapper\StudentClazzHistoryMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.StudentClazzHistory;
import com.school.entity.dto.StudentClazzHistoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 学生班级分配历史记录 Mapper 接口
 * </p>
 *
 * @author Gemini
 * @since 2024-05-07
 */
@Mapper
public interface StudentClazzHistoryMapper extends BaseMapper<StudentClazzHistory> {

    /**
     * 根据学生ID查询班级历史记录（包含班级名称）
     * @param studentId 学生ID
     * @return DTO列表
     */
    List<StudentClazzHistoryDto> selectHistoryWithClassName(@Param("studentId") Long studentId);

    // 可以在这里添加自定义的 SQL 方法，如果需要的话
    // 例如：查询某个学生的所有历史记录
    // List<StudentClazzHistory> selectHistoryByStudentId(Long studentId);

} 