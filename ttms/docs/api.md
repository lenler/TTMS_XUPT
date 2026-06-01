# TTMS API 清单

统一前缀：`/api`

统一响应：

```json
{
  "code": "10000",
  "message": "请求成功",
  "data": {}
}
```

## 演出厅

- `GET /studios?name=` 查询演出厅。
- `POST /studios` 新增演出厅。
- `PUT /studios/{id}` 修改演出厅。
- `DELETE /studios/{id}` 停用演出厅。

## 座位

- `GET /seats?studioId=` 查询演出厅座位。
- `PUT /seats/{id}` 修改座位状态。

## 剧目

- `GET /plays?name=` 查询剧目。
- `POST /plays` 新增剧目。
- `PUT /plays/{id}` 修改剧目。
- `DELETE /plays/{id}` 下线剧目。

## 演出计划

- `GET /schedules?playId=` 查询排期。
- `POST /schedules` 创建演出计划并生成票。
- `POST /schedules/{id}/tickets` 为指定计划生成票。

## 票务

- `GET /tickets?scheduleId=` 查询某场次票据。
- `POST /tickets/{id}/check-in` 入场验票。

## 销售

- `GET /sales?date=&employeeId=&customerId=` 查询销售单。
- `GET /sales/{id}` 查询销售单详情。
- `POST /sales/orders` 下单锁票。
- `POST /sales/{id}/payments` 支付确认。
- `POST /sales/{id}/refund` 退票。

## 登录与用户

- `POST /auth/login` 员工或顾客登录，`userType` 可为 `employee` 或 `customer`。
- `POST /auth/customers/register` 顾客注册。
- `GET /users/employees?keyword=` 查询员工。
- `POST /users/employees` 新增员工。
- `PUT /users/employees/{id}` 修改员工。
- `DELETE /users/employees/{id}` 停用员工。
- `GET /users/customers?keyword=` 查询顾客。
- `POST /users/customers` 新增顾客。
- `PUT /users/customers/{id}` 修改顾客。
- `DELETE /users/customers/{id}` 停用顾客。

## 财务

- `GET /finance/daily?date=&employeeId=` 查询个人或全剧院日销售额。
- `GET /finance/theater?startDate=&endDate=` 查询剧院销售业绩。
- `GET /finance/schedules/{scheduleId}` 查询场次票房与上座率。

## 观众端公开接口

- `GET /public/plays?name=` 查询可展示剧目。
- `GET /public/schedules?playId=` 查询可售放映安排。

## 管理端前端兼容接口

当前 React 管理端页面统一调用 `/admin/api/*`，后端在 `/api/admin/api/*` 下提供兼容接口。兼容接口返回前端约定格式：

```json
{
  "resCode": "10000",
  "resMsg": "请求成功",
  "data": {}
}
```

分页响应：

```json
{
  "list": [],
  "total": 0,
  "page": 1,
  "pageSize": 10
}
```

### 基础管理

- `GET /admin/api/studios?page=&pageSize=&keyword=` 查询演出厅。
- `GET /admin/api/studios/{id}` 查询演出厅详情。
- `POST /admin/api/studios` 新增演出厅。
- `PUT /admin/api/studios/{id}` 修改演出厅。
- `DELETE /admin/api/studios/{id}` 停用演出厅。
- `GET /admin/api/plays?page=&pageSize=&keyword=&type=&lang=` 查询剧目。
- `GET /admin/api/plays/{id}` 查询剧目详情。
- `POST /admin/api/plays` 新增剧目。
- `PUT /admin/api/plays/{id}` 修改剧目。
- `DELETE /admin/api/plays/{id}` 下线剧目。
- `GET /admin/api/dicts?parentId=type|lang|position` 查询前端下拉字典。

### 排期与票务

- `GET /admin/api/schedules?page=&pageSize=&playId=&studioId=` 查询演出计划。
- `GET /admin/api/schedules/{id}` 查询演出计划详情。
- `POST /admin/api/schedules` 新增演出计划并自动生成票。
- `PUT /admin/api/schedules/{id}` 修改演出计划，按剧目时长检测演出厅时间冲突。
- `DELETE /admin/api/schedules/{id}` 停用演出计划。
- `GET /admin/api/schedules/{scheduleId}/tickets?pageSize=&status=` 查询某场次票据座位状态。
- `POST /admin/api/tickets/{ticketId}/verify` 验票。
- `GET /admin/api/checks?page=&pageSize=` 查询验票记录。

### 用户与销售

- `POST /admin/api/login` 管理端登录，兼容前端返回 `token` 和 `employee`。
- `GET /admin/api/current-user` 查询当前登录用户。
- `GET /admin/api/current-user/menus` 查询当前用户菜单。
- `GET /admin/api/employees?page=&pageSize=&keyword=&role=` 查询员工。
- `GET /admin/api/employees/{id}` 查询员工详情。
- `POST /admin/api/employees` 新增员工。
- `PUT /admin/api/employees/{id}` 修改员工。
- `DELETE /admin/api/employees/{id}` 停用员工。
- `GET /admin/api/customers?page=&pageSize=&keyword=` 查询观众。
- `GET /admin/api/customers/{id}` 查询观众详情。
- `PUT /admin/api/customers/{id}/status` 封禁或解封观众。
- `GET /admin/api/sales?page=&pageSize=` 查询销售单。
- `GET /admin/api/sales/{id}` 查询销售单详情。
- `POST /admin/api/sales` 线下售票，下单后立即支付。
- `POST /admin/api/sales/{id}/refund` 退票。
