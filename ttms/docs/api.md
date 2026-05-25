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
