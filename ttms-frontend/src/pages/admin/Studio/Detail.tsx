// 演出厅详情页

import { Button, Card, Descriptions, Empty, Skeleton, Space, Tag } from 'antd';
import { ArrowLeftOutlined, EditOutlined } from '@ant-design/icons';
import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getStudioById } from '@/services/admin/studio';
import type { Studio } from '@/types/models';

/** 渲染演出厅详情信息，供后续座位管理功能扩展 */
function StudioDetailPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(true);
  const [studio, setStudio] = useState<Studio | null>(null);

  useEffect(() => {
    /** 根据路由编号加载演出厅详情 */
    const loadStudio = async () => {
      if (!id) {
        setLoading(false);
        return;
      }
      try {
        const res = await getStudioById(Number(id));
        setStudio(res.data);
      } finally {
        setLoading(false);
      }
    };

    void loadStudio();
  }, [id]);

  if (loading) {
    return (
      <Card title="演出厅详情">
        <Skeleton active />
      </Card>
    );
  }

  if (!studio) {
    return (
      <Card title="演出厅详情">
        <Empty description="未找到演出厅信息" />
      </Card>
    );
  }

  return (
    <Card
      title="演出厅详情"
      extra={
        <Space>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/admin/studio')}>
            返回
          </Button>
          <Button type="primary" icon={<EditOutlined />} onClick={() => navigate('/admin/studio')}>
            返回列表编辑
          </Button>
        </Space>
      }
    >
      <Descriptions
        bordered
        column={2}
        items={[
          { key: 'name', label: '演出厅名称', children: studio.name },
          {
            key: 'status',
            label: '状态',
            children: studio.status === 1 ? <Tag color="green">启用</Tag> : <Tag color="red">停用</Tag>,
          },
          { key: 'rowCount', label: '座位行数', children: studio.rowCount },
          { key: 'colCount', label: '座位列数', children: studio.colCount },
          { key: 'seatTotal', label: '座位总数', children: studio.rowCount * studio.colCount },
          { key: 'introduction', label: '简介', children: studio.introduction },
        ]}
      />
    </Card>
  );
}

export default StudioDetailPage;
