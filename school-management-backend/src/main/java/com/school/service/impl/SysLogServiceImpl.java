package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.entity.SysLog;
import com.school.entity.query.LogQuery;
import com.school.mapper.SysLogMapper;
import com.school.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 系统操作日志记录表 服务实现类
 * </p>
 *
 * @author LingMeng
 * @since 2024-05-17
 */
@Service
@Slf4j
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    /**
     * 使用 @Async 注解表示该方法是异步执行的
     * 需要在启动类或配置类上添加 @EnableAsync
     * @param sysLog 日志实体
     */
    @Async // 标记为异步方法
    @Override
    public void saveLogAsync(SysLog sysLog) {
        try {
            // 这里可以添加一些默认值设置，例如操作时间
             if (sysLog.getOperTime() == null) {
                 sysLog.setOperTime(java.time.LocalDateTime.now());
             }
            this.save(sysLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败", e);
        }
    }

    @Override
    public IPage<SysLog> listLogs(LogQuery logQuery) {
        Page<SysLog> page = new Page<>(logQuery.getPageNum(), logQuery.getPageSize());
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        wrapper.like(StringUtils.hasText(logQuery.getTitle()), SysLog::getTitle, logQuery.getTitle());
        wrapper.like(StringUtils.hasText(logQuery.getOperName()), SysLog::getOperName, logQuery.getOperName());
        wrapper.eq(logQuery.getBusinessType() != null, SysLog::getBusinessType, logQuery.getBusinessType());
        wrapper.eq(logQuery.getStatus() != null, SysLog::getStatus, logQuery.getStatus());

        // 添加日期范围查询
        if (logQuery.getBeginTime() != null) {
             wrapper.ge(SysLog::getOperTime, logQuery.getBeginTime());
        }
         if (logQuery.getEndTime() != null) {
             // 结束时间通常需要包含当天，所以查询 <= 结束日期的 23:59:59
             // 或者简单处理为 < 结束日期的后一天
             wrapper.le(SysLog::getOperTime, logQuery.getEndTime());
         }

        // 按操作时间降序排序
        wrapper.orderByDesc(SysLog::getOperTime);

        return this.page(page, wrapper);
    }
} 