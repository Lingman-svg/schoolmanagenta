package com.school.constant;

/**
 * 操作日志业务类型常量
 *
 * @author LingMeng
 */
public final class BusinessType {

    /**
     * 其它
     */
    public static final int OTHER = 0;

    /**
     * 新增
     */
    public static final int INSERT = 1;

    /**
     * 修改
     */
    public static final int UPDATE = 2;

    /**
     * 删除
     */
    public static final int DELETE = 3;

    /**
     * 授权
     */
    public static final int GRANT = 4;

    /**
     * 导出
     */
    public static final int EXPORT = 5;

    /**
     * 导入
     */
    public static final int IMPORT = 6;

    /**
     * 强制退出
     */
    public static final int FORCE = 7;

    /**
     * 清空数据
     */
    public static final int CLEAN = 8;

    /**
     * 查询
     */
    public static final int SELECT = 9; // Or QUERY

    /**
     * 登录
     */
    public static final int LOGIN = 10;

    /**
     * 登出
     */
    public static final int LOGOUT = 11;

    // 私有构造函数防止实例化
    private BusinessType() {
    }
} 