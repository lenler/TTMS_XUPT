# TTMS软件设计与需求分析

## 1. 文档说明

本文档用于整合 `doc` 目录中以下 2 份原始 PDF 材料的信息：

- `TTMS---需求分析.pdf`
- `TTMS---软件设计(BS).pdf`

整合目标是形成一份同时覆盖项目背景、业务需求、功能需求、软件设计、数据设计、接口设计、开发架构和部署设计的统一文档，便于课程设计编写、答辩和后续实现参考。

## 2. 项目资料

| 项目项 | 内容 |
| --- | --- |
| 项目名称 | 汉唐剧院票务管理系统 |
| 项目简称 | TTMS（Theater Ticket Management System） |
| 项目编号 | XUPTSE.2016.TTMS |
| 投资方 | 汉唐传媒有限公司（简称“汉唐传媒”） |
| 用户 | 汉唐传媒有限公司下属各剧院 |
| 开发方 | 西安邮电大学软件工程系 |
| 项目性质 | 虚拟项目，专供软件工程课程教学案例使用 |

## 3. 项目综述

### 3.1 项目背景

汉唐传媒有限公司在剧院管理过程中存在业务管理混乱、工作效率低、人工处理强、容易出错等问题。为了提升剧院的信息化水平，需要建设一套统一的剧院票务管理系统，对剧目管理、演出计划、售票、退票、验票、统计分析等业务进行系统化支撑。

### 3.2 项目目标

项目建设目标如下：

- 解决剧院日常管理中的人工处理问题。
- 提高售票、验票、统计、结算等业务处理效率。
- 降低人工安排和人工核对带来的差错率。
- 为剧院运营、财务和系统管理提供统一的信息化平台。

### 3.3 组织结构

汉唐剧院的组织结构可分为以下部门：

- 运营部
  - 演出厅管理
  - 引进剧目
  - 安排演出与宣传
  - 买票、退票、咨询
  - 入场验票
  - 演出秩序维护
- 财务部
  - 财务预算、决算
  - 流动资金管理
  - 售票日结算
  - 收支管理
- 技术部
  - 演出设施设备维护
  - 演出技术支持

### 3.4 岗位角色

| 岗位 | 所在部门 | 职责 |
| --- | --- | --- |
| 财务经理 | 财务部 | 资金审计，剧院预算与决算管理 |
| 会计员 | 财务部 | 日常资金流动管理、统计、售票日报结算 |
| 设备运维 | 技术部 | 演出设施设备提供与维护，提供剧目技术支持与解决方案 |
| 场务员 | 运营部 | 进场验票，引导观众入场与出场，维持场内秩序 |
| 售票员 | 运营部 | 完成售票服务，办理退票业务 |
| 运营经理 | 运营部 | 剧目引进、演出计划制定 |

## 4. 需求分析

### 4.1 业务需求概览

需求分析材料中给出的核心业务包括：

- 顾客侧业务
  - 看演出
  - 购票
  - 入场
  - 观看演出
  - 退场
  - 退票
  - 购买零食
- 剧院内部支持业务
  - 引进剧目
  - 制定演出计划
  - 宣传
  - 售票
  - 演出
  - 剧目下线
  - 制作票

### 4.2 现有业务流程

#### 4.2.1 顾客观看演出流程（外部）

顾客观看演出的现有业务流程为：

1. 购票
2. 入场
3. 观看演出
4. 退场
5. 退票

#### 4.2.2 剧院支持业务流程（内部）

剧院内部围绕演出运营的业务链条为：

1. 引进剧目
2. 制定演出计划
3. 宣传
4. 售票
5. 演出
6. 剧目下线
7. 制作票

### 4.3 关键业务步骤分析：制定演出计划

| 项目 | 内容 |
| --- | --- |
| 负责部门 | 运营部 |
| 岗位角色 | 计划员 |
| 业务流程 | 选择剧目 -> 选择合适演出厅 -> 安排演出时间 |
| 存在问题 | 依赖人工安排，工作强度大，且容易出错 |
| 要求与期望 | 计算机辅助安排；支持时间与资源冲突检测 |

