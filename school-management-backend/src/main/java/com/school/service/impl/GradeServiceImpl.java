/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 16:10:11
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 16:23:28
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\impl\GradeServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.*; // Import related entities
import com.school.entity.dto.GradeDto; // Import GradeDto
import com.school.entity.query.GradeQuery;
import com.school.entity.vo.GradeExcelVo; // Import GradeExcelVo
import com.school.mapper.GradeMapper;
import com.school.service.*; // Import related services for validation
import com.school.utils.ExcelUtil; // Import ExcelUtil
import jakarta.servlet.http.HttpServletResponse; // Import HttpServletResponse
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; // Import IOException
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList; // Import ArrayList
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger; // Import AtomicInteger
import java.util.function.Consumer; // Import Consumer
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 成绩服务实现类
 *
 * @author Gemini
 * @since 2024-05-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    // 注入需要的 Service 用于校验关联数据是否存在
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final ClazzService clazzService;
    private final TeacherService teacherService; // Optional teacher validation

    @Override
    public Page<GradeDto> listGrades(GradeQuery query) {
        Page<Grade> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Grade> wrapper = buildQueryWrapper(query);

        Page<Grade> gradePage = baseMapper.selectPage(page, wrapper);

        // Convert Page<Grade> to Page<GradeDto>
        Page<GradeDto> dtoPage = new Page<>(gradePage.getCurrent(), gradePage.getSize(), gradePage.getTotal());
        dtoPage.setPages(gradePage.getPages());

        List<Grade> gradeList = gradePage.getRecords();
        if (CollUtil.isEmpty(gradeList)) {
            dtoPage.setRecords(Collections.emptyList());
            return dtoPage;
        }

        // 1. Collect IDs
        Set<Long> studentIds = gradeList.stream().map(Grade::getStudentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> subjectIds = gradeList.stream().map(Grade::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> clazzIds = gradeList.stream().map(Grade::getClazzId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> teacherIds = gradeList.stream().map(Grade::getTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());

        // 2. Query related entities in batch
        Map<Long, String> studentNameMap = getEntityNameMap(studentIds, studentService::listByIds, Student::getId, Student::getStudentName);
        Map<Long, String> subjectNameMap = getEntityNameMap(subjectIds, subjectService::listByIds, Subject::getId, Subject::getSubjectName);
        Map<Long, String> clazzNameMap = getEntityNameMap(clazzIds, clazzService::listByIds, Clazz::getId, Clazz::getClassName);
        Map<Long, String> teacherNameMap = getEntityNameMap(teacherIds, teacherService::listByIds, Teacher::getId, Teacher::getTeacherName);

        // 3. Convert Grade to GradeDto and set names
        List<GradeDto> dtoList = gradeList.stream().map(grade -> {
            GradeDto dto = BeanUtil.copyProperties(grade, GradeDto.class); // Use BeanUtil for copying
            dto.setStudentName(studentNameMap.get(grade.getStudentId()));
            dto.setSubjectName(subjectNameMap.get(grade.getSubjectId()));
            dto.setClazzName(clazzNameMap.get(grade.getClazzId()));
            if (grade.getTeacherId() != null) {
                dto.setTeacherName(teacherNameMap.get(grade.getTeacherId()));
            }
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    // Helper method to get ID-Name Map
    private <T> Map<Long, String> getEntityNameMap(Set<Long> ids,
                                                   Function<Set<Long>, List<T>> queryFunction,
                                                   Function<T, Long> idExtractor,
                                                   Function<T, String> nameExtractor) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<T> entities = queryFunction.apply(ids);
        if (CollUtil.isEmpty(entities)) {
            return Collections.emptyMap();
        }
        return entities.stream().collect(Collectors.toMap(idExtractor, nameExtractor, (k1, k2) -> k1)); // Avoid duplicate keys
    }

    private LambdaQueryWrapper<Grade> buildQueryWrapper(GradeQuery query) {
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getStudentId() != null, Grade::getStudentId, query.getStudentId());
        wrapper.eq(query.getSubjectId() != null, Grade::getSubjectId, query.getSubjectId());
        wrapper.eq(query.getClazzId() != null, Grade::getClazzId, query.getClazzId());
        wrapper.eq(query.getTeacherId() != null, Grade::getTeacherId, query.getTeacherId());
        wrapper.like(StrUtil.isNotBlank(query.getExamName()), Grade::getExamName, query.getExamName());
        // 默认按录入时间倒序
        wrapper.orderByDesc(Grade::getRecordTime, Grade::getCreateTime);
        return wrapper;
    }

    @Override
    @Transactional
    public boolean addGrade(Grade grade) {
        validateGradeData(grade, false);
        // 设置默认录入时间
        if (grade.getRecordTime() == null) {
            grade.setRecordTime(LocalDateTime.now());
        }
        return save(grade);
    }

    @Override
    @Transactional
    public boolean updateGrade(Grade grade) {
        if (grade.getId() == null || grade.getId() <= 0) {
            throw new ValidationException("更新成绩时必须提供有效的ID");
        }
        // 检查原始成绩是否存在
        Grade originalGrade = baseMapper.selectById(grade.getId());
        if (originalGrade == null) {
            throw new ValidationException("要更新的成绩记录不存在 (ID: " + grade.getId() + ")");
        }
        validateGradeData(grade, true);
        // 更新时不自动修改录入时间，除非明确传入
        return updateById(grade);
    }

    // 基础校验逻辑
    private void validateGradeData(Grade grade, boolean isUpdate) {
        if (grade.getStudentId() == null) {
            throw new ValidationException("学生ID不能为空");
        }
        if (grade.getSubjectId() == null) {
            throw new ValidationException("科目ID不能为空");
        }
        if (grade.getClazzId() == null) {
            throw new ValidationException("班级ID不能为空");
        }
        if (StrUtil.isBlank(grade.getExamName())) {
            throw new ValidationException("考试名称不能为空");
        }
        if (grade.getScore() == null) {
            throw new ValidationException("成绩分数不能为空");
        }

        // 分数范围校验 (例如 0-100)
        if (grade.getScore().compareTo(BigDecimal.ZERO) < 0 || grade.getScore().compareTo(new BigDecimal("100")) > 0) {
            // TODO: 分数范围可以根据科目配置进行调整
            log.warn("成绩分数 {} 超出常规范围 [0, 100]", grade.getScore());
             throw new ValidationException("成绩分数必须在 0 到 100 之间"); // 暂时硬编码
        }

        // 检查关联数据是否存在
        if (studentService.getById(grade.getStudentId()) == null) {
            throw new ValidationException("关联的学生不存在 (ID: " + grade.getStudentId() + ")");
        }
        if (subjectService.getById(grade.getSubjectId()) == null) {
            throw new ValidationException("关联的科目不存在 (ID: " + grade.getSubjectId() + ")");
        }
        if (clazzService.getById(grade.getClazzId()) == null) {
            throw new ValidationException("关联的班级不存在 (ID: " + grade.getClazzId() + ")");
        }
        if (grade.getTeacherId() != null && teacherService.getById(grade.getTeacherId()) == null) {
            throw new ValidationException("关联的教师不存在 (ID: " + grade.getTeacherId() + ")");
        }

        // 检查是否重复录入 (同一学生、同一科目、同一班级、同一考试名称)
        checkDuplicateGrade(grade, isUpdate ? grade.getId() : null);

    }

    // 检查重复成绩记录
    private void checkDuplicateGrade(Grade grade, Long excludeId) {
        LambdaQueryWrapper<Grade> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Grade::getStudentId, grade.getStudentId());
        wrapper.eq(Grade::getSubjectId, grade.getSubjectId());
        wrapper.eq(Grade::getClazzId, grade.getClazzId());
        wrapper.eq(Grade::getExamName, grade.getExamName());
        if (excludeId != null) {
            wrapper.ne(Grade::getId, excludeId); // 更新时排除自身
        }
        if (baseMapper.exists(wrapper)) {
            throw new ValidationException("已存在该学生在此次考试中的该科目成绩记录");
        }
    }


    @Override
    @Transactional
    public boolean deleteGrade(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("删除成绩时必须提供有效的ID");
        }
        // 逻辑删除
        return removeById(id);
    }

    @Override
    public Grade getGradeInfo(Long id) {
         if (id == null || id <= 0) {
            throw new ValidationException("获取成绩详情时必须提供有效的ID");
        }
        // TODO: 未来可以考虑返回 DTO
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean deleteGradesBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除成绩，ID列表为空");
            return false; // 或者抛出异常，取决于业务需求
        }
        log.info("批量删除成绩，ID列表: {}", ids);
        // 逻辑删除
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // Ensure rollback on any exception
    public String importGrades(MultipartFile file) throws Exception {
        final List<String> errorMessages = new ArrayList<>();
        AtomicInteger currentRow = new AtomicInteger(2); // Excel row starts from 1, header is 1, data starts from 2
        final AtomicInteger successCount = new AtomicInteger(0);

        Consumer<List<GradeExcelVo>> consumer = batchList -> {
            List<Grade> validatedBatch = new ArrayList<>();
            for (GradeExcelVo vo : batchList) {
                int rowNum = currentRow.getAndIncrement();
                try {
                    // Convert VO to Entity
                    Grade grade = BeanUtil.copyProperties(vo, Grade.class);

                    // Set default record time if not provided
                    if (grade.getRecordTime() == null) {
                        grade.setRecordTime(LocalDateTime.now());
                    }

                    // Run validation logic (using existing validateGradeData)
                    // Note: validateGradeData checks for ID existence, which is good.
                    // It also checks for duplicates, which is also important for import.
                    validateGradeData(grade, false); // false for isUpdate

                    // If validation passes:
                    validatedBatch.add(grade);

                } catch (ValidationException e) {
                    errorMessages.add("第 " + rowNum + " 行: " + e.getMessage());
                } catch (Exception e) {
                    errorMessages.add("第 " + rowNum + " 行处理异常: " + e.getMessage());
                    log.error("Error processing imported grade at row {}: {}", rowNum, e.getMessage(), e);
                }
            }

            if (!validatedBatch.isEmpty()) {
                log.info("Saving batch of {} validated grades.", validatedBatch.size());
                boolean batchSuccess = saveBatch(validatedBatch); // Use MybatisPlus batch insert
                if(batchSuccess){
                    successCount.addAndGet(validatedBatch.size());
                } else {
                    log.error("Batch save failed for rows starting near: {}", currentRow.get() - validatedBatch.size());
                    // Add error message for the whole batch if saveBatch fails
                    errorMessages.add("批次保存失败 (行号约 " + (currentRow.get() - validatedBatch.size()) + " 附近)");
                }
            }
        };

        // Use ExcelUtil with listener
        ExcelUtil.importExcelByListener(file, GradeExcelVo.class, consumer);

        int totalProcessed = currentRow.get() - 2;
        int finalSuccessCount = successCount.get();
        int failureCount = errorMessages.size(); // Approximation, as one batch failure affects multiple rows
        log.info("Grade import finished. Total Processed: {}, Success: {}, Failures: {}", totalProcessed, finalSuccessCount, failureCount);

        if (!errorMessages.isEmpty()) {
            String combinedErrors = String.join("\n", errorMessages);
            // Ensure the exception message is not too long for potential frontend display
             String finalMessage = String.format("导入完成！成功 %d 条，失败 %d 条。失败详情:\n%s",
                                                finalSuccessCount, failureCount, combinedErrors);
             if (finalMessage.length() > 1000) { // Limit message length
                  finalMessage = String.format("导入完成！成功 %d 条，失败 %d 条。失败详情过长，请查看日志。",
                                                 finalSuccessCount, failureCount);
             }
            log.error("Grade import failed with validation errors:\n{}", combinedErrors);
            // Throw exception to indicate failure and provide detailed errors
            throw new IllegalArgumentException(finalMessage);
        } else {
            return String.format("成功导入所有 %d 条数据！", finalSuccessCount);
        }
    }

    @Override
    public void exportGrades(HttpServletResponse response, GradeQuery query) throws IOException {
        // 1. Query the data based on query parameters (no pagination needed for export)
        LambdaQueryWrapper<Grade> wrapper = buildQueryWrapper(query);
        List<Grade> gradeList = baseMapper.selectList(wrapper);

        if (CollUtil.isEmpty(gradeList)) {
            log.info("导出成绩数据为空，查询条件: {}", query);
            // Export empty file with headers
             ExcelUtil.exportExcel(response, "成绩数据", "成绩列表", GradeExcelVo.class, Collections.emptyList());
            return;
        }

        // 2. Convert Grade list to GradeExcelVo list (similar to listGrades, but for ExcelVo)
         // Collect IDs
        Set<Long> studentIds = gradeList.stream().map(Grade::getStudentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> subjectIds = gradeList.stream().map(Grade::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> clazzIds = gradeList.stream().map(Grade::getClazzId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> teacherIds = gradeList.stream().map(Grade::getTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());

        // Query related entities in batch
        Map<Long, String> studentNameMap = getEntityNameMap(studentIds, studentService::listByIds, Student::getId, Student::getStudentName);
        Map<Long, String> subjectNameMap = getEntityNameMap(subjectIds, subjectService::listByIds, Subject::getId, Subject::getSubjectName);
        Map<Long, String> clazzNameMap = getEntityNameMap(clazzIds, clazzService::listByIds, Clazz::getId, Clazz::getClassName);
        Map<Long, String> teacherNameMap = getEntityNameMap(teacherIds, teacherService::listByIds, Teacher::getId, Teacher::getTeacherName);

        // Convert Grade to GradeExcelVo and set names
        List<GradeExcelVo> excelVoList = gradeList.stream().map(grade -> {
            GradeExcelVo vo = BeanUtil.copyProperties(grade, GradeExcelVo.class);
            vo.setStudentName(studentNameMap.get(grade.getStudentId()));
            vo.setSubjectName(subjectNameMap.get(grade.getSubjectId()));
            vo.setClazzName(clazzNameMap.get(grade.getClazzId()));
            if (grade.getTeacherId() != null) {
                vo.setTeacherName(teacherNameMap.get(grade.getTeacherId()));
            }
            return vo;
        }).collect(Collectors.toList());

        // 3. Use ExcelUtil to export
        log.info("开始导出成绩数据，共 {} 条，查询条件: {}", excelVoList.size(), query);
        ExcelUtil.exportExcel(response, "成绩数据", "成绩列表", GradeExcelVo.class, excelVoList);
        log.info("导出成绩数据完成.");
    }

} 