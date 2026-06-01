// 票房榜单页

import { useEffect, useState } from 'react';
import { Table, Typography, Spin, Empty } from 'antd';
import { TrophyOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getBoard } from '@/services/customer/board';

const { Title, Text } = Typography;

interface BoardItem {
  rank: number; playId: number; playName: string;
  poster: string; sales: number;
}

/** 排名颜色 */
const rankColors: Record<number, string> = { 1: '#ff4d4f', 2: '#fa8c16', 3: '#fadb14' };

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
          return <TrophyOutlined style={{ color: rankColors[rank], fontSize: 20 }} />;
        }
        return <Text strong>{rank}</Text>;
      },
    },
    { title: '剧目', dataIndex: 'playName', key: 'playName' },
    {
      title: '销售额', dataIndex: 'sales', key: 'sales',
      render: (v: number) => <Text strong style={{ color: '#ff4d4f' }}>¥{v.toFixed(2)}</Text>,
      sorter: (a, b) => a.sales - b.sales,
    },
  ];

  return (
    <div style={{ maxWidth: 800, margin: '0 auto' }}>
      <Title level={3} style={{ marginBottom: 24 }}>
        <TrophyOutlined style={{ color: '#fa8c16', marginRight: 8 }} />票房榜单
      </Title>
      <Spin spinning={loading}>
        {data.length === 0 && !loading ? (
          <Empty description="暂无数据" />
        ) : (
          <Table rowKey="playId" columns={columns} dataSource={data} pagination={false} />
        )}
      </Spin>
    </div>
  );
}

export default BoardPage;
