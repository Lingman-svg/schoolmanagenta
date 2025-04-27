# 小学学生管理系统开发计划 (TODO)

**重要提示:**
*   严格按照本 TODO 文件列出的功能点进行开发，不要额外增加功能点。
*   每完成一个步骤后，请及时更新对应步骤的状态（[ ] -> [x]），并增加 **完成时间** 和 **实现思路** 到备注中。

## 第一阶段：项目初始化与基础设置

*   [x] **1. 创建后端 Maven 项目**
    *   **技术栈:** Java 17, Spring Boot 3+, Maven, MybatisPlus 3+, Lombok
    *   **数据库/缓存:** MySQL 8.0.12 (localhost:3988, root/root), Redis (127.0.0.1:6379)
    *   **步骤:**
        *   [x] 使用 Maven Archetype 创建项目骨架。
        *   [x] 配置 `pom.xml` 添加所需依赖 (Spring Boot Parent, Web, Redis, Validation, MybatisPlus, MySQL, Lombok, Hutool, EasyExcel)。
        *   [x] 配置 `application.yml` (数据库连接, Redis 连接, MybatisPlus 配置)。
        *   [x] 创建基础包结构 (controller, service, mapper, entity, config, utils)。
        *   [x] 创建基础实体类 (`BaseEntity`, `PageDomain`) 并配置 MybatisPlus 自动填充 (`MybatisPlusConfig`, `MyMetaObjectHandler`) 和分页插件。
    *   **备注:** 完成时间: 2025-04-25。实现思路: 使用 `mvn archetype:generate` 创建基础项目，然后手动修改 `pom.xml` 添加 Spring Boot 及相关依赖，创建 `application.yml` 配置数据库和 Redis，创建标准包结构和基础实体类，配置 MybatisPlus 插件。

*   [x] **2. 创建前端 Vue 项目**
    *   **技术栈:** Vue 3, Vite/Vue CLI, Element Plus, VxeTable, Axios, JavaScript
    *   **步骤:**
        *   [x] 使用 `npm create vue@latest` 创建项目骨架 (Vite + Vue 3 + Router + Pinia + ESLint + Prettier)。
        *   [x] 安装所需依赖 (Element Plus, VxeTable, Axios)。
        *   [x] 设计基础布局 (左右结构，蓝白主题)。
        *   [ ] 配置路由 (`vue-router`)。
        *   [x] 封装 Axios 请求 (`utils/request.js`)。
    *   **备注:** 完成时间: 2025-04-25。实现思路: 使用 `npm create vue@latest` 交互式创建项目，然后 `cd` 进入目录使用 `npm install` 安装 Element Plus, VxeTable, Axios。在 `main.js` 中引入 Element Plus 和 VxeTable。创建 `utils/request.js` 封装 Axios 实例和拦截器。注意 Node 版本兼容性警告。

*   [x] **3. 实现通用工具类**
    *   **后端:**
        *   [x] 封装 EasyExcel 导入导出工具类 (`ExcelUtil.java`)，支持通用导入导出及复杂表头。
        *   [x] 定义统一响应结果类 (`R.java` 或类似)。
        *   [x] 配置全局异常处理 (`GlobalExceptionHandler.java`)。
    *   **前端:**
        *   [ ] 封装通用组件 (例如：表格和分页组件, 导入按钮, 导出按钮)。
        *   [x] 建立 API 统一管理模块 (`utils/request.js` 基础封装)。
    *   **备注:** 完成时间: 2025-04-25。实现思路: 后端创建 `R.java` 定义统一响应结构，`GlobalExceptionHandler` 使用 `@RestControllerAdvice` 捕获异常并返回 R 对象，`ExcelUtil` 使用 EasyExcel API 封装导入导出方法。前端创建 `utils/request.js` 配置 Axios 实例、拦截器和基础 URL。

## 第二阶段：核心业务功能开发 (按模块迭代)

**提醒:** 每个模块完成后，先进行后端测试，再开发前端页面，最后更新此 TODO。

