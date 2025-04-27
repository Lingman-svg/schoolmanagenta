package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List; // 用于关联角色

/**
 * 用户信息表实体类
 *
 * @author Gemini
 * @since 2024-05-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sch_user") // 表名建议为 sch_user
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID (主键)
     */
    @TableId // 使用 BaseEntity 的 id
    private Long id;

    /**
     * 用户账号 (唯一)
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 3, max = 20, message = "用户账号长度必须介于 3 和 20 之间")
    @TableField("user_name")
    private String userName;

    /**
     * 用户昵称
     */
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    @TableField("nick_name")
    private String nickName;

    /**
     * 用户类型 (例如 00系统用户 01注册用户)
     * 可以根据需要定义
     */
    @TableField("user_type")
    private String userType = "00"; // 默认为系统用户

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    @TableField("email")
    private String email;

    /**
     * 手机号码
     */
    @Size(max = 11, message = "手机号码长度不能超过 11 个字符") // 可以添加更严格的正则校验
    @TableField("phonenumber")
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @TableField("sex")
    private String sex;

    /**
     * 头像地址
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 密码 (数据库存储加密后的密码)
     */
    @TableField("password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 返回给前端时忽略密码字段
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     * 继承自 BaseEntity 的 isValid 字段
     */
    // @TableField("status")
    // private Integer status; // 使用 BaseEntity 的 isValid

    /**
     * 最后登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @TableField("login_date")
    private Date loginDate;

    // --- 非数据库字段 ---

    /**
     * 角色对象列表 (用于用户详情)
     */
    @TableField(exist = false)
    private List<Role> roles;

    /**
     * 角色组 (用于新增/修改用户时传递选中的角色ID)
     */
    @TableField(exist = false)
    private Long[] roleIds;


    // id, remark, is_deleted, create_by, create_time, update_by, update_time, isValid (status) 继承自 BaseEntity

    // 便利方法，判断是否为管理员 (简化判断，假设 ID=1 为管理员)
    @JsonIgnore // 不序列化给前端
    public boolean isAdmin() {
        return this.getId() != null && 1L == this.getId();
    }
} 