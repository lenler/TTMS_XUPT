-- 数据库索引优化脚本
-- 为核心业务表添加常用查询索引，提升检索性能
--
-- 使用说明：
--   mysql -u root -p ttms < database/indexes.sql
--
-- @author lyd

-- 剧目表索引：按名称和类型模糊搜索
CREATE INDEX idx_play_name ON play (name);
CREATE INDEX idx_play_type ON play (type);
CREATE INDEX idx_play_status ON play (status);

-- 演出计划索引：按剧目、演出厅、日期查询
CREATE INDEX idx_schedule_play_id ON schedule (play_id);
CREATE INDEX idx_schedule_studio_id ON schedule (studio_id);
CREATE INDEX idx_schedule_date ON schedule (schedule_date);
CREATE INDEX idx_schedule_time ON schedule (start_time);

-- 座位索引：按演出厅查询
CREATE INDEX idx_seat_studio_id ON seat (studio_id);
CREATE INDEX idx_seat_status ON seat (seat_status);

-- 票据索引：按演出计划、座位、状态查询
CREATE INDEX idx_ticket_schedule_id ON ticket (schedule_id);
CREATE INDEX idx_ticket_seat_id ON ticket (seat_id);
CREATE INDEX idx_ticket_status ON ticket (ticket_status);
CREATE INDEX idx_ticket_sale_item_id ON ticket (sale_item_id);

-- 销售单索引：按顾客、员工、时间查询
CREATE INDEX idx_sale_customer_id ON sale (customer_id);
CREATE INDEX idx_sale_employee_id ON sale (employee_id);
CREATE INDEX idx_sale_time ON sale (sale_time);
CREATE INDEX idx_sale_status ON sale (status);

-- 销售明细索引
CREATE INDEX idx_sale_item_sale_id ON sale_item (sale_id);

-- 顾客索引：按账号和手机号查询
CREATE INDEX idx_customer_account ON customer (account);
CREATE INDEX idx_customer_phone ON customer (phone);

-- 员工索引：按编号和手机号查询
CREATE INDEX idx_employee_no ON employee (employee_no);
CREATE INDEX idx_employee_phone ON employee (phone);
