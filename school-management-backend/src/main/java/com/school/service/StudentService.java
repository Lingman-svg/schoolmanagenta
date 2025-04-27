/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 01:40:01
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 01:47:49
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\StudentService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Student;
import com.school.entity.dto.StudentClazzHistoryDto;
import com.school.entity.query.StudentQuery;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 *
 * @author LingMeng
 * @since 2025-04-26
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学生列表
     * @param query 查询参数
     * @return 分页结果 (Student 对象中可能需要关联班级名称等信息)
     */
    Page<Student> listStudents(StudentQuery query);

    /**
     * 根据ID获取学生详情 (可能需要关联班级信息)
     * @param id 学生ID
     * @return 学生详情
     */
    Student getStudentInfo(Long id);

    /**
     * 新增学生 (需要处理学号生成、身份证校验、班级关联等)
     * @param student 学生信息
     * @return 是否成功
     */
    boolean addStudent(Student student);

    /**
     * 修改学生信息 (需要处理班级变更记录等)
     * @param student 学生信息
     * @return 是否成功
     */
    boolean updateStudent(Student student);

    /**
     * 删除学生 (逻辑删除)
     * @param id 学生ID
     * @return 是否成功
     */
    boolean deleteStudentLogically(Long id);

    /**
     * 批量删除学生 (逻辑删除)
     * @param ids 学生ID列表
     * @return 是否成功
     */
    boolean deleteStudentsLogicallyBatch(List<Long> ids);

    /**
     * 从 Excel 导入学生数据
     * @param file 上传的 Excel 文件
     * @return 导入结果信息
     * @throws IOException IO异常
     */
    String importStudents(MultipartFile file) throws Exception; // Changed to throws Exception

    /**
     * 导出学生数据
     * @param query 查询参数
     * @return 学生列表 (用于导出)
     */
    List<Student> listStudentsForExport(StudentQuery query);

    /**
     * 查询学生班级变更历史
     * @param studentId 学生ID
     * @return 班级历史记录列表 (包含班级名称)
     */
    List<StudentClazzHistoryDto> getClassChangeHistory(Long studentId);

    /**
     * AI路由专用：根据参数Map查询学生信息
     */
    Object getStudentInfo(Map<String, Object> params);

    /**
     * AI路由专用：根据参数Map统计学生数量
     */
    Object getStudentCount(Map<String, Object> params);

     // TODO: 添加处理班级关联、变更历史的方法
     // boolean assignClass(Long studentId, Long clazzId);
} 