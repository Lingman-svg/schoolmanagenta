/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-27 22:07:06
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-27 22:07:51
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\SysLogService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.SysLog;
import com.school.entity.query.LogQuery;

/**
 * <p>
 * 系统操作日志记录表 服务类
 * </p>
 *
 * @author LingMeng
 * @since 2024-05-17
 */
public interface SysLogService extends IService<SysLog> {

    /**
     * 异步保存操作日志
     * @param sysLog 日志实体
     */
    void saveLogAsync(SysLog sysLog);

    /**
     * 分页查询操作日志
     * @param logQuery 查询条件
     * @return 分页结果
     */
    IPage<SysLog> listLogs(LogQuery logQuery);

} 