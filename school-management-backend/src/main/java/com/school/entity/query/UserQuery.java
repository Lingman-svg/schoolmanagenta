package com.school.entity.query;

// import com.school.entity.BaseQuery; // 如果有基础查询类
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询参数
 */
@Data
@EqualsAndHashCode(callSuper = false) // 如果没有继承 BaseQuery
public class UserQuery /* extends BaseQuery */ {
    private String userName; // 用户名
    private String phoneNumber; // 手机号
    private Integer isValid; // 帐号状态（0正常 1停用）
    // 可以添加其他查询条件，例如创建时间范围等
    // private String beginTime;
    // private String endTime;
} 