### 4.4 业务流程优化：自助检票

需求分析中明确提出了检票业务优化目标：

| 项目 | 内容 |
| --- | --- |
| 业务目标 | 检票 |
| 使用方式 | 自助 |
| 工作模式 | 软硬件配合 |
| 处理流程 | 顾客输入票 ID -> 软件检查票有效性 -> 打开闸机 |

### 4.5 系统体系结构需求

在需求分析阶段，系统体系结构被描述为一套 C/S 架构系统：

- 数据库服务器：`DB Server`
- 数据库：`SQLServer 2008`
- 工作终端：汉唐票务管理系统客户端
- 通信方式：`TCP/IP`、`JDBC`

工作终端覆盖的岗位包括但不限于：

- 售票员
- 财务经理
- 会计
- 其他内部岗位

### 4.6 功能需求

#### 4.6.1 系统总用例

需求分析从角色视角给出了系统总用例图，涉及的主要角色包括：

- 售票员
- 运营经理
- 场务员
- 财务经理
- 会计
- 系统管理员

系统功能总体分为 3 大类：

- 运营管理
- 财务管理
- 系统管理

#### 4.6.2 运营管理

运营管理相关功能包括：

- 查询演出票
- 安排演出
- 管理剧目
- 统计个人日销售额
- 售票
- 统计上座率
- 查询演出
- 退票
- 入场验票

对应角色主要包括：

- 售票员
- 运营经理
- 场务员

#### 4.6.3 财务管理

财务管理相关功能包括：

- 统计个人日销售额
- 分析剧院盈利数据
- 统计剧院销售业绩
- 收取买票款

对应角色主要包括：

- 财务经理
- 会计

#### 4.6.4 系统管理

系统管理相关功能包括：

- 管理演出厅
- 设置座位
- 管理系统用户

对应角色为：

- 系统管理员

### 4.7 非功能需求

需求分析材料中给出的非功能需求维度包括：

- 界面
- 性能
- 安全性
- 可靠性
- 其他扩展性要求

### 4.8 产品提交

项目交付物包括：

- 应用系统软件包，1CD
- 数据库初始数据，1CD
- 应用系统源代码及开发过程文档电子版，1CD
- 纸质文档
  - 需求规格说明书
  - 软件设计说明书
  - 软件测试报告
  - 软件用户手册
  - 软件管理维护手册

### 4.9 项目验收

项目验收从以下维度开展，并需要逐项说明验收标准：

- 功能
- 性能
- 安全性
- 可靠性
- 其他质量属性

## 5. 软件设计

### 5.1 设计决策

软件设计材料中给出的核心设计决策如下：

- 使用面向对象技术进行系统分析与设计，并使用 UML 描述系统设计模型。
- 使用 Java 语言进行服务端开发。
- 前端使用 **React + TypeScript + Ant Design** 技术栈，构建现代化的单页应用（SPA）。
- 逻辑架构采用前后端分离的分层体系结构。
- 物理架构整体为 B/S 架构，内部管理端与观众端均通过浏览器访问。
- 服务器端包括 Web 服务器和 `MySQL` 数据库服务器（具体技术栈待后续补充）。
- 数据采用集中式存储。
- 数据库使用关系数据库 `MySQL`。

### 5.2 需求架构与设计架构的关系说明

源材料中存在“需求阶段 C/S 架构”和“设计阶段 C/S + B/S 混合架构”两种表述。可将其理解为：

- 在需求阶段，系统以传统工作终端访问数据库服务器的 C/S 模式进行抽象描述。
- 在设计阶段，系统进一步扩展为混合架构：
  - 内部管理、售票、验票等业务由服务端组件支撑。
  - 后台管理、网络购票等能力通过 Web 前端与 Web 服务端协同实现。
- 数据库产品也从需求阶段描述的 `SQLServer 2008` 演化为设计阶段采用的 `MySQL 5`。

### 5.3 逻辑架构设计

#### 5.3.1 总体逻辑架构

