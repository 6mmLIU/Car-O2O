# Car-O2O 项目说明

## 项目简介

本仓库基于 [RuoYi](https://gitee.com/y_project/RuoYi-Vue) 框架二次开发，版本为 `v3.8.3`，用于构建汽车 O2O 业务管理平台。项目采用 **Spring Boot** + **Vue** 的前后端分离技术栈，实现用户管理、业务管理、定时任务、代码生成等多种功能，适用于企业级快速开发。

## 目录结构

```
Car-O2O/
├── car-o2o-admin         # Web 服务入口，Spring Boot 应用
├── car-o2o-business      # 业务模块（Controller、Service、Mapper 等）
├── car-o2o-business-domain # 业务实体、DTO、VO 等
├── car-o2o-common        # 通用工具与基础组件
├── car-o2o-framework     # 框架核心功能
├── car-o2o-generator     # 代码生成模块
├── car-o2o-quartz        # 定时任务模块
├── car-o2o-system        # 系统管理模块
├── car-o2o-ui            # 前端 Vue 项目
├── sql                   # 初始化数据库脚本
├── doc                   # 额外文档
└── ry.sh / ry.bat        # Linux/Windows 启动脚本
```

各模块之间通过 Maven 聚合项目方式管理，父工程位于 `pom.xml`，统一依赖版本与打包配置。

## 技术栈

- **后端：** Spring Boot、Spring Security、MyBatis、Redis、Quartz、Jackson、Fastjson2 等
- **前端：** Vue 2.x、Element UI、Axios、Vue Router、Vuex
- **数据库：** MySQL（提供初始化脚本），使用 Druid 连接池
- **其他：** Swagger3 接口文档、PageHelper 分页、Velocity 模版引擎

## 主要功能

- **系统管理：** 用户、角色、部门、菜单、岗位、字典、参数、通知公告等基础功能
- **业务模块：** 服务项管理、预约管理、结算单管理等汽车相关业务流程
- **代码生成：** 根据表结构自动生成后端和前端代码，提升开发效率
- **定时任务：** 基于 Quartz 实现的任务调度，可在线管理任务状态
- **日志监控：** 提供操作日志、登录日志、在线用户、服务监控等运维功能

## 环境要求

- JDK 1.8 及以上
- Maven 3.5+（已包含 `mvnw` 可使用）
- Node.js 8.9+ 与 npm
- MySQL 5.7 及以上

## 编译与运行

1. **初始化数据库**：执行 `sql/ry_20220625.sql` 与 `sql/quartz.sql`，初始化系统表结构。
2. **后端启动**：
   ```bash
   # 打包父工程
   mvn clean package -DskipTests
   # 进入 car-o2o-admin 目录并运行
   java -jar car-o2o-admin/target/car-o2o-admin.jar
   ```
   也可使用 `ry.sh`（Linux）或 `ry.bat`（Windows）脚本进行 start/stop/restart 等操作。
3. **前端启动**：
   ```bash
   cd car-o2o-ui
   npm install --registry=https://registry.npmmirror.com
   npm run dev
   ```
   启动后访问 `http://localhost:80`。

## 许可证

项目遵循 [MIT License](LICENSE)。

## 参考文档

- 项目根目录的 [README.md](README.md) 提供了 RuoYi 原版框架的概述与在线体验地址。
- `doc/若依环境使用手册.docx` 为环境搭建与使用手册。

