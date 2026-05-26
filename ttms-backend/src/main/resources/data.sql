INSERT INTO studio (id, name, row_count, col_count, introduction, status)
SELECT 1, '1号厅', 7, 7, '小型演出厅，适合话剧', 1
WHERE NOT EXISTS (SELECT 1 FROM studio WHERE id = 1);

INSERT INTO studio (id, name, row_count, col_count, introduction, status)
SELECT 2, '2号厅', 10, 12, '中型演出厅，适合音乐会', 1
WHERE NOT EXISTS (SELECT 1 FROM studio WHERE id = 2);

INSERT INTO studio (id, name, row_count, col_count, introduction, status)
SELECT 3, '3号厅', 15, 20, '大型演出厅，适合大型歌舞剧', 1
WHERE NOT EXISTS (SELECT 1 FROM studio WHERE id = 3);