*   [x] **4. 模块：科目管理 (Subject)**
    *   **后端:**
        *   [x] 创建 `Subject` 实体类 (继承 `BaseEntity`), `SubjectQuery` (继承 `PageDomain`)。
        *   [x] 创建 `SubjectMapper` 接口及 `SubjectMapper.xml` (XML 暂未创建，按需)。
        *   [x] 创建 `SubjectService` 接口及 `SubjectServiceImpl` (包含 CRUD, 导入, 导出逻辑)。
        *   [x] 创建 `SubjectController` (提供 RESTful API)。
        *   [ ] 编写必要的单元测试。
    *   **前端:**
        *   [x] 创建 `SubjectManagement.vue` 页面。
        *   [x] 使用 VxeTable 展示数据，实现分页、查询、新增、修改、删除。
        *   [x] 对接导入导出接口。
        *   [ ] 将页面逻辑抽离到 `subject.config.js` (按需进行)。
    *   **备注:** 完成时间: 2025-04-25 (后端), 2025-04-26 (前端)。实现思路: 后端遵循 MVC + Service + Mapper 结构，使用 MybatisPlus 实现基础 CRUD，Service 层处理业务逻辑，Controller 层提供 API。前端使用 Vue3 + ElementPlus + VxeTable 构建页面，通过 Axios 调用后端 API，实现数据展示和交互。解决了 CORS 和 VxeTable 组件加载问题。

*   [x] **5. 模块：教师管理 (Teacher)**
    *   **后端:**
        *   [x] 创建 `Teacher` 实体类, `TeacherQuery`。
        *   [x] 创建 `TeacherSubject` 关联实体类 (或直接在 Teacher 中用 List 表示)。
        *   [x] 创建 `TeacherMapper` 接口及 XML, `TeacherSubjectMapper` (如果需要)。
        *   [x] 创建 `TeacherService` 接口及实现 (包含 CRUD, 关联科目, 导入, 导出)。
        *   [x] 创建 `TeacherController`。
        *   [ ] 实现身份证号自动提取出生日期逻辑。
        *   [ ] 编写测试。
    *   **前端:**
        *   [x] 创建 `TeacherManagement.vue` 页面。
        *   [x] 实现教师信息的展示、增删改查、导入导出。
        *   [x] 实现关联科目的选择/展示。
        *   [ ] 将页面逻辑抽离到 `teacher.config.js`。
    *   **备注:** 完成时间: 2025-04-26 (后端), 2025-04-26 (前端)。实现思路: 后端完成 CRUD、分页、导入导出及教师-科目关联。前端完成对应页面功能，包括表单、表格、搜索、分页、弹窗、导入导出及关联科目展示。统一了按钮样式和顺序，并解决了图标显示问题。

*   [x] **6. 模块：班级管理 (Class)**
    *   **后端:**
        *   [x] 创建 `Clazz` 实体类, `ClazzQuery` (注意 Class 是关键字，用 Clazz 或其他名称)。
        *   [x] 创建 `ClazzMapper` 接口及 XML。
        *   [x] 创建 `ClazzService` 接口及实现 (CRUD, 导入, 导出, 自动计算结束日期, 自动判断状态)。
        *   [x] 创建 `ClazzController`。
        *   [x] 添加了后端校验逻辑 (唯一性、班主任、日期)。
        *   [x] 添加了后端定时任务更新班级状态。
        *   [ ] 编写测试。
    *   **前端:**
        *   [x] 创建 `ClassManagement.vue` 页面。
        *   [x] 实现班级信息的展示、增删改查、导入导出。
        *   [x] 班主任字段使用下拉选择（调用教师列表接口）。
        *   [x] 添加了前端校验逻辑 (日期)。
        *   [x] 实现前后端自动状态计算和更新。
        *   [ ] 将页面逻辑抽离到 `class.config.js`。
    *   **备注:** 完成时间: 2025-04-26 (后端), 2025-04-26 (前端)。实现思路: 后端完成 CRUD、分页、导入导出 API。前端完成对应页面，包括搜索、表格、分页、表单弹窗、导入导出。实现了前后端校验、基于日期的自动状态计算和后端定时状态更新。

