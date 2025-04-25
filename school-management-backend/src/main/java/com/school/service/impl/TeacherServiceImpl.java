/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-25 23:09:30
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 01:06:25
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\impl\TeacherServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service.impl;

import com.school.entity.Teacher;
import com.school.entity.TeacherSubject;
import com.school.entity.query.TeacherQuery;
import com.school.mapper.TeacherMapper;
import com.school.mapper.TeacherSubjectMapper;
import com.school.service.TeacherService;
import com.school.utils.ExcelUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 教师 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    private final TeacherMapper teacherMapper;
    private final TeacherSubjectMapper teacherSubjectMapper;

    @Override
    public IPage<Teacher> findTeachersByPage(TeacherQuery query) {
        Page<Teacher> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getTeacherName()), Teacher::getTeacherName, query.getTeacherName())
               .like(StrUtil.isNotBlank(query.getPhone()), Teacher::getPhone, query.getPhone())
               .eq(StrUtil.isNotBlank(query.getIdCard()), Teacher::getIdCard, query.getIdCard())
               .eq(StrUtil.isNotBlank(query.getGender()), Teacher::getGender, query.getGender())
               .eq(query.getIsValid() != null, Teacher::getIsValid, query.getIsValid());

        if (query.getSubjectId() != null) {
            LambdaQueryWrapper<TeacherSubject> tsWrapper = new LambdaQueryWrapper<>();
            tsWrapper.eq(TeacherSubject::getSubjectId, query.getSubjectId());
            List<TeacherSubject> teacherSubjects = teacherSubjectMapper.selectList(tsWrapper);
            List<Long> teacherIds = teacherSubjects.stream().map(TeacherSubject::getTeacherId).distinct().collect(Collectors.toList());
            if (CollUtil.isEmpty(teacherIds)) {
                return new Page<>(query.getPageNum(), query.getPageSize());
            }
            wrapper.in(Teacher::getId, teacherIds);
        }

        IPage<Teacher> teacherPage = teacherMapper.selectPage(page, wrapper);

        if (CollUtil.isNotEmpty(teacherPage.getRecords())) {
            List<Long> currentPageTeacherIds = teacherPage.getRecords().stream().map(Teacher::getId).collect(Collectors.toList());
            LambdaQueryWrapper<TeacherSubject> tsQueryWrapper = new LambdaQueryWrapper<>();
            tsQueryWrapper.in(TeacherSubject::getTeacherId, currentPageTeacherIds);
            List<TeacherSubject> allTeacherSubjects = teacherSubjectMapper.selectList(tsQueryWrapper);
            java.util.Map<Long, List<Long>> teacherSubjectMap = allTeacherSubjects.stream()
                    .collect(Collectors.groupingBy(TeacherSubject::getTeacherId,
                            Collectors.mapping(TeacherSubject::getSubjectId, Collectors.toList())));
            teacherPage.getRecords().forEach(teacher -> {
                teacher.setSubjectIds(teacherSubjectMap.get(teacher.getId()));
            });
        }
        return teacherPage;
    }

    @Override
    public Teacher getTeacherDetailById(Long id) {
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            return null;
        }
        LambdaQueryWrapper<TeacherSubject> tsWrapper = new LambdaQueryWrapper<>();
        tsWrapper.eq(TeacherSubject::getTeacherId, id);
        List<TeacherSubject> teacherSubjects = teacherSubjectMapper.selectList(tsWrapper);
        if (CollUtil.isNotEmpty(teacherSubjects)) {
            List<Long> subjectIds = teacherSubjects.stream()
                    .map(TeacherSubject::getSubjectId)
                    .collect(Collectors.toList());
            teacher.setSubjectIds(subjectIds);
        }
        return teacher;
    }

    @Override
    @Transactional
    public boolean addTeacher(Teacher teacher) {
        boolean saveResult = this.save(teacher);
        if (!saveResult) {
            return false;
        }
        List<Long> subjectIds = teacher.getSubjectIds();
        if (CollUtil.isNotEmpty(subjectIds)) {
            Long teacherId = teacher.getId();
            List<TeacherSubject> teacherSubjectList = subjectIds.stream()
                    .map(subjectId -> new TeacherSubject(null, teacherId, subjectId))
                    .collect(Collectors.toList());
            for (TeacherSubject ts : teacherSubjectList) {
                teacherSubjectMapper.insert(ts);
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updateTeacher(Teacher teacher) {
        boolean updateResult = this.updateById(teacher);
        if (!updateResult) {
            return false;
        }
        Long teacherId = teacher.getId();
        List<Long> subjectIds = teacher.getSubjectIds();

        LambdaQueryWrapper<TeacherSubject> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(TeacherSubject::getTeacherId, teacherId);
        teacherSubjectMapper.delete(deleteWrapper);

        if (CollUtil.isNotEmpty(subjectIds)) {
            List<TeacherSubject> teacherSubjectList = subjectIds.stream()
                    .map(subjectId -> new TeacherSubject(null, teacherId, subjectId))
                    .collect(Collectors.toList());
            for (TeacherSubject ts : teacherSubjectList) {
                teacherSubjectMapper.insert(ts);
            }
        }
        return true;
    }

    @Override
    @Transactional
    public boolean deleteTeacherById(Long id) {
        boolean removeResult = this.removeById(id);
        if (!removeResult) {
            return false;
        }
        LambdaQueryWrapper<TeacherSubject> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(TeacherSubject::getTeacherId, id);
        teacherSubjectMapper.delete(deleteWrapper);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteTeachersByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return true;
        }
        boolean removeResult = this.removeByIds(ids);
        if (!removeResult) {
            return false;
        }
        LambdaQueryWrapper<TeacherSubject> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.in(TeacherSubject::getTeacherId, ids);
        teacherSubjectMapper.delete(deleteWrapper);
        return true;
    }

    @Override
    public void exportTeachers(HttpServletResponse response, TeacherQuery query) throws IOException {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getTeacherName()), Teacher::getTeacherName, query.getTeacherName())
               .like(StrUtil.isNotBlank(query.getPhone()), Teacher::getPhone, query.getPhone())
               .eq(StrUtil.isNotBlank(query.getIdCard()), Teacher::getIdCard, query.getIdCard())
               .eq(StrUtil.isNotBlank(query.getGender()), Teacher::getGender, query.getGender())
               .eq(query.getIsValid() != null, Teacher::getIsValid, query.getIsValid());

        if (query.getSubjectId() != null) {
            LambdaQueryWrapper<TeacherSubject> tsWrapper = new LambdaQueryWrapper<>();
            tsWrapper.eq(TeacherSubject::getSubjectId, query.getSubjectId());
            List<TeacherSubject> teacherSubjects = teacherSubjectMapper.selectList(tsWrapper);
            List<Long> teacherIds = teacherSubjects.stream().map(TeacherSubject::getTeacherId).distinct().collect(Collectors.toList());
            if (CollUtil.isEmpty(teacherIds)) {
                ExcelUtil.exportExcel(response, "教师数据", "教师列表", Teacher.class, Collections.emptyList());
                return;
            }
            wrapper.in(Teacher::getId, teacherIds);
        }

        List<Teacher> teacherList = teacherMapper.selectList(wrapper);
        ExcelUtil.exportExcel(response, "教师数据", "教师列表", Teacher.class, teacherList);
    }

    /**
     * 从 Excel 导入教师数据
     * @param file 上传的 Excel 文件
     * @return 导入结果信息 (成功 N 条，失败 M 条，失败原因...)
     * @throws IOException IO异常
     */
    @Override
    @Transactional // 添加事务管理
    public String importTeachers(MultipartFile file) throws IOException {
        // List<Teacher> teacherList = ExcelUtil.importExcel(file.getInputStream(), Teacher.class, null); // 旧的错误调用
        List<Teacher> teacherList = ExcelUtil.importExcel(file, Teacher.class); // 正确的调用

        if (CollUtil.isEmpty(teacherList)) {
            return "导入失败：Excel 文件为空或格式不正确！";
        }

        int successCount = 0;
        int failureCount = 0;
        StringBuilder failureMsg = new StringBuilder();

        // 2. 遍历处理导入的数据
        for (int i = 0; i < teacherList.size(); i++) {
            Teacher teacher = teacherList.get(i);
            try {
                // 3. 数据校验 (部分校验已通过实体类注解 + EasyExcel 完成)
                // 这里可以添加更复杂的业务校验，例如身份证号是否已存在
                String idCard = teacher.getIdCard();
                if (StrUtil.isNotBlank(idCard)) {
                    LambdaQueryWrapper<Teacher> existCheck = new LambdaQueryWrapper<>();
                    existCheck.eq(Teacher::getIdCard, idCard);
                    if (teacherMapper.exists(existCheck)) {
                        failureCount++;
                        failureMsg.append("<br/>第 ").append(i + 2).append(" 行，身份证号 [ ").append(idCard).append(" ] 已存在;");
                        continue; // 跳过已存在的记录
                    }
                }
                // 补充校验：教师姓名不能为空等 (虽然实体类有注解，但最好再次确认)
                if (StrUtil.isBlank(teacher.getTeacherName())) {
                    failureCount++;
                    failureMsg.append("<br/>第 ").append(i + 2).append(" 行，教师姓名不能为空;");
                    continue;
                }

                // 4. 数据处理与插入 (不处理科目关联)
                // 实体类 set 方法会自动处理生日/性别/年龄计算
                this.save(teacher); // 调用 MybatisPlus 的 save 方法插入
                successCount++;
            } catch (Exception e) {
                failureCount++;
                String msg = "<br/>第 " + (i + 2) + " 行导入失败: ";
                failureMsg.append(msg).append(e.getMessage());
                log.error(msg, e); // 记录详细错误日志
            }
        }

        // 5. 返回导入结果
        if (failureCount > 0) {
            return StrUtil.format("成功导入 {} 条数据，失败 {} 条，失败详情: {}", successCount, failureCount, failureMsg.toString());
        } else {
            return StrUtil.format("成功导入全部 {} 条数据！", successCount);
        }
    }

    @Override
    public List<Teacher> listValidTeachersForSelection() {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Teacher::getId, Teacher::getTeacherName) // Only select ID and Name
               .eq(Teacher::getIsValid, 1) // Assuming 1 means valid
               .orderByAsc(Teacher::getTeacherName); // Optional: order by name
        return list(wrapper); // Use IService's list method
    }
    
    @Override
    public boolean existsById(Long id) {
        return baseMapper.exists(new LambdaQueryWrapper<Teacher>().eq(Teacher::getId, id));
    }
}