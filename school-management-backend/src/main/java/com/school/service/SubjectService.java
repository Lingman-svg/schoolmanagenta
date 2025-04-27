/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 21:48:21
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-05-10 17:32:53
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\SubjectService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Subject;
import com.school.entity.query.SubjectQuery;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 科目 Service 接口
 */
public interface SubjectService extends IService<Subject> {

    /**
     * 分页查询科目列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<Subject> findSubjectsByPage(SubjectQuery query);

    /**
     * 查询所有有效的科目列表 (通常用于下拉选择)
     *
     * @return 科目列表
     */
    List<Subject> findAllValidSubjects();

    /**
     * 新增科目
     *
     * @param subject 科目信息
     * @return 是否成功
     */
    boolean addSubject(Subject subject);

    /**
     * 修改科目
     *
     * @param subject 科目信息
     * @return 是否成功
     */
    boolean updateSubject(Subject subject);

    /**
     * 删除科目 (逻辑删除)
     *
     * @param id 科目ID
     * @return 是否成功
     */
    boolean deleteSubjectById(Long id);

    /**
     * 批量删除科目 (逻辑删除)
     *
     * @param ids ID列表
     * @return 是否成功
     */
    boolean deleteSubjectsByIds(List<Long> ids);

    /**
     * 导出科目数据到 Excel
     *
     * @param response HttpServletResponse
     * @param query    查询条件 (导出符合条件的科目)
     * @throws IOException IO异常
     */
    void exportSubjects(HttpServletResponse response, SubjectQuery query) throws IOException;

    /**
     * 从 Excel 导入科目数据
     *
     * @param file 上传的 Excel 文件
     * @return 导入结果信息 (例如: 成功导入 N 条)
     * @throws IOException IO异常
     */
    String importSubjects(MultipartFile file) throws IOException;

    /**
     * AI路由专用：根据参数Map查询科目信息
     */
    Object getSubjectInfo(Map<String, Object> params);

    /**
     * AI路由专用：根据参数Map统计科目数量
     */
    Object getSubjectCount(Map<String, Object> params);

} 