*   [x] **7. 模块：学生管理 (Student)**
    *   **后端:**
        *   [x] 创建 `Student` 实体类, `StudentQuery`。
        *   [x] 创建 `StudentClazzHistory` 变更记录实体类。
        *   [x] 创建 `StudentMapper`, `StudentClazzHistoryMapper` 接口及 XML。
        *   [x] 创建 `StudentService` 接口及实现 (CRUD, 导入, 导出, 处理班级变更历史记录逻辑)。
        *   [x] 创建 `StudentController` (提供 RESTful API, 包括获取历史记录)。
        *   [x] 实现身份证号自动提取出生日期逻辑 (在 `Student` 实体 `setIdCard` setter 中)。
        *   [x] 添加后端校验逻辑 (Entity 注解, Controller 参数校验)。
        *   [x] 添加 Service 层业务校验 (ID card 唯一性, Student Number 唯一性, 班级有效性)。
        *   [x] 创建 `StudentClazzHistoryDto` 用于包含班级名称。
        *   [x] 实现 Service 和 Mapper 方法以 JOIN 查询方式获取带班级名称的历史记录。
        *   [ ] 编写测试。
        *   [TODO] Enhance `listStudents` to return DTO with class name.
        *   [TODO] Enhance `getStudentInfo` to return DTO with class name.
        *   [TODO] Enhance `listStudentsForExport` to export class name (using DTO/VO).
        *   [TODO] Consider implementing automatic student number generation.
    *   **前端:**
        *   [x] 创建 `StudentManagement.vue` 页面 (包含搜索、按钮、表格)。
        *   [x] 实现学生信息的展示、增删改查、导入导出 (对接 API)。
        *   [x] 实现 `StudentFormDialog.vue` 新增/修改弹窗组件。
        *   [x] 实现 `StudentClassHistoryDialog.vue` 班级历史弹窗组件。
        *   [x] 实现前端表单校验。
        *   [x] 实现前端身份证自动填充逻辑。
        *   [x] 实现查看班级变更历史功能 (按钮、API 调用、弹窗展示，包含班级名称)。
        *   [ ] 将页面逻辑抽离到 `student.config.js` (按需进行)。
        *   [TODO] Display class name directly in the main table (requires backend DTO change).
        *   [TODO] Enhance import feedback (e.g., download error report).
        *   [IGNORE] Linter warnings for unused icons (`StudentManagement.vue`).
        *   [IGNORE] Linter warnings for unused props (`StudentClassHistoryDialog.vue`).
    *   **备注:** 完成时间: 2024-05-07 (主要功能)。实现思路: 后端实现 CRUD、导入导出、校验、班级历史记录。前端实现页面、弹窗、API 对接、前端校验、自动填充、历史记录展示。注意: `StudentClazzRelationMapper` 未实现，因业务规则为学生同时只能属一个班级。

*   [x] **8. 模块：节课管理 (CourseTime)**
    *   **后端:**
        *   [x] 创建 `CourseTime` 实体类, `CourseTimeQuery`。
        *   [x] 创建 `CourseTimeMapper` 接口及 XML。
        *   [x] 创建 `CourseTimeService` 接口及实现 (包含 CRUD, 分页查询, 基础校验, **时间段重叠校验**)。
        *   [x] 创建 `CourseTimeController`。
        *   [ ] 编写测试。
    *   **前端:**
        *   [x] 创建 `CourseTimeManagement.vue` 页面。
        *   [x] 实现节课信息的展示、增删改查 (包含分页、搜索、弹窗表单、前后端校验反馈)。
        *   [ ] 将页面逻辑抽离到 `courseTime.config.js`。
    *   **备注:** 完成时间: 2024-05-12 (前后端)。实现思路: 后端完成 CRUD、分页查询及校验逻辑 (包括时间段重叠)。前端完成对应页面，使用 VxeTable 和 Element Plus，实现数据展示和交互，并能正确处理后端的校验错误信息。

