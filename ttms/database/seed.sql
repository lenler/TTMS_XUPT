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
VALUES (1, '话剧', '中文', '长安夜话', '汉唐剧院示例剧目', 120, 100.00, 'ACTIVE', NOW(), NOW());

INSERT INTO employees (id, employee_no, name, position, password_hash, status, created_at, updated_at)
VALUES
  (1, 'admin', '系统管理员', '系统管理员', '123456', 'ACTIVE', NOW(), NOW()),
  (2, 'seller01', '售票员01', '售票员', '123456', 'ACTIVE', NOW(), NOW());

INSERT INTO customers (id, username, password_hash, name, phone, balance, status, created_at, updated_at)
VALUES (1, 'customer01', '123456', '示例观众', '13800000000', 0, 'ACTIVE', NOW(), NOW());

INSERT INTO roles (id, name, created_at, updated_at)
VALUES (1, 'ADMIN', NOW(), NOW()), (2, 'SELLER', NOW(), NOW()), (3, 'FINANCE', NOW(), NOW()), (4, 'CHECKER', NOW(), NOW());
