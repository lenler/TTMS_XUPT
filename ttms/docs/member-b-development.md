# 成员 B 后端开发文档

## 模块范围

成员 B 主要负责票据管理、售票退票、验票，以及为了当前前端页面可直接联调而补充的管理端兼容接口。

本分支：`codex/ticket-management`

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3 |
| ORM | Spring Data JPA / Hibernate |
| 数据库 | MySQL，测试与本地联调可使用 H2 |
| 构建工具 | Maven |
| JDK | JDK 17 |
| 前端联调 | React + Vite，通过 `/admin/api` 代理到后端 `/api/admin/api` |

## 关键表结构约定

核心票务表：

- `studios`：演出厅，包含 `row_count`、`col_count`、`status`。
- `seats`：座位，关联 `studio_id`，包含 `row_no`、`col_no`、`status`。
- `plays`：剧目，包含 `duration_minutes`、`base_price`、`status`。
- `schedules`：演出计划，关联 `studio_id`、`play_id`，包含 `show_time`、`ticket_price`、`status`。
- `tickets`：演出票，关联 `schedule_id`、`seat_id`，包含 `price`、`status`、`lock_time`。
- `sales` / `sale_items`：销售单和销售明细。

重要约束：

- `tickets(schedule_id, seat_id)` 必须唯一。
- 创建演出计划后自动按演出厅座位生成票。
- 演出计划冲突检测按 `[showTime, showTime + play.durationMinutes)` 判断时间段重叠。
- `DELETE /studios/{id}`、`DELETE /plays/{id}`、管理端兼容删除均为状态停用，不做物理删除。

## 状态流转

票据状态：

| 状态 | 含义 |
|------|------|
| `AVAILABLE` | 可售 |
| `LOCKED` | 已锁定 |
| `SOLD` | 已售 |
| `CHECKED` | 已验票 |
| `REFUNDED` | 已退票 |
| `VOIDED` | 作废 |

售票主流程：

1. 创建排期后生成 `AVAILABLE` 票。
2. 下单时校验票据可售，将票据更新为 `LOCKED`，写入 `lock_time`。
3. 支付时校验锁票未超时，将票据更新为 `SOLD`，清空 `lock_time`。
4. 验票时只允许 `SOLD` 通过，通过后更新为 `CHECKED`。
5. 退票时禁止退已验票 `CHECKED` 的票。

## 后端接口分层

后端保留两套接口：

- 业务 REST 接口：`/api/studios`、`/api/plays`、`/api/schedules`、`/api/tickets`、`/api/sales` 等。
- 前端兼容接口：`/api/admin/api/*`，返回当前 React 前端约定的 `resCode/resMsg/data`。

兼容接口只负责路径、字段和状态码适配，核心业务仍复用已有 Service：

- `AdminManagementCompatController`：演出厅、剧目、字典、员工。
- `AdminCatalogCompatController`：排期、观众。
- `AdminTicketCompatController`：票据、售票、退票、验票。

## 管理端兼容接口清单

| 模块 | 接口 |
|------|------|
| 登录 | `POST /admin/api/login`，`GET /admin/api/current-user`，`GET /admin/api/current-user/menus` |
| 演出厅 | `GET/POST/PUT/DELETE /admin/api/studios`，`GET /admin/api/studios/{id}` |
| 剧目 | `GET/POST/PUT/DELETE /admin/api/plays`，`GET /admin/api/plays/{id}` |
| 字典 | `GET /admin/api/dicts?parentId=type|lang|position` |
| 排期 | `GET/POST/PUT/DELETE /admin/api/schedules`，`GET /admin/api/schedules/{id}` |
| 票据 | `GET /admin/api/schedules/{scheduleId}/tickets` |
| 售票 | `GET/POST /admin/api/sales`，`GET /admin/api/sales/{id}` |
| 退票 | `POST /admin/api/sales/{id}/refund` |
| 验票 | `POST /admin/api/tickets/{ticketId}/verify`，`GET /admin/api/checks` |
| 员工 | `GET/POST/PUT/DELETE /admin/api/employees`，`GET /admin/api/employees/{id}` |
| 观众 | `GET /admin/api/customers`，`GET /admin/api/customers/{id}`，`PUT /admin/api/customers/{id}/status` |

## 本地运行

后端：

```powershell
$env:JAVA_HOME='E:\develop\DevTools\Java\jdk-17'
$env:MAVEN_HOME='E:\develop\DevTools\Maven\apache-maven-3.9.16'
$env:Path="$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:Path"
cd ttms/backend
mvn spring-boot:run
```

前端：

```powershell
cd ttms-frontend
$env:VITE_ENABLE_MOCK='false'
npm run dev -- --host 127.0.0.1
```

前端访问：`http://127.0.0.1:3000/`

## 验证

已执行：

```powershell
cd ttms/backend
mvn test

cd ttms-frontend
npm run build
```

验收重点：

- 售票页能加载场次、观众和座位图。
- 选择可售座位后可创建销售单。
- 退票页能查询销售单并退未验票的票。
- 验票页输入已售票 ID 后能完成验票。
- 排期新增和修改按剧目时长检测同厅冲突。
