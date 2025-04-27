/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 22:09:44
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:12:44
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\controller\LogController.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.school.annotation.Log;
import com.school.entity.SysLog;
import com.school.entity.query.LogQuery;
import com.school.service.SysLogService;
import com.school.utils.R;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志记录 Controller
 *
 * @author LingMeng
 * @since 2024-05-17
 */
@RestController
@RequestMapping("/system/log")
@RequiredArgsConstructor
public class LogController {

    private final SysLogService sysLogService;

    /**
     * 查询操作日志列表
     */
    @PreAuthorize("hasAuthority('system:log:list')")
    @GetMapping("/list")
    // 添加 @Log 注解，但不记录此查询操作本身到日志表，避免无限循环或无意义日志
    // @Log(title = "操作日志", businessType = 0) // businessType=0 for QUERY or OTHER
    public R<IPage<SysLog>> list(LogQuery logQuery) {
        IPage<SysLog> page = sysLogService.listLogs(logQuery);
        return R.success(page);
    }

    // 可以在这里添加删除日志等接口，并加上相应的 @Log 注解和权限控制
    /*
    @PreAuthorize("hasAuthority('system:log:delete')")
    @DeleteMapping("/{logIds}")
    @Log(title = "操作日志", businessType = 3) // businessType=3 for DELETE
    public R<?> remove(@PathVariable Long[] logIds) {
        return R.success(sysLogService.removeByIds(Arrays.asList(logIds)));
    }

    @PreAuthorize("hasAuthority('system:log:clean')")
    @DeleteMapping("/clean")
    @Log(title = "操作日志", businessType = 3) // businessType=3 for DELETE or CLEAN
    public R<?> clean() {
        sysLogService.cleanOperLog(); // 假设 Service 有清空日志的方法
        return R.success();
    }
    */
} 