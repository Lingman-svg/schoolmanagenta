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
        *   [ ] 封装通用组件 (例如：分页组件, 导入按钮, 导出按钮)。
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

*   [ ] **7. 模块：学生管理 (Student)**
    *   **后端:**
        *   [ ] 创建 `Student` 实体类, `StudentQuery`。
        *   [ ] 创建 `StudentClazzRelation` 关联实体类。
        *   [ ] 创建 `StudentClazzHistory` 变更记录实体类。
        *   [ ] 创建 `StudentMapper`, `StudentClazzRelationMapper`, `StudentClazzHistoryMapper` 接口及 XML。
        *   [ ] 创建 `StudentService` 接口及实现 (CRUD, 导入, 导出, 关联班级, 处理班级变更逻辑)。
        *   [ ] 创建 `StudentController`。
        *   [ ] 实现身份证号自动提取出生日期逻辑。
        *   [ ] 编写测试。
    *   **前端:**
        *   [ ] 创建 `StudentManagement.vue` 页面。
        *   [ ] 实现学生信息的展示、增删改查、导入导出。
        *   [ ] 实现学生关联班级、查看/添加变更记录功能。
        *   [ ] 将页面逻辑抽离到 `student.config.js`。
    *   **备注:**

*   [ ] **8. 模块：节课管理 (CourseTime)**
    *   **后端:**
        *   [ ] 创建 `CourseTime` 实体类, `CourseTimeQuery`。
        *   [ ] 创建 `CourseTimeMapper` 接口及 XML。
        *   [ ] 创建 `CourseTimeService` 接口及实现。
        *   [ ] 创建 `CourseTimeController`。
        *   [ ] 编写测试。
    *   **前端:**
        *   [ ] 创建 `CourseTimeManagement.vue` 页面。
        *   [ ] 实现节课信息的展示、增删改查。
        *   [ ] 将页面逻辑抽离到 `courseTime.config.js`。
    *   **备注:**


*   [ ] **9. 模块：课程管理 (Course)**
    *   **后端:**
        *   [ ] 创建 `Course` 实体类, `CourseQuery`。
        *   [ ] 创建 `CourseMapper` 接口及 XML。
        *   [ ] 创建 `CourseService` 接口及实现 (CRUD, 导入, 导出, **时间冲突校验逻辑**)。
        *   [ ] 创建 `CourseController`。
        *   [ ] 编写测试。
    *   **前端:**
        *   [ ] 创建 `CourseManagement.vue` 或 `CourseSchedule.vue` 页面。
        *   [ ] 以课表形式或列表形式展示、编排课程。
        *   [ ] 实现增删改查、导入导出。
        *   [ ] 班级、教师、节课使用下拉选择。
        *   [ ] 将页面逻辑抽离到 `course.config.js`。
    *   **备注:**

*   [ ] **10. 模块：成绩管理 (Grade/Score)**
    *   **后端:**
        *   [ ] 创建 `Grade` 实体类, `GradeQuery`。
        *   [ ] 创建 `GradeMapper` 接口及 XML。
        *   [ ] 创建 `GradeService` 接口及实现 (CRUD, 导入, 导出)。
        *   [ ] 考虑成绩分析接口 (可能需要行转列)。
        *   [ ] 创建 `GradeController`。
        *   [ ] 编写测试。
    *   **前端:**
        *   [ ] 创建 `GradeManagement.vue` 页面。
        *   [ ] 实现成绩录入、查询、修改、删除、导入导出。
        *   [ ] 考虑使用 VxeTable 或类似组件实现行转列展示。
        *   [ ] 学生、科目、班级、教师使用下拉选择。
        *   [ ] 将页面逻辑抽离到 `grade.config.js`。
    *   **备注:**

## 第三阶段：系统管理功能

*   [ ] **11. 模块：系统参数 (SystemConfig)**
    *   **后端/前端:** 实现简单的键值对配置管理。
    *   **备注:**

*   [ ] **12. 模块：用户管理 (User)**
    *   **后端/前端:** 实现用户增删改查，关联角色。
    *   **备注:**

*   [ ] **13. 模块：角色管理 (Role)**
    *   **后端/前端:** 实现角色增删改查，关联权限(菜单/按钮)。
    *   **备注:**

*   [ ] **14. 模块：菜单管理 (Menu)**
    *   **后端/前端:** 实现菜单(目录、菜单、按钮)的增删改查，用于动态路由和权限控制。
    *   **备注:**

*   [ ] **15. 模块：权限管理 (Permission)**
    *   **后端:**
        *   [ ] 实现基于 RBAC (Role-Based Access Control) 的权限校验逻辑 (整合 Spring Security)。
        *   [ ] 定义权限注解或配置。
        *   [ ] 实现用户登录、登出、JWT Token 生成与校验。
    *   **前端:**
        *   [ ] 实现登录页面。
        *   [ ] 根据用户权限动态生成菜单。
        *   [ ] 实现路由守卫进行权限拦截。
        *   [ ] 控制按钮级权限显示。
    *   **备注:**

*   [ ] **16. 模块：日志管理 (Log)**
    *   **后端:**
        *   [ ] 使用 AOP 或拦截器记录操作日志 (记录请求信息、操作用户、结果等)。
        *   [ ] 创建 `Log` 实体类及相关 Mapper/Service/Controller。
    *   **前端:**
        *   [ ] 创建 `LogManagement.vue` 页面展示操作日志。
    *   **备注:**

## 第四阶段：AI 功能集成 (优先级最低)

*   [ ] **17. AI 助手集成**
    *   **后端:**
        *   [ ] 添加 `spring-cloud-starter-alibaba-ai` 依赖。
        *   [ ] 阅读官方文档: <https://java2ai.com/docs/1.0.0-M6.1/overview/>。
        *   [ ] 配置 AI 服务 (选择模型, base_url, key 等)。
        *   [ ] 创建 `AiService` 用于与 AI SDK 交互。
        *   [ ] 创建 Controller 接口接收聊天请求。
        *   [ ] 实现将自然语言指令解析并调用对应业务 Service 的逻辑。
        *   [ ] 探索模型调优可能性。
    *   **前端:**
        *   [ ] (可选) 创建一个聊天界面与后端 AI 接口交互。
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