系统逻辑架构采用前后端分离的分层结构，前端与后端通过 RESTful API（JSON）通信。

**前端架构（React + TypeScript + Ant Design）**：

- 页面层（Pages）—— 路由对应的页面组件
- 组件层（Components）—— 可复用的 UI 组件
- 状态管理层（Store / Hooks）—— 全局状态与局部状态
- API 服务层（Services）—— 封装 HTTP 请求，与后端接口对接

**后端架构（待补充）**：

- Web 控制器层（Controller）—— 接收 HTTP 请求，返回 JSON
- 业务逻辑层（Service）—— 核心业务逻辑
- 业务模型层（Model）—— 领域实体
- 持久化接口层（IDAO）—— 数据访问接口
- 持久化实现层（DAO）—— 数据访问实现
- 技术服务层（DBUtil）—— 数据库连接

#### 5.3.2 命名规则

**前端命名规则**：

| 类别 | 命名规则 | 示例 |
| --- | --- | --- |
| 页面组件 | PascalCase，以 `Page` 结尾 | `StudioListPage.tsx` |
| 通用组件 | PascalCase | `StudioForm.tsx`, `SeatPicker.tsx` |
| API 服务 | camelCase，以 `Service` 结尾 | `studioService.ts` |
| 类型定义 | PascalCase | `Studio.ts`, `Schedule.ts` |
| 自定义 Hook | `use` 前缀 + PascalCase | `useStudios.ts` |
| 样式文件 | 与组件同名 | `StudioForm.module.css` |

**后端命名规则**（待后端技术栈确定后细化）：

| 所在层 | 命名规则 | 命名示例 |
| --- | --- | --- |
| Web 控制器层 | 实体名 + `Servlet` | `StudioServlet` |
| 业务逻辑层 | 实体名 + `Srv` | `StudioSrv` |
| 业务模型层 | 实体名 | `Studio` |
| 持久化接口层 | `I` + 实体名 + `DAO` | `IStudioDAO` |
| 持久化层 | 实体名 + `DAO` | `StudioDAO` |

#### 5.3.3 领域模型中的核心实体

设计材料中出现的核心业务实体包括：

- `Sale`
- `SaleItem`
- `Studio`
- `Seat`
- `Play`
- `Schedule`
- `Ticket`
- `Clerk`
- `Customer`
- `Comment`

领域关系可概括为：

- `Studio` 包含多个 `Seat`
- `Play` 对应多个 `Schedule`
- `Schedule` 上演于某个 `Studio`
- `Schedule` 生成多个 `Ticket`
- `Sale` 记录销售行为
- `Sale` 包含多个 `SaleItem`
- `SaleItem` 对应 `Ticket`
- `Customer` 购买 `Ticket`
- `Comment` 对应售票员或观众之一

#### 5.3.4 前端页面与组件结构

基于 React 组件化思想，前端页面结构设计如下：

**管理端页面与组件**：

| 父模块 | 子模块 | 页面路由 | 主要组件 |
| --- | --- | --- | --- |
| 剧院管理 | 演出厅管理 | `/admin/studio` | `StudioListPage`, `StudioFormModal` |
| 剧院管理 | 剧目管理 | `/admin/play` | `PlayListPage`, `PlayFormModal` |
| 剧院管理 | 演出计划 | `/admin/schedule` | `ScheduleListPage`, `ScheduleFormModal` |
| 剧院管理 | 验票管理 | `/admin/check` | `CheckListPage` |
| 用户管理 | 观众管理 | `/admin/customer` | `CustomerListPage` |
| 用户管理 | 员工管理 | `/admin/employee` | `EmployeeListPage` |
| 权限管理 | 角色管理 | `/admin/role` | `RoleListPage`, `RoleFormModal` |
| 票务管理 | 售票管理 | `/admin/sale` | `SaleListPage` |
| 财务管理 | 销售统计 | `/admin/finance` | `FinanceDashboard` |

**观众端页面与组件**：

