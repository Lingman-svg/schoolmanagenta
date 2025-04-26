package com.school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.entity.Subject;
import com.school.entity.query.SubjectQuery;
import com.school.service.SubjectService;
import com.school.utils.R; // 引入统一响应结果类
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid; // 用于 @Valid 注解
import org.springframework.validation.annotation.Validated; // 用于 @Validated 注解
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 科目管理 Controller
 */
@RestController
@RequestMapping("/api/subjects") // 定义基础路由
@Validated // 开启方法级别的参数校验
public class SubjectController {

    private static final Logger log = LoggerFactory.getLogger(SubjectController.class);

    @Resource
    private SubjectService subjectService;

    /**
     * 分页查询科目列表
     * 使用 @Validated 注解来校验 SubjectQuery 中的分页参数 (如果 PageDomain 中有约束)
     */
    @GetMapping
    public R<IPage<Subject>> list(SubjectQuery query) {
        IPage<Subject> page = subjectService.findSubjectsByPage(query);
        return R.success(page);
    }

    /**
     * 获取所有有效科目列表 (用于下拉)
     */
    @Operation(summary = "获取所有有效科目列表 (用于下拉)")
    @GetMapping("/valid")
    public R<List<Subject>> listValid() {
        log.info("获取所有有效科目列表");
        List<Subject> list = subjectService.findAllValidSubjects();
        return R.success(list);
    }

    /**
     * 根据 ID 获取科目详情
     */
    @GetMapping("/{id}")
    public R<Subject> getById(@PathVariable Long id) {
        Subject subject = subjectService.getById(id);
        return subject != null ? R.success(subject) : R.fail(R.NOT_FOUND_CODE, "科目未找到");
    }

    /**
     * 新增科目
     * 使用 @Valid 注解校验 Subject 实体中的约束 (@NotBlank 等)
     */
    @PostMapping
    public R<Void> add(@Valid @RequestBody Subject subject) {
        boolean success = subjectService.addSubject(subject);
        return R.result(success);
    }

    /**
     * 修改科目
     */
    @PutMapping
    public R<Void> update(@Valid @RequestBody Subject subject) {
        if (subject.getId() == null) {
            return R.fail("更新科目时必须提供ID");
        }
        boolean success = subjectService.updateSubject(subject);
        return R.result(success);
    }

    /**
     * 删除科目
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        boolean success = subjectService.deleteSubjectById(id);
        return R.result(success);
    }

    /**
     * 批量删除科目
     * @param ids ID列表, 例如: [1, 2, 3]
     */
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.fail("请选择要删除的科目");
        }
        boolean success = subjectService.deleteSubjectsByIds(ids);
        return R.result(success);
    }

    /**
     * 导出科目数据
     */
    @PostMapping("/export") // 使用 POST 方便传递查询条件
    public void export(HttpServletResponse response, @RequestBody SubjectQuery query) throws IOException {
        // 注意：导出方法通常没有返回值 (void)，直接操作 response 输出流
        subjectService.exportSubjects(response, query);
    }

    /**
     * 导入科目数据
     * @param file 上传的 Excel 文件
     */
    @PostMapping("/import")
    public R<String> importSubjects(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        try {
            String resultMsg = subjectService.importSubjects(file);
            // 根据导入结果返回成功或失败信息
            if (resultMsg.contains("失败")) {
                // 可以考虑返回更结构化的失败信息，但这里简单处理
                return R.fail(resultMsg); // 返回包含失败详情的消息
            } else {
                return R.success(resultMsg); // 返回成功消息
            }
        } catch (IOException e) {
            return R.fail("导入科目失败: " + e.getMessage());
        } catch (Exception e) {
            // 处理 Service 层可能抛出的其他业务异常
            return R.fail("导入科目时发生错误: " + e.getMessage());
        }
    }
} 