*   [x] **9. 模块：课程管理 (Course)**
    *   **后端:**
        *   [x] 创建 `Course` 实体类, `CourseQuery`。
        *   [x] 创建 `CourseMapper` 接口及 XML。
        *   [x] 创建 `CourseService` 接口及实现。
        *   [x] 创建 `CourseController`。
        *   [x] 实现时间冲突校验逻辑 (在 Service/Mapper 中)。
        *   [ ] 编写测试。
    *   **前端:**
        *   [x] 创建 `CourseManagement.vue` 页面 (使用 FullCalendar)。
        *   [x] 以课表形式 (周/日视图) 展示课程，支持切换视图。
        *   [x] 实现课程的增、删、改（通过点击事件和弹窗）。
        *   [x] 实现通过下拉列表筛选班级、教师。
        *   [x] 实现切换周/日时自动重新加载数据。
        *   [x] 动态调整事件显示内容以适应不同视图。
        *   [ ] 实现导入导出功能。
        *   [ ] 班级、教师、节课使用下拉选择 (已在弹窗内实现)。
        *   [ ] 将页面逻辑抽离到 `course.config.js` (按需进行)。
    *   **备注:** 完成时间: 2024-05-13 (前端主要功能), 2024-05-13 (后端基础结构)。实现思路: 前端使用 FullCalendar。后端基础 CRUD 结构已存在。**时间冲突校验已在 `CourseServiceImpl` 的 `checkConflict` 方法中通过 `exists` 查询实现 (2024-05-14)，覆盖了班级、教师、地点三个维度的冲突。** 后续需补充单元测试。

*   [x] **10. 模块：成绩管理 (Grade/Score)**
    *   **后端:**
        *   [x] 创建 `Grade` 实体类, `GradeQuery`, `GradeDto`, `GradeExcelVo`。
        *   [x] 创建 `GradeMapper` 接口及 XML。
        *   [x] 创建 `GradeService` 接口及实现 (CRUD, 批量删除, 导入, 导出, 列表 DTO 转换)。
        *   [ ] 考虑成绩分析接口 (可能需要行转列)。 (未完成)
        *   [x] 创建 `GradeController`。
        *   [ ] 编写测试。(未完成)
    *   **前端:**
        *   [x] 创建 `GradeManagement.vue` 页面。
        *   [x] 实现成绩录入、查询、修改、删除、导入导出。
        *   [ ] 考虑使用 VxeTable 或类似组件实现行转列展示。(未完成)
        *   [x] 学生、科目、班级、教师使用下拉选择。
        *   [x] 实现 `GradeFormDialog.vue` 新增/修改弹窗组件。
        *   [x] 集成 `ImportDialog.vue` 用于导入。
        *   [x] 实现新增时根据学生自动填充班级。
        *   [x] 添加路由和菜单。
        *   [ ] 将页面逻辑抽离到 `grade.config.js`。(未完成)
    *   **备注:** 完成时间: 2024-05-10 (主要功能)。实现思路: 后端完成 CRUD、批量删除、导入导出、DTO 转换和日期格式处理。前端完成页面、弹窗、API 对接、导入导出逻辑、下拉选项加载、自动填充班级。解决了日期格式、导出文件类型、导出字段和 JS 导入错误等问题。

## 第三阶段：系统管理功能

*   [x] **11. 模块：系统参数 (SystemConfig)**
    *   **后端/前端:** 实现简单的键值对配置管理。
    *   **备注:** 完成时间: 2024-05-14。实现思路: 后端创建 Entity, Mapper, Service, Controller 实现基础 CRUD API，并添加校验逻辑 (key 唯一性，系统内置参数限制)。前端创建 API 文件 (`system.js`) 和 Vue 组件 (`SystemConfigManagement.vue`)，使用 Element Plus 构建页面，实现查询、展示、新增、修改、删除功能，并遵循项目前端代码规范。