| 页面 | 路由 | 主要组件 |
| --- | --- | --- |
| 首页 | `/` | `HomePage`, `Banner`, `HotPlays` |
| 放映安排 | `/schedule` | `SchedulePage`, `PlayCard` |
| 选座订票 | `/seats/:scheduleId` | `SeatPicker`, `TicketCart` |
| 确认订单 | `/order` | `OrderConfirm`, `PaymentPanel` |
| 订单结果 | `/result/:orderId` | `OrderResult` |
| 榜单 | `/board` | `BoardPage`, `RankTable` |
| 联系我们 | `/contact` | `ContactPage` |
| 登录 | `/login` | `LoginPage` |
| 注册 | `/register` | `RegisterPage` |

> 注：原 PDF 中的 C/S 架构 UI 单元（`mainUITmpl`、`popUITmpl`、`SysLoginUI` 等）为传统工作终端界面，现由 React 前端统一承载。

### 5.4 关键处理流程设计

#### 5.4.1 管理演出厅

管理演出厅的前后端协作流程包括：

1. 业务员打开演出厅管理界面。
2. 页面加载触发 `OnLoad()`。
3. 页面调用查询逻辑 `Search("")`。
4. 控制层创建服务对象。
5. 服务层调用 `Fetch("")`。
6. 返回演出厅列表 `studioList`。
7. 前端完成查询展示。
8. 在同一业务上下文中支持以下操作：
   - 查询演出厅
   - 添加演出厅
   - 修改演出厅
   - 删除演出厅
   - 设置座位

#### 5.4.2 管理演出厅：提取全部数据

提取演出厅数据的处理流程为：

1. `StudioServlet` 调用 `StudioSrv.fetch()`。
2. `StudioSrv` 调用 `IStudioDAO.select("")`。
3. DAO 层创建数据库连接并执行 `select` 语句。
4. 遍历 `ResultSet`。
5. 根据读取到的数据库记录重建 `Studio` 对象。
6. 将对象逐个加入 `stuList` 并返回。

#### 5.4.3 管理演出厅：修改

修改演出厅的处理流程为：

1. 业务员在演出厅管理界面发起“修改演出厅”操作。
2. 打开修改界面并装载已选中的演出厅对象。
3. 界面加载完成后允许编辑。
4. 用户保存。
5. 页面提交 `Update(stu)`。
6. 服务层创建业务对象并调用 `modify(stu)`。
7. DAO 层执行 `update(stu)`。
8. 返回处理完成结果。

#### 5.4.4 网络购票：初始化界面

网络购票的初始化过程包括：

1. 前端剧目界面发起 `search()`。
2. `SaleServlet` 创建 `ScheduleSrv` 与 `TicketSrv`。
3. `ScheduleSrv` 调用 `fetchByID()`，底层通过 `IScheduleDAO.select()` 查询排片信息。
4. `TicketSrv` 调用 `fetchBySchID()`，底层通过 `ITicketDAO.select()` 查询票信息。
5. 返回 `TicketData` 供前端页面渲染。

#### 5.4.5 网络购票：选票与下单

网络购票选票与下单的处理流程为：

1. 观众在购票界面选票。
2. 前端调用 `UpdateCart(tid)` 更新购物车。
3. 观众提交下单。
4. 前端调用 `placeOrder(tidList)`。
5. 服务层执行下单逻辑并返回 `orderID`。
6. 票务服务与销售服务分别对待售票执行 `lock(tidList)`。
7. 当锁定成功后，创建 `Sale` 对象。
8. 遍历 `tidList`，逐个创建 `SaleItem`。
9. 调用 DAO 层 `insert(sale)` 完成订单落库。
10. 返回订单号。

#### 5.4.6 网络购票：在线支付

在线支付过程涉及前端、服务端和第三方支付平台三方交互：

1. 观众选择支付方式。
2. 前端向服务端发起 `reqPayment()`。
3. 服务端向第三方支付平台发起支付请求。
4. 第三方支付平台返回支付码。
5. 前端展示支付码。
6. 观众扫码支付。
7. 前端调用 `makePayment()`。
8. 服务端查询订单 `sale := selectByID()`。
9. 获取订单中的票 ID 集合 `tidList := getTicketIDs()`。
10. 对相关票执行占有确认 `claim()`。
11. 当确认成功后更新销售单状态。
12. 返回支付成功结果。

