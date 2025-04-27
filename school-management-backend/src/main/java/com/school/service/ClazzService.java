/*
 * @Author: LingMeng 2663421939@qq.com
 * @Date: 2025-04-26 00:25:57
 * @LastEditors: LingMeng 2663421939@qq.com
 * @LastEditTime: 2025-04-26 00:41:00
 * @FilePath: \schoolmanagenta\school-management-backend\src\main\java\com\school\service\ClazzService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.school.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Clazz;
import com.school.entity.query.ClazzQuery;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

/**
 * 班级服务接口
 *
 * @author LingMeng
 * @since 2025-04-26
 */
public interface ClazzService extends IService<Clazz> {

    /**
     * 分页查询班级列表
     *
     * @param query 查询参数
     * @return 分页结果
     */
    Page<Clazz> listClazzes(ClazzQuery query);

    /**
     * 根据ID获取班级详情 (可能需要关联查询班主任信息)
     *
     * @param id 班级ID
     * @return 班级详情
     */
    Clazz getClazzInfo(Long id);

    /**
     * 新增班级
     *
     * @param clazz 班级信息
     * @return 是否成功
     */
    boolean addClazz(Clazz clazz);

    /**
     * 修改班级
     *
     * @param clazz 班级信息
     * @return 是否成功
     */
    boolean updateClazz(Clazz clazz);

    /**
     * 删除班级 (逻辑删除)
     *
     * @param id 班级ID
     * @return 是否成功
     */
    boolean deleteClazzLogically(Long id);

    /**
     * 批量删除班级 (逻辑删除)
     *
     * @param ids 班级ID列表
     * @return 是否成功
     */
    boolean deleteClazzesLogicallyBatch(List<Long> ids);

    /**
     * 导入班级数据
     *
     * @param file 上传的 Excel 文件
     * @throws Exception 导入过程中可能发生的异常
     */
    void importClazzes(MultipartFile file) throws Exception;

    /**
     * 根据查询条件获取用于导出的班级列表 (不分页)
     *
     * @param query 查询参数
     * @return 班级列表
     */
    List<Clazz> listClazzesForExport(ClazzQuery query);

    /**
     * 获取所有有效的班级列表 (通常用于下拉选择)
     * @return 有效班级列表 (可能只需要 ID 和 Name)
     */
    List<Clazz> listValidClasses();

    boolean existsById(Long id);

    // TODO: 添加导入、导出相关方法声明
    // void importClazzes(InputStream inputStream);
    // byte[] exportClazzes(ClazzQuery query);

    /**
     * AI路由专用：根据参数Map查询班级信息
     */
    Object getClazzInfo(java.util.Map<String, Object> params);

    /**
     * AI路由专用：根据参数Map统计班级数量
     */
    Object getClazzCount(java.util.Map<String, Object> params);

} 