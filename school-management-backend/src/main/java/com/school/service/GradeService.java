/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 16:10:00
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 16:21:55
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\GradeService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Grade;
import com.school.entity.query.GradeQuery;
import com.school.entity.dto.GradeDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 成绩服务接口
 *
 * @author Gemini
 * @since 2024-05-10
 */
public interface GradeService extends IService<Grade> {

    // 可以在这里定义 Service 层特有的方法

    /**
     * 分页查询成绩列表
     *
     * @param query 查询条件
     * @return 分页结果 (包含关联名称)
     */
    Page<GradeDto> listGrades(GradeQuery query);

    /**
     * 添加成绩
     *
     * @param grade 成绩信息
     * @return 是否成功
     */
    boolean addGrade(Grade grade);

    /**
     * 更新成绩
     *
     * @param grade 成绩信息
     * @return 是否成功
     */
    boolean updateGrade(Grade grade);

    /**
     * 删除成绩
     *
     * @param id 成绩ID
     * @return 是否成功
     */
    boolean deleteGrade(Long id);

    /**
     * 获取成绩详情
     *
     * @param id 成绩ID
     * @return 成绩实体
     */
    Grade getGradeInfo(Long id);

    /**
     * 批量删除成绩
     *
     * @param ids 成绩ID列表
     * @return 是否成功
     */
    boolean deleteGradesBatch(List<Long> ids);

    /**
     * 导入成绩数据
     *
     * @param file Excel 文件
     * @return 导入结果信息 (成功 N 条，失败 M 条及原因)
     * @throws Exception 可能的解析或数据库异常
     */
    String importGrades(MultipartFile file) throws Exception;

    /**
     * 导出成绩数据
     *
     * @param response HttpServletResponse
     * @param query    查询条件
     * @throws IOException IO 异常
     */
    void exportGrades(HttpServletResponse response, GradeQuery query) throws IOException;

} 