### 5.5 界面与接口设计

#### 5.5.1 管理端功能结构

管理端从模块上可分为以下几个部分：

- 剧院管理
  - 演出厅管理
  - 剧目管理
  - 演出计划
  - 验票管理
- 用户管理
  - 观众管理
  - 员工管理
- 权限管理
  - 资源管理
  - 角色管理
- 票务管理
  - 座位管理
  - 售票管理
  - 退票管理
- 财务管理
  - 销售统计
  - 票款管理
  - 剧目销售统计
  - 剧院销售业绩
  - 上座率统计
  - 票房统计
  - 销售业绩

#### 5.5.2 管理端页面文件

| 父模块名 | 子模块名 | 目录名 | 文件名 |
| --- | --- | --- | --- |
| 剧院管理 | 演出厅管理 | `admin\studio` | `index.html`（展示、查询、删除演出厅）；`detail.html`（增加、修改演出厅） |
| 剧院管理 | 剧目管理 | `admin\play` | `index.html`（展示、查询、删除剧目）；`detail.html`（增加、修改剧目） |
| 剧院管理 | 演出计划 | `admin\schedule` | `index.html`（展示、查询、删除演出计划）；`detail.html`（增加、修改演出计划） |
| 剧院管理 | 验票管理 | `admin\check` | `index.html` |
| 用户管理 | 观众管理 | `admin\customer` | `index.html` |
| 用户管理 | 员工管理 | `admin\employee` | `index.html` |

#### 5.5.3 观众端页面流程

观众端页面流程包括：

- 主页
- 放映安排
- 联系我们
- 榜单
- 登录注册
- 选座订票
- 确认订单
- 下单

#### 5.5.4 观众端座位图数据结构

观众端页面中，座位图采用字符串数组形式表示，例如：

```text
[
  "aaaa_aaa",
  "aaaa_aaa",
  "aaaa_aaa",
  "aaaa_aaa",
  "aaaa_aaa",
  "aaaa_aaa",
  "aaaa_aaa",
  "aaaa_aaa"
]
```

#### 5.5.5 观众端页面文件

| 页面名 | 目录名 | 文件名 |
| --- | --- | --- |
| 首页 | `customer` | `index.html` |
| 放映安排 | `customer` | `schedule.html` |
| 选座订票 | `customer` | `seats.html` |
| 购票 | `customer` | `order.html` |
| 订单结果 | `customer` | `result.html` |
| 榜单 | `customer` | `board.html` |
| 联系我们 | `customer` | `contact.html` |
| 登录 | `customer` | `login.html` |
| 注册 | `customer` | `register.html` |

#### 5.5.6 外部接口：客户端与数据库接口

客户端与数据库的访问通过持久化接口层、持久化实现层和数据库工具层协作完成：

- `Persistence Interface (IDAO)`
  - 定义接口，实现业务逻辑与数据库连接的隔离
- `Persistence (DAO)`
  - 实现对象与关系数据之间的转换
  - 可以将对象值转换为 SQL
  - 也可以利用查询结果重构对象
- `Technical Service (DBUtil)`
  - 封装数据库连接组件
  - 实现查询、更新数据库及执行存储过程

#### 5.5.7 演出厅管理接口设计

##### Web 接口

| URL | 接口名称 | 说明 |
| --- | --- | --- |
| `../StudioServlet` | `search` | 根据传入的演出厅名称模糊查询；名称为空时返回全部演出厅数据 |
| `../StudioServlet` | `add` | 将传入的演出厅添加到系统中 |
| `../StudioServlet` | `update` | 利用传入的演出厅数据更新系统中的对应记录 |
| `../StudioServlet` | `delete` | 根据 ID 匹配原则删除系统中的演出厅 |

查询演出厅返回的 JSON 数据包示例如下：