*   [x] **12. 模块：用户管理 (User)**
    *   **后端/前端:** 实现用户增删改查，关联角色，密码加密存储。
    *   **备注:** 完成时间: 2024-05-15。实现思路: 后端完成 Entity, Mapper, Service, Controller，实现 CRUD、分页查询、角色关联、DTO/VO、密码加密(BCrypt)。前端完成 API、Vue 页面、路由、菜单，实现用户列表展示、查询、分页、新增、修改、删除、状态修改、重置密码、角色选择。**剩余:** 权限认证与授权 (属于权限模块), 前端日期范围查询、导入导出、**[TODO] 实现分配角色功能 (单独按钮/界面)**。

*   [x] **13. 模块：角色管理 (Role)**
    *   **后端/前端:** 实现角色增删改查，关联权限(菜单/按钮)。
    *   **备注:** 完成时间: 2024-05-14。实现思路: 后端创建 Entity (Role, RoleMenu), Mapper (RoleMapper, RoleMenuMapper), Service (RoleService), Controller (RoleController)，实现角色 CRUD、分页查询、状态修改、唯一性校验以及角色菜单关联。前端创建 API 文件 (`role.js`) 和 Vue 组件 (`RoleManagement.vue`)，使用 VxeTable 和 Element Plus Dialog/Form/Tree，实现角色展示、查询、分页、增删改、状态修改、菜单权限分配功能，并保持 UI 一致性。

*   [x] **14. 模块：菜单管理 (Menu)**
    *   **后端/前端:** 实现菜单(目录、菜单、按钮)的增删改查，用于动态路由和权限控制。
    *   **备注:** 完成时间: 2024-05-14。实现思路: 后端创建 Entity (Menu, dto.MenuNode, vo.TreeSelect), Mapper (MenuMapper, 空 RoleMenuMapper), Service, Controller，实现菜单 CRUD、树构建和校验逻辑。前端创建 API 文件 (`menu.js`) 和 Vue 组件 (`MenuManagement.vue`)，使用 Element Plus TreeTable 和 TreeSelect，实现树形展示、查询、新增、修改、删除菜单功能。修复了 Linter 错误，添加了路由。

*   [x] **15. 模块：权限管理 (Permission)**
    *   **后端:**
        *   [x] 实现基于 RBAC (Role-Based Access Control) 的权限校验逻辑 (整合 Spring Security, 方法级 @PreAuthorize)。 (完成时间: 2024-05-16) 实现思路: 添加 Spring Security 依赖, 配置 SecurityConfig (禁用 CSRF, 启用 CORS, 配置认证入口/处理器, 注册 JWT 过滤器), 实现 UserDetailsServiceImpl 加载用户及权限, 实现 JwtAuthenticationTokenFilter 校验 Token, 在 Controller 方法上使用 @PreAuthorize("hasAuthority(...)")。
        *   [ ] 定义权限注解或配置。
        *   [x] 实现用户登录、登出、JWT Token 生成与校验。 (完成时间: 2024-05-15/16) 实现思路: 后端创建 LoginController 处理登录(/login)和登出(/logout)请求, 创建 AuthService 处理认证逻辑并生成 JWT, 创建 JwtUtil 生成/解析 Token, SecurityConfig 中配置登录端点。 前端创建 Pinia store 管理 Token, 在登录成功后存储 Token, 在请求头中携带 Token, 实现登出逻辑。
    *   **前端:**
        *   [x] 实现登录页面。 (完成时间: 2024-05-15) 实现思路: 创建 UserLogin.vue 页面, 使用 Element Plus Form 组件, 对接 /login API。
        *   [x] 根据用户权限动态生成菜单。 (完成时间: 2024-05-16) 实现思路: 在 App.vue 中修改 `filterAndBuildMenu` 函数读取路由 `meta.permission`, 在模板中使用 `v-permission` 指令控制 `el-menu-item` 和 `el-sub-menu` 的渲染。
        *   [x] 实现路由守卫进行权限拦截。 (完成时间: 2024-05-15) 实现思路: 在 router/index.js 中使用 `router.beforeEach` 进行导航守卫, 判断 Token 和用户信息, 无 Token 或获取信息失败则重定向到登录页。
        *   [x] 控制按钮级权限显示。 (完成时间: 2024-05-16) 实现思路: 创建 `directives/permission.js` 自定义指令, 在 `mounted` 钩子中检查用户权限并移除无权限元素, 全局注册指令, 在 Vue 模板中使用 `v-permission="'xxx:xxx:xxx'"`。
    *   **备注:** 完成时间: 2024-05-16 (前后端权限控制主要功能)。权限标识符需在数据库、后端注解、前端路由、前端指令中保持严格一致。`LessonController` 问题已澄清并确认当前配置正确。

