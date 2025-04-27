-- 1. 插入菜单数据（示例，实际可根据前端页面结构补充/调整）
INSERT INTO sch_menu (id, parent_id, name, path, component, perms, type, icon, order_num, status, create_time)
VALUES
(1, 0, '系统管理', '/system', NULL, NULL, 0, 'setting', 1, 1, NOW()),
(2, 1, '用户管理', '/system/user', 'system/UserManagement', 'system:user:list', 1, 'user', 1, 1, NOW()),
(3, 1, '角色管理', '/system/role', 'system/RoleManagement', 'system:role:list', 1, 'role', 2, 1, NOW()),
(4, 1, '菜单管理', '/system/menu', 'system/MenuManagement', 'system:menu:list', 1, 'menu', 3, 1, NOW()),
(5, 0, '基础数据', '/base', NULL, NULL, 0, 'database', 2, 1, NOW()),
(6, 5, '科目管理', '/base/subject', 'system/SubjectManagement', 'base:subject:list', 1, 'book', 1, 1, NOW()),
(7, 5, '教师管理', '/base/teacher', 'system/TeacherManagement', 'base:teacher:list', 1, 'teacher', 2, 1, NOW()),
(8, 5, '班级管理', '/base/class', 'system/ClassManagement', 'base:class:list', 1, 'class', 3, 1, NOW()),
(9, 5, '学生管理', '/base/student', 'system/StudentManagement', 'base:student:list', 1, 'student', 4, 1, NOW()),
(10, 0, '课程管理', '/courses', 'system/CourseManagement', 'course:list', 1, 'calendar', 3, 1, NOW()),
(11, 0, '成绩管理', '/grades', 'system/GradeManagement', 'grade:list', 1, 'score', 4, 1, NOW()),
(12, 0, '日志管理', '/logs', 'system/LogManagement', 'log:list', 1, 'log', 5, 1, NOW()),
(13, 0, 'AI助手', '/ai', 'system/AiChat', 'ai:chat', 1, 'robot', 6, 1, NOW());

-- 2. 插入基础角色数据
INSERT INTO sch_role (id, name, code, status, remark, create_time)
VALUES
(1, '管理员', 'admin', 1, '系统管理员', NOW()),
(2, '教师', 'teacher', 1, '教师角色', NOW()),
(3, '学生', 'student', 1, '学生角色', NOW());

-- 3. 插入角色权限关联数据（管理员拥有所有权限）
INSERT INTO sch_role_menu (role_id, menu_id)
SELECT 1, id FROM sch_menu;

-- 3.1 学生角色只分配成绩管理菜单权限
INSERT INTO sch_role_menu (role_id, menu_id) VALUES (3, 11);

-- 3.2 教师角色分配班级管理、学生管理、成绩管理菜单权限
INSERT INTO sch_role_menu (role_id, menu_id) VALUES (2, 8), (2, 9), (2, 11);

-- 3.3 教师拥有成绩管理、班级管理、学生管理下所有按钮权限
INSERT INTO sch_role_menu (role_id, menu_id)
SELECT 2, id FROM sch_menu WHERE parent_id IN (8, 9, 11) AND type = 2;

-- 3.4 学生仅拥有成绩管理下的查询按钮权限
INSERT INTO sch_role_menu (role_id, menu_id)
SELECT 3, id FROM sch_menu WHERE parent_id = 11 AND type = 2 AND (perms LIKE '%:query%' OR perms LIKE '%:list%');

-- 4. 插入超级管理员用户并关联管理员角色
INSERT INTO sch_user (id, username, password, real_name, status, create_time)
VALUES (1, 'admin', '$2a$10$wH8QwQwQwQwQwQwQwQwQwOQwQwQwQwQwQwQwQwQwQwQwQwQw', '超级管理员', 1, NOW());
-- 上述密码为 bcrypt 加密后的 'admin'，如需更换请重新加密

INSERT INTO sch_user_role (user_id, role_id) VALUES (1, 1); 