```json
{
  "resCode": "10000",
  "resMsg": "请求成功",
  "data": [
    {
      "id": "1",
      "name": "1号厅",
      "rowCount": "7",
      "colCount": "7",
      "introduction": "1号厅"
    }
  ]
}
```

##### Service 接口

| 类名 | 接口名称 | 说明 |
| --- | --- | --- |
| `StudioSrv` | `fetch` | 根据传入条件查询并返回符合条件的演出厅数据列表 |
| `StudioSrv` | `add` | 将传入的演出厅添加到系统中 |
| `StudioSrv` | `modify` | 利用传入的演出厅数据修改系统中的对应记录 |
| `StudioSrv` | `delete` | 根据 ID 匹配原则删除系统中的演出厅 |

##### DAO 接口

| 类名 | 接口名称 | 说明 |
| --- | --- | --- |
| `IStudioDAO` | `select` | 根据传入条件查询数据库，条件为空时返回全部演出厅数据 |
| `IStudioDAO` | `insert` | 将传入的演出厅插入数据库 |
| `IStudioDAO` | `update` | 利用传入的演出厅数据修改数据库中对应记录 |
| `IStudioDAO` | `delete` | 根据 ID 匹配原则删除数据库中的演出厅 |

### 5.6 数据存储设计

#### 5.6.1 内存数据结构

系统在运行期需要维护的内存数据包括：

- 当前用户信息
- 当前用户权限

对应问题与解决思路如下：

| 问题 | 解决方案 |
| --- | --- |
| 窗口间切换时如何保存数据 | 对象保存并作为参数在窗口间传递；或使用全局变量 / 单实例类 |

设计材料中给出了单实例类示例，用于全局数据共享控制：

```java
public class Singleton {
    private static Singleton uniInstance = null;

    private Singleton() {
    }

    public synchronized static Singleton GetInstance() {
        if (uniInstance == null) {
            uniInstance = new Singleton();
        }
        return uniInstance;
    }
}
```

#### 5.6.2 数据库表设计

设计材料中给出了以下数据表及关键字段。

##### 演出厅

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 演出厅ID | `int` | 主键 |
| 名称 | `varchar(100)` | 演出厅名称 |
| 行数 | `int` | 座位行数 |
| 列数 | `int` | 座位列数 |
| 简介 | `varchar(2000)` | 演出厅简介 |
| 状态 | `smallint` | 状态标识 |

##### 剧目

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 剧目ID | `int` | 主键 |
| 剧目类型 | `int` | 外键 |
| 剧目语种 | `int` | 外键 |
| 剧目名称 | `varchar(200)` | 名称 |
| 剧情简介 | `varchar(2000)` | 简介 |
| 剧目海报 | `varchar(2000)` | 海报地址 |
| 宣传片 | `varchar(2000)` | 宣传片地址 |
| 演出时长（分钟） | `int` | 时长 |
| 基准票价 | `numeric(10,2)` | 票价 |
| 剧目状态 | `smallint` | 状态 |

##### 演出票

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 票ID | `bigint` | 主键 |
| 座位ID | `int` | 外键 |
| 计划ID | `int` | 外键 |
| 票价 | `numeric(10,2)` | 售价 / 票价 |
| 状态 | `smallint` | 状态 |
| 加锁时间 | `timestamp` | 锁定时间 |

##### 演出计划

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 计划ID | `int` | 主键 |
| 演出厅ID | `int` | 外键 |
| 剧目ID | `int` | 外键 |
| 演出时间 | `datetime` | 排期时间 |
| 票价 | `numeric(10,2)` | 本场票价 |
| 状态 | `smallint` | 状态 |

##### 座位

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 座位ID | `int` | 主键 |
| 演出厅ID | `int` | 外键 |
| 行号 | `int` | 行 |
| 列号 | `int` | 列 |
| 状态 | `smallint` | 状态 |

##### 数据字典

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 字典ID | `int` | 主键 |
| 上级字典ID | `int` | 外键 |
| 字典序号 | `int` | 序号 |
| 字典名称 | `varchar(200)` | 名称 |
| 字典值 | `varchar(100)` | 值 |

