package com.school.entity.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.school.entity.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色查询条件封装类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQuery extends PageDomain {

    /**
     * 角色名称 (模糊查询)
     */
    private String roleName;

    /**
     * 角色权限字符串 (模糊查询)
     */
    private String roleKey;

    /**
     * 角色状态 (精确查询)
     */
    private Integer isValid; // 对应 Role 实体的 isValid 字段

    // TODO: 可以添加创建时间的范围查询字段 (例如 beginTime, endTime)
    // private String beginTime;
    // private String endTime;

    /**
     * 构建 MybatisPlus 查询条件 Wrapper
     *
     * @return LambdaQueryWrapper<Role>
     */
    public LambdaQueryWrapper<Role> buildQueryWrapper() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StrUtil.isNotEmpty(this.roleName), Role::getRoleName, this.roleName);
        wrapper.like(StrUtil.isNotEmpty(this.roleKey), Role::getRoleKey, this.roleKey);
        wrapper.eq(this.isValid != null, Role::getIsValid, this.isValid);

        // TODO: 添加时间范围查询逻辑
        // wrapper.ge(StrUtil.isNotEmpty(this.beginTime), Role::getCreateTime, this.beginTime);
        // wrapper.le(StrUtil.isNotEmpty(this.endTime), Role::getCreateTime, this.endTime);

        // 处理排序
        if (StrUtil.isNotEmpty(this.getOrderByColumn())) {
            boolean asc = "asc".equalsIgnoreCase(this.getIsAsc());
            // 注意：需要根据前端传入的 orderByColumn 映射到数据库字段
            // 这里简化处理，假设前端传入的就是数据库字段名
            // if("roleSort".equals(this.getOrderByColumn())) {
            //      wrapper.orderBy(true, asc, Role::getRoleSort);
            // } else {
            //      // 默认按创建时间排序或其他
            //      wrapper.orderBy(true, asc, Role::getCreateTime);
            // }
             // 暂时简化为支持 roleSort 和 createTime 排序
             if ("roleSort".equals(this.getOrderByColumn())) {
                 wrapper.orderBy(true, asc, Role::getRoleSort);
             } else if ("createTime".equals(this.getOrderByColumn())) {
                 wrapper.orderBy(true, asc, Role::getCreateTime);
             } else {
                  wrapper.orderByAsc(Role::getRoleSort); // 默认按 roleSort 升序
             }

        } else {
            wrapper.orderByAsc(Role::getRoleSort); // 默认排序字段
        }

        return wrapper;
    }
} 