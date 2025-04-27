/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 23:07:50
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-05-10 17:22:23
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\TeacherService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Teacher;
import com.school.entity.query.TeacherQuery;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 教师 Service 接口
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 分页查询教师列表
     * @param query 查询条件，可能包含 subjectId 用于关联查询
     * @return 分页结果 (Teacher 对象中可能需要包含关联的科目信息)
     */
    IPage<Teacher> findTeachersByPage(TeacherQuery query);

    /**
     * 根据 ID 获取教师详情，并加载其关联的科目信息
     * @param id 教师ID
     * @return 教师对象 (包含 subjectIds 或 Subject 列表)
     */
    Teacher getTeacherDetailById(Long id);

    /**
     * 新增教师，并处理其关联的科目
     * @param teacher 教师信息，包含 subjectIds 列表
     * @return 是否成功
     */
    boolean addTeacher(Teacher teacher);

    /**
     * 修改教师信息，并更新其关联的科目
     * @param teacher 教师信息，包含 subjectIds 列表
     * @return 是否成功
     */
    boolean updateTeacher(Teacher teacher);

    /**
     * 删除教师 (逻辑删除)，并处理关联关系
     * @param id 教师ID
     * @return 是否成功
     */
    boolean deleteTeacherById(Long id);

    /**
     * 批量删除教师 (逻辑删除)，并处理关联关系
     * @param ids ID列表
     * @return 是否成功
     */
    boolean deleteTeachersByIds(List<Long> ids);

    /**
     * 导出教师数据到 Excel
     * @param response HttpServletResponse
     * @param query 查询条件
     * @throws IOException IO异常
     */
    void exportTeachers(HttpServletResponse response, TeacherQuery query) throws IOException;

    /**
     * 从 Excel 导入教师数据
     * @param file 上传的 Excel 文件
     * @return 导入结果信息
     * @throws IOException IO异常
     */
    String importTeachers(MultipartFile file) throws IOException;

    /**
     * 获取所有有效的教师列表 (通常用于下拉选择)
     * @return 有效教师列表 (可能只需要 ID 和 Name)
     */
    List<Teacher> listValidTeachersForSelection();

    boolean existsById(Long id); // Add this if not present

    /**
     * AI路由专用：根据参数Map查询教师信息
     */
    Object getTeacherInfo(Map<String, Object> params);

    /**
     * AI路由专用：根据参数Map查询教师所授科目
     */
    Object getTeacherSubjects(Map<String, Object> params);

} 