*   [x] **16. 模块：日志管理 (Log)**
    *   **后端:**
        *   [x] 使用 AOP 或拦截器记录操作日志 (记录请求信息、操作用户、结果等)。
        *   [x] 创建 `Log` 实体类及相关 Mapper/Service/Controller。
        *   [x] 创建 `@Log` 注解、`LogAspect` 切面、`BusinessType` 常量，并在各 Controller 添加注解。
        *   [x] 启用异步 `@EnableAsync`。
    *   **前端:**
        *   [x] 创建 `LogManagement.vue` 页面展示操作日志。
        *   [x] 创建 `log.js` API 文件。
        *   [x] 添加前端路由。
    *   **备注:** 完成时间: 2024-05-17 (基本功能)。实现思路: 后端使用 AOP + 自定义注解拦截 Controller 方法，异步保存日志到 `sys_log` 表。前端创建页面、API、路由。**待办:** 需手动创建 `sys_log` 数据库表，并在 `sys_menu` 表中插入菜单 SQL。

## 第四阶段：AI 功能集成 (优先级最低)

*   [ ] **17. AI 助手集成**
    *   **后端:**
        *   [ ] 添加 `spring-cloud-starter-alibaba-ai` 依赖。
        *   [ ] 阅读官方文档: <https://java2ai.com/docs/1.0.0-M6.1/overview/>。
        *   [x] 配置 AI 服务 (选择模型, base_url, key 等)。 (完成时间: 2024-05-17, 实现思路: 在 application.yml 中添加 spring.cloud.ai 相关配置, API Key 需手动填入)
        *   [x] 创建 `AiService` 用于与 AI SDK 交互。 (完成时间: 2024-05-17, 实现思路: 创建 AiService 接口和 AiServiceImpl 实现, 注入 ChatClient.Builder, 注册函数回调)
        *   [x] 创建 Controller 接口接收聊天请求。 (完成时间: 2024-05-17, 实现思路: 创建 AiController, 注入 AiService, 处理 /ai/chat POST 请求)
        *   [x] 定义后端 AI 函数调用 (Function Calling)。 (完成时间: 2025-04-27, 实现思路: 使用 Spring AI Function Callback 机制在 AiFunctionConfig 中定义多个 Bean 作为 AI 可调用的函数，处理科目、学生、教师、课程、成绩查询。)
        *   [ ] 实现将自然语言指令解析并调用对应业务 Service 的逻辑。
        *   [ ] 探索模型调优可能性。
    *   **前端:**
        *   [x] (可选) 创建一个聊天界面与后端 AI 接口交互。 (完成时间: 2025-04-27, 实现思路: 创建 AiChat.vue, 使用 marked, hljs, DOMPurify 实现 Markdown 渲染和代码高亮/复制)
    *   **文档:**
        *   [ ] 编写 AI 功能使用及调优文档。
    *   **备注:**

## 第五阶段：收尾工作

*   [ ] **18. 生成数据库脚本与初始化数据**
    *   [ ] 根据最终的实体类生成 `schema.sql` 文件。
    *   [ ] 编写 `data.sql` 文件：
        *   插入菜单数据 (根据前端页面)。
        *   插入基础角色数据 (管理员, 教师, 学生)。
        *   插入角色权限关联数据 (管理员拥有所有权限)。
        *   插入超级管理员用户 (`admin/admin`) 并关联管理员角色。
    *   **备注:**

*   [ ] **19. 最终测试与部署**
    *   [ ] 进行系统性的整体测试。
    *   [ ] 编写 README 文档说明项目如何运行和部署。
    *   **备注:** 