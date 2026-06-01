# 部署与运行

## 前置环境

- JDK 17+
- Maven 3.9+
- Node.js 20+
- MySQL 8+

当前机器已检测到 Node/npm，但未检测到 Java/Maven，因此本次只完成代码结构和前端依赖校验。

## 数据库

```bash
mysql -uroot -p < database/schema.sql
mysql -uroot -p < database/seed.sql
```

创建应用用户示例：

```sql
CREATE USER 'ttms'@'localhost' IDENTIFIED BY 'ttms';
GRANT ALL PRIVILEGES ON ttms.* TO 'ttms'@'localhost';
FLUSH PRIVILEGES;
```

## 后端

```bash
cd backend
mvn spring-boot:run
```

服务地址：`http://localhost:8080/api`

## 前端

```bash
cd frontend
npm install
npm run dev
```

开发地址：`http://127.0.0.1:5173`
