-- 科目表
CREATE TABLE sch_subject (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL COMMENT '科目名称',
    code VARCHAR(32) NOT NULL COMMENT '科目编码',
    remark VARCHAR(255),
    status TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 教师表
CREATE TABLE sch_teacher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL COMMENT '教师姓名',
    gender CHAR(1) COMMENT '性别',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20),
    email VARCHAR(64),
    status TINYINT DEFAULT 1,
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 教师-科目关联表
CREATE TABLE sch_teacher_subject (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL
);

-- 班级表
CREATE TABLE sch_clazz (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL COMMENT '班级名称',
    code VARCHAR(32) NOT NULL COMMENT '班级编码',
    head_teacher_id BIGINT COMMENT '班主任ID',
    start_date DATE,
    end_date DATE,
    status TINYINT DEFAULT 1 COMMENT '1-在读 0-毕业',
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 学生表
CREATE TABLE sch_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    gender CHAR(1),
    id_card VARCHAR(18),
    student_number VARCHAR(32) NOT NULL,
    clazz_id BIGINT,
    phone VARCHAR(20),
    email VARCHAR(64),
    status TINYINT DEFAULT 1,
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 学生班级变更历史表
CREATE TABLE sch_student_clazz_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    clazz_id BIGINT NOT NULL,
    change_date DATE NOT NULL,
    remark VARCHAR(255),
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 节课表
CREATE TABLE sch_course_time (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL COMMENT '节次名称',
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    remark VARCHAR(255),
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 课程表
CREATE TABLE sch_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL COMMENT '课程名称',
    clazz_id BIGINT NOT NULL COMMENT '班级ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    subject_id BIGINT NOT NULL COMMENT '科目ID',
    course_time_id BIGINT NOT NULL COMMENT '节次ID',
    location VARCHAR(64) COMMENT '上课地点',
    week_day TINYINT NOT NULL COMMENT '星期几 1-7',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    remark VARCHAR(255),
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 成绩表
CREATE TABLE sch_grade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    clazz_id BIGINT,
    teacher_id BIGINT,
    score DECIMAL(5,2) NOT NULL,
    exam_date DATE,
    remark VARCHAR(255),
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 系统参数表
CREATE TABLE sch_system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(64) NOT NULL UNIQUE,
    config_value VARCHAR(255) NOT NULL,
    remark VARCHAR(255),
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 用户表
CREATE TABLE sch_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    real_name VARCHAR(64),
    phone VARCHAR(20),
    email VARCHAR(64),
    status TINYINT DEFAULT 1,
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 角色表
CREATE TABLE sch_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    code VARCHAR(32) NOT NULL UNIQUE,
    status TINYINT DEFAULT 1,
    remark VARCHAR(255),
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 用户-角色关联表
CREATE TABLE sch_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

-- 菜单表
CREATE TABLE sch_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(64) NOT NULL,
    path VARCHAR(128),
    component VARCHAR(128),
    perms VARCHAR(128) COMMENT '权限标识',
    type TINYINT COMMENT '0-目录 1-菜单 2-按钮',
    icon VARCHAR(64),
    order_num INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- 角色-菜单关联表
CREATE TABLE sch_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL
);

-- 日志表
CREATE TABLE sch_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(128),
    business_type INT,
    method VARCHAR(255),
    request_method VARCHAR(16),
    operator_type INT,
    oper_name VARCHAR(64),
    dept_name VARCHAR(64),
    oper_url VARCHAR(255),
    oper_ip VARCHAR(64),
    oper_location VARCHAR(128),
    oper_param TEXT,
    json_result TEXT,
    status TINYINT,
    error_msg VARCHAR(512),
    oper_time DATETIME,
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- AI 聊天记录表
CREATE TABLE sch_ai_chat_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    question TEXT NOT NULL,
    answer TEXT,
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- AI 函数调用日志表
CREATE TABLE sch_ai_function_call_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    function_name VARCHAR(64) NOT NULL,
    arguments TEXT,
    result TEXT,
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
);

-- AI 配置表
CREATE TABLE sch_ai_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(64) NOT NULL UNIQUE,
    config_value VARCHAR(255) NOT NULL,
    remark VARCHAR(255),
    create_time DATETIME,
    create_user VARCHAR(64),
    update_time DATETIME,
    update_user VARCHAR(64)
); 