# TTMS 项目完成说明

## 完成范围

本次完成后，项目具备管理端和观众端的基础可运行闭环。

管理端：

- 登录
- 工作台
- 演出厅管理
- 剧目管理
- 演出计划管理
- 售票记录
- 退票处理
- 验票管理
- 员工管理
- 观众管理
- 角色权限查看
- 财务统计

观众端：

- 首页
- 放映安排
- 选座订票
- 确认订单
- 支付结果
- 我的订单
- 票房榜单
- 登录注册
- 联系我们

## 联调账号

本地 H2 演示数据：

| 类型 | 账号 | 密码 |
|------|------|------|
| 管理员 | `admin` | `123456` |
| 观众 | `customer` | `123456` |

## 启动方式

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

访问地址：`http://127.0.0.1:3000/`

## 验证结果

- 后端 `mvn test` 通过。
- 前端 `npm run build` 通过。
- Vite 代理已覆盖 `/admin/api`、`/public`、`/auth`、`/sales`、`/finance`。
- 本地联调已验证 `/public/schedules` 能通过前端代理返回后端排片数据。

## 说明

角色权限模块当前实现为静态角色权限查看，满足课设展示和权限结构说明。真实动态授权、菜单裁剪和细粒度资源分配可作为后续增强。
