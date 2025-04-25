package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.Clazz;
import com.school.entity.query.ClazzQuery;
import com.school.entity.Teacher;
import com.school.mapper.ClazzMapper;
import com.school.service.ClazzService;
import com.school.service.TeacherService;
import com.school.utils.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import java.time.LocalDate;

/**
 * 班级服务实现类
 *
 * @author LingMeng
 * @since 2025-04-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {

    private final TeacherService teacherService;

    @Override
    public Page<Clazz> listClazzes(ClazzQuery query) {
        Page<Clazz> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Clazz> wrapper = buildQueryWrapper(query); // Extract wrapper building
        return baseMapper.selectPage(page, wrapper);
    }

    // Extracted method to build query wrapper
    private LambdaQueryWrapper<Clazz> buildQueryWrapper(ClazzQuery query) {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getClassName()), Clazz::getClassName, query.getClassName());
        wrapper.eq(StringUtils.isNotBlank(query.getGrade()), Clazz::getGrade, query.getGrade());
        wrapper.eq(query.getTeacherId() != null, Clazz::getTeacherId, query.getTeacherId());
        wrapper.eq(query.getStatus() != null, Clazz::getStatus, query.getStatus());
        wrapper.ge(query.getStartDateBegin() != null, Clazz::getStartDate, query.getStartDateBegin());
        wrapper.le(query.getStartDateEnd() != null, Clazz::getStartDate, query.getStartDateEnd());
        wrapper.orderByDesc(Clazz::getCreateTime); // Default sort by creation time descending
        return wrapper;
    }

    @Override
    public Clazz getClazzInfo(Long id) {
        // TODO: Implement logic to potentially join with Teacher info later
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean addClazz(Clazz clazz) {
        // Calculate status based on dates *before* validation (validation might depend on status)
        updateStatusBasedOnDate(clazz); // Update status first
        validateClazzData(clazz, false); // Then validate

        // Set default only if still null after calculation
        if (clazz.getStatus() == null) {
            clazz.setStatus(0); // Default to 未开班 if dates don't allow calculation
        }
        return save(clazz);
    }

    @Override
    @Transactional
    public boolean updateClazz(Clazz clazz) {
        if (clazz.getId() == null) {
             throw new ValidationException("更新班级时必须提供ID");
        }
        // Calculate status based on dates *before* validation
        updateStatusBasedOnDate(clazz); // Update status first
        validateClazzData(clazz, true); // Then validate

         // Set default only if still null after calculation
        if (clazz.getStatus() == null) {
            // For update, maybe preserve original status or handle differently?
            // Let's default to 0 if dates don't allow calculation and it wasn't set
             Clazz originalClazz = getById(clazz.getId());
             if(originalClazz != null && originalClazz.getStatus() != null) {
                 clazz.setStatus(originalClazz.getStatus()); // Keep original if calc fails
             } else {
                 clazz.setStatus(0); // Default if all else fails
             }
        }
        return updateById(clazz);
    }

    @Override
    @Transactional
    public boolean deleteClazzLogically(Long id) {
        // ServiceImpl's removeById handles logical delete if @TableLogic is configured
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean deleteClazzesLogicallyBatch(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importClazzes(MultipartFile file) throws Exception {
        final List<String> errorMessages = new ArrayList<>();
        AtomicInteger currentRow = new AtomicInteger(2);

        Consumer<List<Clazz>> consumer = batchList -> {
            List<Clazz> validatedBatch = new ArrayList<>();
            for (Clazz clazz : batchList) {
                int rowNum = currentRow.getAndIncrement();
                try {
                     // --- Calculate Status based on dates first ---
                    updateStatusBasedOnDate(clazz);

                    // --- Run Validation Logic ---
                    if (StringUtils.isBlank(clazz.getClassName())) {
                         throw new ValidationException("班级名称不能为空");
                    }
                    if (clazz.getStartDate() == null) {
                         throw new ValidationException("开班日期不能为空");
                    }
                    if (StringUtils.isBlank(clazz.getGrade())){
                         throw new ValidationException("年级不能为空");
                    }
                    if (clazz.getTeacherId() == null) {
                         throw new ValidationException("班主任ID不能为空");
                    }

                    // Set default status *after* calculation attempt and *before* full validation if needed
                     if (clazz.getStatus() == null) {
                         clazz.setStatus(0); // Default to 未开班 if calculation wasn't possible
                    }

                    // Re-use validation logic (which now also checks status validity)
                    validateClazzData(clazz, false);

                    validatedBatch.add(clazz);

                } catch (ValidationException e) {
                    errorMessages.add("第 " + rowNum + " 行: " + e.getMessage());
                } catch (Exception e) {
                     errorMessages.add("第 " + rowNum + " 行处理异常: " + e.getMessage());
                     log.error("Error processing imported class at row {}: {}", rowNum, e.getMessage(), e);
                }
            }
            if (!validatedBatch.isEmpty()) {
                log.info("Saving batch of {} validated classes.", validatedBatch.size());
                saveBatch(validatedBatch);
            }
        };
        ExcelUtil.importExcelByListener(file, Clazz.class, consumer);
        if (!errorMessages.isEmpty()) {
            String combinedErrors = errorMessages.stream().collect(Collectors.joining("\n"));
            log.error("Class import failed with validation errors:\n{}", combinedErrors);
            throw new IllegalArgumentException("导入数据校验失败:\n" + combinedErrors);
        }
        log.info("Class import finished successfully.");
    }

    @Override
    public List<Clazz> listClazzesForExport(ClazzQuery query) {
        LambdaQueryWrapper<Clazz> wrapper = buildQueryWrapper(query);
        // Fetch all matching records, ignoring pagination from the query object
        return baseMapper.selectList(wrapper);
    }

    // --- Validation Helper ---
    private void validateClazzData(Clazz clazz, boolean isUpdate) {
        // 1. Check unique class name within the grade
        checkClazzNameUnique(clazz.getClassName(), clazz.getGrade(), isUpdate ? clazz.getId() : null);

        // 2. Check if teacher exists and is valid
        if (clazz.getTeacherId() != null) {
            // Assuming TeacherService has existsById or a similar check
            // boolean teacherExists = teacherService.existsById(clazz.getTeacherId());
            Teacher teacher = teacherService.getById(clazz.getTeacherId()); // More robust: check if teacher is valid too
            if (teacher == null || teacher.getIsValid() == 0) { // Assuming 0 means invalid
                 throw new ValidationException("指定的班主任不存在或无效 (ID: " + clazz.getTeacherId() + ")");
            }
        } else {
             throw new ValidationException("必须指定班主任"); // TeacherId is required by frontend rules
        }

        // 3. Check if endDate is after startDate
        if (clazz.getStartDate() != null && clazz.getEndDate() != null) {
            if (clazz.getEndDate().isBefore(clazz.getStartDate())) {
                throw new ValidationException("结业日期不能早于开班日期");
            }
        }

        // 4. Check status validity (optional, as frontend should enforce it)
         if (clazz.getStatus() != null && (clazz.getStatus() < 0 || clazz.getStatus() > 2)) {
            throw new ValidationException("无效的班级状态值");
         }
    }

    private void checkClazzNameUnique(String className, String grade, Long excludeId) {
        if (StringUtils.isBlank(className) || StringUtils.isBlank(grade)) {
            return; // Basic non-null checks should be done before this
        }
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clazz::getClassName, className)
               .eq(Clazz::getGrade, grade);
        if (excludeId != null) {
            wrapper.ne(Clazz::getId, excludeId);
        }
        if (baseMapper.exists(wrapper)) {
            throw new ValidationException("同年级下已存在同名班级: " + grade + " " + className);
        }
    }

    // --- Helper method to calculate and update status ---
    private void updateStatusBasedOnDate(Clazz clazz) {
        if (clazz.getStartDate() != null && clazz.getEndDate() != null) {
            LocalDate today = LocalDate.now();
            if (today.isBefore(clazz.getStartDate())) {
                clazz.setStatus(0); // 未开班
            } else if (today.isAfter(clazz.getEndDate())) {
                clazz.setStatus(2); // 已结业
            } else {
                clazz.setStatus(1); // 进行中
            }
        }
        // If dates are not complete, don't automatically change status here
        // Let the calling method handle defaults if needed.
    }

    @Scheduled(fixedRate = 1800000) // Run every 30 minutes (30 * 60 * 1000 ms)
    @Transactional // Ensure the whole update process is transactional
    public void updateClazzStatusesScheduled() {
        log.info("Starting scheduled task: Update Clazz Statuses...");
        LocalDate today = LocalDate.now();
        int updatedCount = 0;

        // 1. Find classes that *might* need status updates (not deleted, dates present)
        LambdaQueryWrapper<Clazz> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Clazz::getDeleted, 0) // Not deleted
                    .isNotNull(Clazz::getStartDate)
                    .isNotNull(Clazz::getEndDate);
                    // Optimization: Optionally query only those whose status might change
                    // .and(wq -> wq.ne(Clazz::getStatus, 2).lt(Clazz::getEndDate, today) // Should be 2
                    //             .or(wq2 -> wq2.ne(Clazz::getStatus, 1)
                    //                           .ge(Clazz::getStartDate, today)
                    //                           .le(Clazz::getEndDate, today)) // Should be 1
                    //             .or(wq3 -> wq3.ne(Clazz::getStatus, 0).gt(Clazz::getStartDate, today)) // Should be 0
                    // );


        List<Clazz> candidates = list(queryWrapper);
        List<Clazz> toUpdate = new ArrayList<>();

        // 2. Iterate and check status
        for (Clazz clazz : candidates) {
            Integer currentStatus = clazz.getStatus();
            Integer expectedStatus = calculateStatus(clazz.getStartDate(), clazz.getEndDate()); // Use helper

            if (expectedStatus != null && !expectedStatus.equals(currentStatus)) {
                clazz.setStatus(expectedStatus);
                toUpdate.add(clazz); // Add to update list
                updatedCount++;
                log.debug("Class ID {} status changing from {} to {}", clazz.getId(), currentStatus, expectedStatus);
            }
        }

        // 3. Batch update changed classes
        if (!toUpdate.isEmpty()) {
            log.info("Updating status for {} classes.", updatedCount);
            updateBatchById(toUpdate); // Efficient batch update
        } else {
            log.info("No class statuses needed updating.");
        }
        log.info("Finished scheduled task: Update Clazz Statuses.");
    }

    // --- Helper to calculate status based on LocalDate ---
    // (Could be reused by updateStatusBasedOnDate if refactored slightly)
    private Integer calculateStatus(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null; // Cannot calculate without both dates
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return 0; // 未开班
        } else if (today.isAfter(endDate)) {
            return 2; // 已结业
        } else {
            return 1; // 进行中
        }
    }
} 