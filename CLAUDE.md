# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## ⚠️ 当前工作分支

**必须切换到 `fullstack` 分支进行联调工作**，该分支包含前端+后端全部最新代码。

```bash
git checkout fullstack
```

## 联调启动

### 1. 启动后端（Spring Boot 3，端口 8080）

```bash
# 设置 Java 环境（Windows PowerShell）
$env:JAVA_HOME='E:\develop\DevTools\Java\jdk-17'
$env:MAVEN_HOME='E:\develop\DevTools\Maven\apache-maven-3.9.16'
$env:Path="$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:Path"

# 编译并启动
cd ttms/backend
mvn spring-boot:run
```

后端默认使用 H2 内存数据库（自动建表 + 种子数据），无需本地 MySQL。

### 2. 启动前端（React + Vite，端口 3000）

```bash
# 关闭 Mock，启用后端联调
cd ttms/frontend
$env:VITE_ENABLE_MOCK='false'
pnpm dev -- --host 127.0.0.1
```

访问：`http://127.0.0.1:3000/`

### 3. 测试账号

| 类型 | 账号 | 密码 |
|------|------|------|
| 管理员 | `admin` | `123456` |
| 观众 | `customer` | `123456` |

### 4. 前端 Mock 模式

如需脱离后端独立运行前端，设置 `VITE_ENABLE_MOCK=true`（默认值），MSW 将拦截所有 API 请求返回模拟数据。

### 5. Vite 代理配置

| 前端路径 | 代理目标 |
|----------|---------|
| `/admin/api` | `http://localhost:8080/api` |
| `/public` | `http://localhost:8080/api` |
| `/auth` | `http://localhost:8080/api` |
| `/sales` | `http://localhost:8080/api` |
| `/finance` | `http://localhost:8080/api` |

## 联调验证清单

- [ ] 后端 `mvn test` 通过
- [ ] 后端 `mvn spring-boot:run` 启动成功（端口 8080）
- [ ] 前端 `pnpm dev` 启动成功（端口 3000）
- [ ] 管理端登录（`admin / 123456`）成功
- [ ] 观众端浏览首页、选座、登录、下单链路通
- [ ] 售票、验票、退票核心流程走通

> 更多详情见 [`ttms/docs/project-completion.md`](ttms/docs/project-completion.md)

---

## 智能体执行规范

以下规则**所有智能体必须严格遵守**，不可违反。

### 语言规范

所有输出、注释、文档、提交信息、代码审查、交互对话均使用**中文**。智能体在创作、生成、修改任何内容时，必须使用中文。

### 注释规范

编写代码时，**每个函数和每个类都必须写注释**，说明其功能与用途：

- **函数/方法**：注释说明该函数做什么（而非怎么实现）
- **类/接口**：注释说明该类的职责与用途
- 注释格式不限（JSDoc、Javadoc、Docstring 均可），必须使用中文

### Git 分支规范

- **禁止直接在 `main` 分支上修改代码**
- 每次修改前，先拉取 `main` 分支最新代码
- 从 `main` 创建新的功能/修复分支，在分支上进行开发
- 开发完成后，确认与 `main` 无冲突，再将分支合并回 `main`
- 合并完成后删除开发分支，保持仓库整洁

### 设计参考规范

- **所有开发和设计工作，必须在开工前阅读 [`ttms/docs/TTMS软件设计与需求分析.md`](ttms/docs/TTMS软件设计与需求分析.md)**
- 任何代码实现、接口定义、数据库设计、页面开发都必须以该文档为依据
- 具体的开发细节必须依照各自的模块设计文档, 如(前端设计文档、后端设计文档、数据库设计文档等)
- **开发时必须按照设计文档中的顺序与规定执行**，不得跳过或打乱开发阶段
- 如文档内容与实际需求有出入，需先更新文档再修改代码

### 接口开发规范

- **每次开发接口前，必须先查阅接口设计文档**，确认接口的 URL、请求方法、参数、响应格式等要求
- 前端 Mock 模拟和后端真实接口均以接口文档为唯一标准
- 接口实现必须与文档保持一致，不得自行增删字段或修改响应结构
- 如需变更接口，必须先更新接口文档，再同步修改代码

### 开发任务与日志规范

- **开发代码必须拆分为小任务**，每个任务粒度控制在可独立完成和验证的范围内
- **每个任务完成后必须写日志记录**，内容包括：做了什么、涉及文件、关键决策
- **日志任务审核通过后，方可提交 Git**，未经审核不得直接 commit

## 项目概述

**汉唐剧院票务管理系统（TTMS）**—— 西安邮电大学软件工程课程设计项目。

- 项目编号：XUPTSE.2016.TTMS
- 投资方：汉唐传媒有限公司
- 开发方：西安邮电大学软件工程系
- 架构：C/S + B/S 混合架构

## 设计文档

**首要参考**：[`ttms/docs/TTMS软件设计与需求分析.md`](ttms/docs/TTMS软件设计与需求分析.md) —— 整合了需求分析、软件设计、数据模型、接口设计、物理架构等全部关键信息，所有开发工作以此为准。

其他补充文档：

| 文件 | 说明 |
| ---- | ---- |
| `ttms/docs/TTMS---需求分析.pdf` | 原始需求分析 PDF（图片类型，不可直接提取文字） |
| `ttms/docs/TTMS---软件设计(BS).pdf` | 原始软件设计 PDF（图片类型） |
| `ttms/docs/前端设计文档.md` | 前端工程化设计文档 |
| `ttms/docs/接口设计文档.md` | 前后端 REST API 接口契约 |
| `ttms/docs/开发日志/` | 前端、后端、文档开发日志 |

## 技术栈

| 类别 | 技术 |
| ------ | ------ |
| 前端 | React 19 + TypeScript + Zustand + React Router + Axios + Ant Design + Vite + MSW |
| 后端 | Spring Boot 3 + MyBatis + Maven |
| 数据库 | MySQL 5（测试用 H2） |
| 建模 | 面向对象 + UML |

## 开发模式

采用**前后端分离 + Mock 驱动开发**模式：

- **接口文档**是前后端的共同契约，双方各自参考同一份接口规范
- **前端**：使用 MSW（Mock Service Worker）模拟接口响应，不依赖后端，独立开发与调试
- **后端**：参考接口文档开发真实接口，确保与 Mock 数据格式一致
- 前后端可并行开发，互不阻塞

## 项目结构

```
ttms/
├── frontend/          # React 19 前端（管理端 + 观众端）
│   └── src/
│       ├── pages/     # 页面组件（admin/ + customer/）
│       ├── services/  # API 服务层
│       ├── mocks/     # MSW Mock 数据与处理器
│       ├── stores/    # Zustand 状态管理
│       └── types/     # TypeScript 类型定义
├── backend/           # Spring Boot 3 后端
│   └── src/main/java/com/hantang/ttms/
│       ├── controller/  # REST 控制器 + 前端兼容控制器
│       ├── service/     # 业务逻辑层
│       ├── repository/  # MyBatis Mapper
│       └── domain/      # 实体类
├── database/          # MySQL 建表与种子数据
└── docs/              # 全部设计文档与开发日志
```
