// 座位图编辑器：网格点击切换座位可用/不可用

import { useState, useEffect, useCallback } from 'react';
import { Button, Space, message } from 'antd';

interface SeatEditorProps {
  studioId: number;
}

/**
 * 座位布局字符串：a = 可用，_ = 不可用（过道/损坏）
 * 示例：["aaaa_aaa", "aaaa_aaa", ...]
 */
type SeatLayout = string[];

/** 渲染演出厅座位图编辑器，用于切换座位可用状态 */
function SeatEditor({ studioId }: SeatEditorProps) {
  const [layout, setLayout] = useState<SeatLayout>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(true);

  /** 加载当前座位布局（Mock 阶段使用默认布局） */
  useEffect(() => {
    // Mock：使用默认布局
    const defaultLayout: SeatLayout = [
      'aaaaa_aaaaa',
      'aaaaa_aaaaa',
      'aaaaa_aaaaa',
      'aaaaa_aaaaa',
      'aaaaa_aaaaa',
      'aaaaa_aaaaa',
      'aaaaa_aaaaa',
      'aa___aa_aa',
    ];
    const timer = window.setTimeout(() => {
      setLayout(defaultLayout);
      setLoading(false);
    }, 200);

    return () => {
      window.clearTimeout(timer);
    };
  }, [studioId]);

  /** 点击座位格子，切换 a/_ */
  const handleCellClick = useCallback(
    (row: number, col: number) => {
      setLayout((prev) => {
        const newLayout = [...prev];
        const rowStr = newLayout[row];
        const chars = rowStr.split('');
        chars[col] = chars[col] === 'a' ? '_' : 'a';
        newLayout[row] = chars.join('');
        return newLayout;
      });
      setSaved(false);
    },
    []
  );

  /** 保存座位布局 */
  const handleSave = async () => {
    setSaving(true);
    try {
      // TODO：后续对接 PUT /admin/api/studios/:id/seats
      await new Promise((resolve) => setTimeout(resolve, 300));
      message.success('座位布局保存成功');
      setSaved(true);
    } catch {
      message.error('保存失败');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div style={{ textAlign: 'center', padding: 24 }}>加载座位布局中...</div>;
  }

  return (
    <div>
      <div style={{ marginBottom: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Space>
          <span style={{ display: 'inline-block', width: 12, height: 12, background: '#52c41a', borderRadius: 2 }} />
          <span style={{ fontSize: 12, color: '#666' }}>可选座位</span>
          <span style={{ display: 'inline-block', width: 12, height: 12, background: '#d9d9d9', borderRadius: 2, marginLeft: 8 }} />
          <span style={{ fontSize: 12, color: '#666' }}>过道/不可用</span>
        </Space>
        <Button type="primary" size="small" loading={saving} disabled={saved} onClick={handleSave}>
          保存座位布局
        </Button>
      </div>

      {/* 座位网格 */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: `repeat(${layout[0]?.length || 1}, 32px)`,
          gap: 4,
          justifyContent: 'center',
        }}
      >
        {layout.map((rowStr, rowIdx) =>
          rowStr.split('').map((char, colIdx) => (
            <div
              key={`${rowIdx}-${colIdx}`}
              onClick={() => handleCellClick(rowIdx, colIdx)}
              style={{
                width: 32,
                height: 32,
                background: char === 'a' ? '#52c41a' : '#d9d9d9',
                borderRadius: 4,
                cursor: 'pointer',
                transition: 'background 0.2s',
              }}
              title={`${rowIdx + 1}排${colIdx + 1}座 — ${char === 'a' ? '可用' : '不可用'}`}
            />
          ))
        )}
      </div>
    </div>
  );
}

export default SeatEditor;
