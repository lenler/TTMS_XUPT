// 票房榜单页 —— 水墨留白 · 东方极简
// 排名列表 + 前三名暗金标识

import { useEffect, useState } from 'react';
import { Table, Typography, Spin, Empty } from 'antd';
import { TrophyOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getBoard } from '@/services/customer/board';

const { Text } = Typography;

interface BoardItem {
  rank: number; playId: number; playName: string;
  poster: string; sales: number;
}

/** 排名颜色：前三名暗金渐变色 */
const rankConfig: Record<number, { color: string; bg: string }> = {
  1: { color: '#C9A96E', bg: '#FBF6EE' },
  2: { color: '#B8944F', bg: '#FBF6EE' },
  3: { color: '#A8813A', bg: '#FAF5EC' },
};

/** 票房榜单页 */
function BoardPage() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<BoardItem[]>([]);

  useEffect(() => {
    setLoading(true);
    getBoard({ type: 'sales', limit: 10 })
      .then((res) => setData(res.data.list))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const columns: ColumnsType<BoardItem> = [
    {
      title: '排名', dataIndex: 'rank', key: 'rank', width: 80,
      render: (rank: number) => {
        if (rank <= 3) {
          return (
            <span className="inline-flex items-center justify-center w-8 h-8">
              <TrophyOutlined className="text-xl" style={{ color: rankConfig[rank].color }} />
            </span>
          );
        }
        return <Text className="!text-stone font-medium">{rank}</Text>;
      },
    },
    {
      title: '剧目', dataIndex: 'playName', key: 'playName',
      render: (name: string, record: BoardItem) => (
        <span className={record.rank <= 3 ? 'font-medium' : ''}>{name}</span>
      ),
    },
    {
      title: '销售额', dataIndex: 'sales', key: 'sales',
      render: (v: number) => (
        <Text className="!text-ink font-medium">¥{v.toFixed(2)}</Text>
      ),
      sorter: (a: BoardItem, b: BoardItem) => a.sales - b.sales,
    },
  ];

  /** 行样式：前三名暗金背景 */
  const rowClassName = (record: BoardItem) => {
    if (record.rank <= 3) return 'board-top-row';
    return '';
  };

  return (
    <div>
      <h1 className="font-serif text-2xl text-ink tracking-wide mb-6 flex items-center gap-2">
        <TrophyOutlined className="text-gold" />
        票房榜单
      </h1>
      <Spin spinning={loading}>
        {data.length === 0 && !loading ? (
          <Empty description="暂无数据" />
        ) : (
          <Table
            rowKey="playId"
            columns={columns}
            dataSource={data}
            pagination={false}
            rowClassName={rowClassName}
          />
        )}
      </Spin>
    </div>
  );
}

export default BoardPage;