##### 销售单明细

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 销售项ID | `bigint` | 主键 |
| 票ID | `bigint` | 外键 |
| 销售单ID | `bigint` | 外键 |
| 售价 | `numeric(10,2)` | 明细售价 |

##### 销售（退票）单

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 销售单ID | `bigint` | 主键 |
| 员工ID | `int` | 外键 |
| 顾客ID | `int` | 外键 |
| 销售时间 | `datetime` | 时间 |
| 支付金额 | `decimal(10,2)` | 支付金额 |
| 找零 | `numeric(10,2)` | 找零 |
| 类别 | `smallint` | 类别 |
| 状态 | `smallint` | 状态 |
| 销售类型 | `smallint` | 销售类型 |

##### 员工

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 员工ID | `int` | 主键 |
| 岗位角色 | `int` | 外键 |
| 工号 | `varchar(20)` | 工号 |
| 姓名 | `varchar(100)` | 姓名 |
| 性别 | `smallint` | 性别 |
| 联系电话 | `varchar(30)` | 电话 |
| 邮箱 | `varchar(100)` | 邮箱 |
| 密码 | `varchar(20)` | 密码 |
| 状态 | `smallint` | 状态 |

##### 顾客

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 顾客ID | `int` | 主键 |
| 姓名 | `varchar(100)` | 姓名 |
| 性别 | `smallint` | 性别 |
| 电话 | `varchar(30)` | 电话 |
| 邮箱 | `varchar(100)` | 邮箱 |
| 用户名 | `varchar(20)` | 用户名 |
| 密码 | `varchar(20)` | 密码 |
| 状态 | `smallint` | 状态 |
| 账户余额 | `dec(10,0)` | 余额 |
| 支付密码 | `varchar(20)` | 支付密码 |

##### 资源表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 资源ID | `int` | 主键 |
| 资源类别 | `varchar(20)` | 类别 |
| 资源名称 | `varchar(20)` | 名称 |
| 资源URL | `varchar(200)` | 资源地址 |

##### 角色资源表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 角色资源ID | `int` | 主键 |
| 角色ID | `int` | 外键 |
| 资源ID | `int` | 外键 |

##### 用户角色表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 用户角色ID | `int` | 主键 |
| 员工ID | `int` | 外键 |
| 角色ID | `int` | 外键 |

##### 角色表

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| 角色ID | `int` | 主键 |
| 角色名称 | `varchar(20)` | 角色名称 |

#### 5.6.3 外键关系

设计材料中列出的主要外键包括：

- `FK_studio_seat`
- `FK_play_sched`
- `FK_studio_sched`
- `FK_sched_ticket`
- `FK_seat_ticket`
- `FK_super_child_dict`
- `FK_dict_lan_play`
- `FK_dict_type_play`
- `FK_ticket_sale_item`
- `FK_sale_sale_item`
- `FK_employee_sale`
- `FK_customer_sale`
- `FK_emp_position`
- `FK_user_role`
- `FK_role_user`
- `FK_role_resource`
- `FK_res_role`

### 5.7 开发架构

软件设计材料中专门给出了“开发架构”章节，但当前可提取文本极少，原始页面主要以图示方式呈现。基于现有可提取信息，可以确认：

- 系统存在单独的开发架构设计章节。
- 开发实现与逻辑分层、页面目录组织、DAO/Service/Controller 分工、Web 前后端协作紧密相关。
- 若后续需要补齐该部分，可结合原始 PDF 图示或实际源码目录继续细化为“开发目录结构图 + 模块依赖图 + 构建部署流程”。

结合当前课程设计实现计划，技术栈暂定如下：

- 前端：采用 `React`、`TypeScript` 和 `Ant Design` 作为主要技术方案，用于实现管理端和观众端页面。
- 后端：暂时空悬，待后续根据实现范围、数据库方案和接口设计进一步补充。

### 5.8 物理架构设计

系统的物理部署采用混合架构，节点包括：

