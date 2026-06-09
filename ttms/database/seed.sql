USE ttms;

INSERT INTO studios (id, name, row_count, col_count, introduction, status, created_at, updated_at)
VALUES (1, '1号厅', 8, 8, '默认演出厅', 'ACTIVE', NOW(), NOW());

INSERT INTO seats (studio_id, row_no, col_no, status, created_at, updated_at)
WITH RECURSIVE rows_cte(row_no) AS (
  SELECT 1 UNION ALL SELECT row_no + 1 FROM rows_cte WHERE row_no < 8
),
cols_cte(col_no) AS (
  SELECT 1 UNION ALL SELECT col_no + 1 FROM cols_cte WHERE col_no < 8
)
SELECT 1, rows_cte.row_no, cols_cte.col_no, 'ACTIVE', NOW(), NOW()
FROM rows_cte CROSS JOIN cols_cte;

INSERT INTO plays (id, type, language, name, introduction, duration_minutes, base_price, status, created_at, updated_at)
VALUES (1, '话剧', '中文', '长安夜话', '奥斯卡剧院示例剧目', 120, 100.00, 'ACTIVE', NOW(), NOW());

INSERT INTO employees (id, employee_no, name, position, password_hash, status, created_at, updated_at)
VALUES
  (1, 'admin', '系统管理员', '系统管理员', '123456', 'ACTIVE', NOW(), NOW()),
  (2, 'seller01', '售票员01', '售票员', '123456', 'ACTIVE', NOW(), NOW());

INSERT INTO customers (id, username, password_hash, name, phone, gender, payment_password, balance, status, created_at, updated_at)
VALUES (1, 'customer01', '123456', '示例观众', '13800000000', 0, '123456', 0, 'ACTIVE', NOW(), NOW());

INSERT INTO roles (id, name, created_at, updated_at)
VALUES (1, 'ADMIN', NOW(), NOW()), (2, 'SELLER', NOW(), NOW()), (3, 'FINANCE', NOW(), NOW()), (4, 'CHECKER', NOW(), NOW());

-- 演出计划（未来8天每晚19:30）
INSERT INTO schedules (id, studio_id, play_id, show_time, ticket_price, status, created_at, updated_at)
VALUES
  (1, 1, 1, DATE_ADD(CURDATE(), INTERVAL 0 DAY) + INTERVAL 19 HOUR + INTERVAL 30 MINUTE, 100.00, 'ACTIVE', NOW(), NOW()),
  (2, 1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY) + INTERVAL 19 HOUR + INTERVAL 30 MINUTE, 100.00, 'ACTIVE', NOW(), NOW()),
  (3, 1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY) + INTERVAL 19 HOUR + INTERVAL 30 MINUTE, 100.00, 'ACTIVE', NOW(), NOW()),
  (4, 1, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY) + INTERVAL 19 HOUR + INTERVAL 30 MINUTE, 100.00, 'ACTIVE', NOW(), NOW()),
  (5, 1, 1, DATE_ADD(CURDATE(), INTERVAL 4 DAY) + INTERVAL 19 HOUR + INTERVAL 30 MINUTE, 100.00, 'ACTIVE', NOW(), NOW()),
  (6, 1, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 19 HOUR + INTERVAL 30 MINUTE, 100.00, 'ACTIVE', NOW(), NOW()),
  (7, 1, 1, DATE_ADD(CURDATE(), INTERVAL 6 DAY) + INTERVAL 19 HOUR + INTERVAL 30 MINUTE, 100.00, 'ACTIVE', NOW(), NOW()),
  (8, 1, 1, DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 19 HOUR + INTERVAL 30 MINUTE, 100.00, 'ACTIVE', NOW(), NOW());

-- 员工-角色关联
INSERT INTO employee_roles (employee_id, role_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW()), (2, 2, NOW(), NOW());

-- 更多剧目
INSERT INTO plays (id, type, language, name, introduction, duration_minutes, base_price, status, created_at, updated_at)
VALUES
  (2, '京剧', '中文', '贵妃醉酒', '经典京剧《贵妃醉酒》选段', 90, 120.00, 'ACTIVE', NOW(), NOW()),
  (3, '音乐剧', '中文', '丝路之声', '大型原创音乐剧，展现丝路千年文明', 150, 180.00, 'ACTIVE', NOW(), NOW()),
  (4, '相声', '中文', '笑满长安', '嘻哈包袱铺相声大会', 120, 80.00, 'ACTIVE', NOW(), NOW()),
  (5, '舞蹈', '中文', '唐韵', '唐代宫廷舞蹈专场演出', 100, 150.00, 'ACTIVE', NOW(), NOW()),
  (6, '儿童剧', '中文', '哪吒闹海', '亲子儿童剧《哪吒闹海》', 90, 60.00, 'ACTIVE', NOW(), NOW());
