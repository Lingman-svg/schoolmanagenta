package com.school.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.Clazz;
import com.school.entity.Student;
import com.school.entity.StudentClazzHistory;
import com.school.entity.dto.StudentClazzHistoryDto;
import com.school.entity.query.StudentQuery;
import com.school.mapper.StudentClazzHistoryMapper;
import com.school.mapper.StudentMapper;
import com.school.service.ClazzService; // Inject ClazzService
import com.school.service.StudentService;
import com.school.utils.ExcelUtil;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 学生服务实现类
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final ClazzService clazzService; // Inject ClazzService for validation
    private final StudentClazzHistoryMapper studentClazzHistoryMapper; // Inject History Mapper

    @Override
    public Page<Student> listStudents(StudentQuery query) {
        Page<Student> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Student> wrapper = buildQueryWrapper(query);

        // TODO: Enhance query to join and display class name instead of just ID
        // For now, just query based on ID
        return baseMapper.selectPage(page, wrapper);
    }

    // Helper to build query wrapper
    private LambdaQueryWrapper<Student> buildQueryWrapper(StudentQuery query) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getStudentName()), Student::getStudentName, query.getStudentName());
        wrapper.eq(StringUtils.isNotBlank(query.getStudentNumber()), Student::getStudentNumber, query.getStudentNumber());
        wrapper.eq(StringUtils.isNotBlank(query.getGender()), Student::getGender, query.getGender());
        wrapper.like(StringUtils.isNotBlank(query.getPhone()), Student::getPhone, query.getPhone());
        wrapper.eq(query.getCurrentClazzId() != null, Student::getCurrentClazzId, query.getCurrentClazzId());
        // Add default sorting
        wrapper.orderByDesc(Student::getCreateTime);
        return wrapper;
    }


    @Override
    public Student getStudentInfo(Long id) {
        // TODO: Enhance to fetch and attach class name
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean addStudent(Student student) {
        // TODO: Implement student number generation logic if needed
        validateStudentData(student, false);
        boolean success = save(student); // Save student first to get the ID
        if (success && student.getId() != null && student.getCurrentClazzId() != null) {
            // Add initial class assignment history
            StudentClazzHistory initialHistory = StudentClazzHistory.builder()
                    .studentId(student.getId())
                    .clazzId(student.getCurrentClazzId())
                    .assignDate(LocalDateTime.now())
                    .removeDate(null) // Stays null for current assignment
                    .build();
            studentClazzHistoryMapper.insert(initialHistory);
        }
        return success;
    }

    @Override
    @Transactional
    public boolean updateStudent(Student student) {
        if (student.getId() == null || student.getId() <= 0) {
            throw new ValidationException("更新学生时必须提供有效的ID");
        }
        // Fetch original student BEFORE validation to compare class ID
        Student originalStudent = getById(student.getId());
        if (originalStudent == null) {
            throw new ValidationException("要更新的学生不存在 (ID: " + student.getId() + ")");
        }

        validateStudentData(student, true);

        LocalDateTime now = LocalDateTime.now();

        // Check if currentClazzId changed and log history
        if (!Objects.equals(originalStudent.getCurrentClazzId(), student.getCurrentClazzId())) {
            log.info("Student {} class changing from {} to {}", student.getId(), originalStudent.getCurrentClazzId(), student.getCurrentClazzId());

            // 1. End the previous history record (if any)
            LambdaUpdateWrapper<StudentClazzHistory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(StudentClazzHistory::getStudentId, student.getId())
                         .isNull(StudentClazzHistory::getRemoveDate) // Find the active record
                         .set(StudentClazzHistory::getRemoveDate, now); // Set remove date
            studentClazzHistoryMapper.update(null, updateWrapper);

            // 2. Add the new history record
            if (student.getCurrentClazzId() != null) { // Ensure new class ID is not null
                StudentClazzHistory newHistory = StudentClazzHistory.builder()
                        .studentId(student.getId())
                        .clazzId(student.getCurrentClazzId())
                        .assignDate(now)
                        .removeDate(null)
                        .build();
                studentClazzHistoryMapper.insert(newHistory);
            } else {
                log.warn("Student {} is being updated without a current class ID. No new history record created.", student.getId());
                // Handle this case based on business logic - maybe throw error if class is mandatory?
                 throw new ValidationException("更新学生时必须指定班级"); // Throw error if class is mandatory
            }
        }

        // Now update the student entity itself
        return updateById(student);
    }

    // Validation Helper
    private void validateStudentData(Student student, boolean isUpdate) {
        // 1. Basic non-null checks (already partially handled by entity setters/frontend)
        if (StrUtil.isBlank(student.getStudentName())) {
            throw new ValidationException("学生姓名不能为空");
        }
        if (StrUtil.isBlank(student.getStudentNumber())) { // Add check for studentNumber
            throw new ValidationException("学号不能为空");
        }

        // Check Student Number uniqueness
        checkStudentNumberUnique(student.getStudentNumber(), isUpdate ? student.getId() : null);

        // 2. ID Card validation and uniqueness
        if (StrUtil.isNotBlank(student.getIdCard())) {
            if (!cn.hutool.core.util.IdcardUtil.isValidCard(student.getIdCard())) {
                 throw new ValidationException("身份证号格式不正确");
            }
            checkIdCardUnique(student.getIdCard(), isUpdate ? student.getId() : null);
        } else {
             throw new ValidationException("身份证号不能为空"); // Assuming ID card is mandatory
        }

        // 3. Check if assigned Class exists and is valid (e.g., not '已结业')
        if (student.getCurrentClazzId() != null) {
            Clazz clazz = clazzService.getById(student.getCurrentClazzId());
            if (clazz == null || clazz.getDeleted() == 1) { // Check if deleted
                 throw new ValidationException("指定的班级不存在 (ID: " + student.getCurrentClazzId() + ")");
            }
            if (clazz.getStatus() == 2) { // Assuming 2 means '已结业'
                 throw new ValidationException("不能将学生分配到已结业的班级 (班级ID: " + student.getCurrentClazzId() + ")");
            }
        } else {
             throw new ValidationException("必须为学生指定班级"); // Assuming class is mandatory
        }
         // Add more validations as needed (e.g., student number unique)
    }

     private void checkIdCardUnique(String idCard, Long excludeId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getIdCard, idCard);
        if (excludeId != null) {
            wrapper.ne(Student::getId, excludeId);
        }
        if (baseMapper.exists(wrapper)) {
            throw new ValidationException("身份证号已被其他学生使用: " + idCard);
        }
    }

    // New helper method for student number uniqueness check
    private void checkStudentNumberUnique(String studentNumber, Long excludeId) {
        if (StrUtil.isBlank(studentNumber)) {
            // This should ideally be caught by @NotBlank, but double-check
            return; 
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getStudentNumber, studentNumber);
        if (excludeId != null) {
            wrapper.ne(Student::getId, excludeId); // Exclude self during update
        }
        if (baseMapper.exists(wrapper)) {
            throw new ValidationException("学号已被其他学生使用: " + studentNumber);
        }
    }

    // TODO: Implement method to log class change history
    // private void logClassChangeHistory(Long studentId, Long oldClazzId, Long newClazzId) { ... }

    @Override
    @Transactional
    public boolean deleteStudentLogically(Long id) {
        // TODO: When deleting a student, should we also mark their latest history record as removed?
         LocalDateTime now = LocalDateTime.now();
         LambdaUpdateWrapper<StudentClazzHistory> updateWrapper = new LambdaUpdateWrapper<>();
         updateWrapper.eq(StudentClazzHistory::getStudentId, id)
                      .isNull(StudentClazzHistory::getRemoveDate)
                      .set(StudentClazzHistory::getRemoveDate, now);
         studentClazzHistoryMapper.update(null, updateWrapper);
        
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean deleteStudentsLogicallyBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        // TODO: Handle history for batch deletion
        LocalDateTime now = LocalDateTime.now();
        LambdaUpdateWrapper<StudentClazzHistory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(StudentClazzHistory::getStudentId, ids)
                     .isNull(StudentClazzHistory::getRemoveDate)
                     .set(StudentClazzHistory::getRemoveDate, now);
        studentClazzHistoryMapper.update(null, updateWrapper);

        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importStudents(MultipartFile file) throws Exception {
        final List<String> errorMessages = new ArrayList<>();
        AtomicInteger currentRow = new AtomicInteger(2);

        Consumer<List<Student>> consumer = batchList -> {
            List<Student> validatedBatch = new ArrayList<>();
            for (Student student : batchList) {
                int rowNum = currentRow.getAndIncrement();
                try {
                    // Trigger setters for calculations
                    student.setIdCard(student.getIdCard()); // Recalculate birthDate, gender, age
                    student.setBirthDate(student.getBirthDate()); // Recalculate age if only birthDate is given

                    // Run validation logic
                    validateStudentData(student, false); // Use the same validation as addStudent

                    // If validation passes:
                    validatedBatch.add(student);

                } catch (ValidationException e) {
                    errorMessages.add("第 " + rowNum + " 行: " + e.getMessage());
                } catch (Exception e) {
                    errorMessages.add("第 " + rowNum + " 行处理异常: " + e.getMessage());
                    log.error("Error processing imported student at row {}: {}", rowNum, e.getMessage(), e);
                }
            }

            if (!validatedBatch.isEmpty()) {
                log.info("Saving batch of {} validated students.", validatedBatch.size());
                saveBatch(validatedBatch);
                // Add initial class assignment history for imported students
                List<StudentClazzHistory> historyBatch = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
                for (Student importedStudent : validatedBatch) {
                     if (importedStudent.getId() != null && importedStudent.getCurrentClazzId() != null) {
                        historyBatch.add(StudentClazzHistory.builder()
                                .studentId(importedStudent.getId())
                                .clazzId(importedStudent.getCurrentClazzId())
                                .assignDate(now)
                                .removeDate(null)
                                .build());
                     }
                }
                if (!historyBatch.isEmpty()){
                    // Assuming StudentClazzHistoryMapper supports batch insert or iterate insert
                     // For simplicity, iterate insert. Consider batch insert if performance needed.
                    historyBatch.forEach(studentClazzHistoryMapper::insert);
                }
            }
        };

        ExcelUtil.importExcelByListener(file, Student.class, consumer);

        if (!errorMessages.isEmpty()) {
            String combinedErrors = errorMessages.stream().collect(Collectors.joining("\n"));
            log.error("Student import failed with validation errors:\n{}", combinedErrors);
            throw new IllegalArgumentException("导入数据校验失败:\n" + combinedErrors);
        }

        int totalImported = currentRow.get() - 2 - errorMessages.size(); // Calculate successful imports
        log.info("Student import finished successfully. Imported {} records.", totalImported);
        if(errorMessages.isEmpty()){
             return "成功导入所有 " + totalImported + " 条数据！";
        } else {
             return "导入完成！成功 " + totalImported + " 条，失败 " + errorMessages.size() + " 条。详情请查看日志或返回信息。"; // Improve error reporting later
        }

    }

    @Override
    public List<Student> listStudentsForExport(StudentQuery query) {
        LambdaQueryWrapper<Student> wrapper = buildQueryWrapper(query);
         // TODO: Enhance export list to include class name?
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<StudentClazzHistoryDto> getClassChangeHistory(Long studentId) {
        if (studentId == null || studentId <= 0) {
            log.warn("Attempted to get class history for invalid student ID: {}", studentId);
            return new ArrayList<>(); // Return empty list for invalid ID
        }
        // Call the custom mapper method that performs the JOIN
        List<StudentClazzHistoryDto> historyDtoList = studentClazzHistoryMapper.selectHistoryWithClassName(studentId);
        
        return historyDtoList == null ? new ArrayList<>() : historyDtoList;
    }
} 