package com.school.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.Subject;
import com.school.entity.query.SubjectQuery;
import com.school.mapper.SubjectMapper;
import com.school.service.SubjectService;
import com.school.utils.ExcelUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 科目 Service 实现类
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    private static final Logger log = LoggerFactory.getLogger(SubjectServiceImpl.class);

    // 推荐使用 @Resource (符合 JSR-250 标准)
    @Resource
    private SubjectMapper subjectMapper;

    @Override
    public IPage<Subject> findSubjectsByPage(SubjectQuery query) {
        Page<Subject> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        wrapper.like(StrUtil.isNotBlank(query.getSubjectCode()), Subject::getSubjectCode, query.getSubjectCode());
        wrapper.like(StrUtil.isNotBlank(query.getSubjectName()), Subject::getSubjectName, query.getSubjectName());
        wrapper.eq(query.getIsValid() != null, Subject::getIsValid, query.getIsValid());

        // 添加排序 (根据 PageDomain 中的排序字段)
        if (StrUtil.isNotBlank(query.getOrderByColumn())) {
            boolean isAsc = "asc".equalsIgnoreCase(query.getIsAsc());
            // 这里需要注意驼峰和下划线的转换，或者直接在前端传递数据库列名
            // 假设前端传递的是驼峰命名，我们需要转换或直接使用列名
            // 为了简单起见，这里假设前端传递的是数据库列名或实体字段名
            // 如果需要更安全的处理，可以映射字段
            wrapper.orderBy(true, isAsc, Subject::getCreateTime); // 默认按创建时间排序，可以修改
            // wrapper.orderBy(true, isAsc, query.getOrderByColumn()); // 这样写不安全，不推荐
        } else {
            // 默认排序
            wrapper.orderByDesc(Subject::getCreateTime);
        }

        return subjectMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Subject> findAllValidSubjects() {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getIsValid, 1); // 假设 1 代表有效
        wrapper.orderByAsc(Subject::getSubjectCode); // 按代号排序
        return subjectMapper.selectList(wrapper);
    }

    @Override
    @Transactional // 添加事务管理
    public boolean addSubject(Subject subject) {
        // TODO: 可以在这里添加业务校验，例如科目代码/名称是否已存在
        // checkSubjectExists(subject.getSubjectCode(), subject.getSubjectName(), null);
        // MybatisPlus 自动填充创建时间和创建人
        return this.save(subject);
    }

    @Override
    @Transactional
    public boolean updateSubject(Subject subject) {
        if (subject.getId() == null) {
            // 或者抛出业务异常
            return false;
        }
        // TODO: 业务校验
        // checkSubjectExists(subject.getSubjectCode(), subject.getSubjectName(), subject.getId());
        // MybatisPlus 自动填充更新时间和更新人
        return this.updateById(subject);
    }

    @Override
    @Transactional
    public boolean deleteSubjectById(Long id) {
        // MybatisPlus 的 removeById 会处理 @TableLogic 逻辑删除
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean deleteSubjectsByIds(List<Long> ids) {
        return this.removeByIds(ids);
    }

    @Override
    public void exportSubjects(HttpServletResponse response, SubjectQuery query) throws IOException {
        // 1. 查询数据 (不分页，查询符合条件的所有数据)
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getSubjectCode()), Subject::getSubjectCode, query.getSubjectCode());
        wrapper.like(StrUtil.isNotBlank(query.getSubjectName()), Subject::getSubjectName, query.getSubjectName());
        wrapper.eq(query.getIsValid() != null, Subject::getIsValid, query.getIsValid());
        wrapper.orderByDesc(Subject::getCreateTime);
        List<Subject> subjectList = subjectMapper.selectList(wrapper);

        // 2. 使用 ExcelUtil 导出
        ExcelUtil.exportExcel(response, "科目数据.xlsx", "科目列表", Subject.class, subjectList);
    }

    @Override
    @Transactional // 导入通常也需要事务
    public String importSubjects(MultipartFile file) throws IOException {
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failureCount = new AtomicInteger(0);
        StringBuilder failureMsg = new StringBuilder();

        ExcelUtil.importExcelByListener(file, Subject.class, dataList -> {
            for (Subject subject : dataList) {
                try {
                    // TODO: 添加更严格的校验逻辑
                    if (StrUtil.isBlank(subject.getSubjectCode()) || StrUtil.isBlank(subject.getSubjectName())) {
                        failureCount.incrementAndGet();
                        failureMsg.append("第 ").append(successCount.get() + failureCount.get()).append(" 行数据错误：科目代码和名称不能为空<br/>");
                        continue;
                    }

                    // 校验科目是否存在 (根据 code 或 name)
                    boolean exists = checkSubjectExists(subject.getSubjectCode(), subject.getSubjectName(), null);
                    if (exists) {
                        // 可以选择更新或者跳过
                        // LambdaQueryWrapper<Subject> updateWrapper = new LambdaQueryWrapper<>();
                        // updateWrapper.eq(Subject::getSubjectCode, subject.getSubjectCode());
                        // // 设置需要更新的字段...
                        // boolean updated = this.update(subject, updateWrapper);
                        // if(updated) successCount.incrementAndGet(); else failureCount.incrementAndGet();
                        failureCount.incrementAndGet();
                        failureMsg.append("第 ").append(successCount.get() + failureCount.get()).append(" 行数据错误：科目代码或名称已存在<br/>");
                        log.warn("Subject already exists, skipping: Code={}, Name={}", subject.getSubjectCode(), subject.getSubjectName());

                    } else {
                        // 不存在则插入
                        boolean saved = this.addSubject(subject);
                        if (saved) {
                            successCount.incrementAndGet();
                        } else {
                            failureCount.incrementAndGet();
                            failureMsg.append("第 ").append(successCount.get() + failureCount.get()).append(" 行数据保存失败<br/>");
                        }
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    failureMsg.append("第 ").append(successCount.get() + failureCount.get()).append(" 行数据处理异常: ").append(e.getMessage()).append("<br/>");
                    log.error("Error processing imported subject at row {}: {}", successCount.get() + failureCount.get(), e.getMessage(), e);
                }
            }
        });

        if (failureCount.get() > 0) {
            return StrUtil.format("导入完成！成功 {} 条，失败 {} 条。<br/>失败详情:<br/>{}", successCount.get(), failureCount.get(), failureMsg.toString());
        } else {
            return StrUtil.format("导入成功！共 {} 条数据。", successCount.get());
        }
    }

    /**
     * 校验科目是否存在 (根据 Code 或 Name)
     * @param code 科目代码
     * @param name 科目名称
     * @param excludeId 要排除的ID (用于更新时校验)
     * @return 是否存在
     */
    private boolean checkSubjectExists(String code, String name, Long excludeId) {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        // 使用 or 连接 code 和 name 查询
        wrapper.eq(Subject::getSubjectCode, code).or().eq(Subject::getSubjectName, name);
        // 如果是更新操作，排除自身
        if (excludeId != null) {
            wrapper.ne(Subject::getId, excludeId);
        }
        // 查询记录数
        Long count = subjectMapper.selectCount(wrapper);
        return count > 0;
    }
} 