- `MySQL` 数据库服务器
- `Tomcat` 管理服务器
- `Tomcat` 售票服务器
- TTMS 数据库
- 售票处工作终端
- 检票工作终端
- 业务部门工作终端
- 顾客电脑
- 线下售票 Web 前端
- 网络购票 Web 前端
- 后台管理 Web 前端
- 后台管理服务端组件
- 售票管理服务端组件

各节点之间通过以下方式通信：

- `TCP/IP`
- `TCP/IP, JDBC`

### 5.9 详细设计：演出厅管理

演出厅管理的类设计包括以下核心类与职责：

#### 5.9.1 `Studio`

- 属性
  - `id: Integer`
  - `name: String`
  - `rowcount: Integer`
  - `colcount: Integer`
  - `intro: String`
  - `status: Integer`
- 主要方法
  - `Studio(ID, name, rowcount, colcount, intro)`
  - `getID(): Integer`
  - `setID()`

#### 5.9.2 `StudioDAO`

- `insert(stu): Integer`
- `update(stu): Integer`
- `delete(ID): Integer`
- `select(condt): List<Studio>`

#### 5.9.3 `IStudioDAO`

- `insert(stu): Integer`
- `update(stu): Integer`
- `delete(ID): Integer`
- `select(condt): List<Studio>`

#### 5.9.4 `StudioServlet`

- 继承：`HttpServlet`
- 方法
  - `doGet(request, response)`
  - `doPost(request, response)`
  - `add(request, response)`
  - `update(request, response)`
  - `delete(request, response)`
  - `search(request, response)`

#### 5.9.5 `StudioSrv`

- `fetch(condt): List<Studio>`
- `add(stu): Integer`
- `modify(stu): Integer`
- `delete(ID): Integer`

#### 5.9.6 `DAOFactory`

- `creatStudiorDAO(): IStudioDAO`

#### 5.9.7 `DBUtil`

- `openConnection(): Boolean`
- `execQuery(sql: String): ResultSet`
- `getInsertObjectIDs(sql): ResultSet`
- `getInsertObjectIDs(sqls: List<String>): ResultSet`
- `execCommand(sqlCmd): Integer`
- `close()`

## 6. 流程建模补充建议

### 6.1 说明

当前 `doc` 目录中未包含单独的 `UML--活动图.pdf`，因此本节不作为原始 PDF 内容的摘录，而是基于需求分析和软件设计中已经明确的业务流程、顺序图和处理流程，对后续课程设计建模给出补充建议。

### 6.2 建议绘制的活动图

结合 TTMS 业务，可优先绘制以下活动图：

- 制定演出计划
  - 选择剧目
  - 选择演出厅
  - 安排时间
  - 冲突检测
  - 保存计划
- 购票流程
  - 浏览排片
  - 选座
  - 下单
  - 支付
  - 出票
- 验票流程
  - 输入票 ID
  - 校验票有效性
  - 放行 / 拒绝入场
- 退票流程
  - 校验订单
  - 校验退票规则
  - 执行退款
  - 更新票状态

### 6.3 建模注意事项

建模时应注意：

- 当需要体现“不同岗位分别做什么”时，适合使用泳道图。
- 当需要体现“支付确认、票锁定、订单写入”等并发或同步点时，适合使用分叉与汇合。
- 当需要体现“票有效 / 无效”“座位已售 / 未售”等条件判断时，适合使用分支与合并。

## 7. 总结

通过整合需求分析和软件设计材料，可以得到 TTMS 的完整认识：

- 在需求层，TTMS 解决的是剧院运营、票务、财务和系统管理的信息化问题。
- 在设计层，TTMS 采用面向对象方法与 UML 建模，使用 Java 语言和分层架构实现。
- 在实现层，系统围绕演出厅、剧目、排期、座位、演出票、销售单、用户、角色与资源建立统一的数据模型。
- 在交互层，系统同时覆盖管理端与观众端，支持后台管理、线下售票、网络购票和验票业务。
- 在建模表达层，可结合活动图、顺序图和部署图清晰描述 TTMS 的业务动作流、岗位分工、前后端协作和物理部署关系。
