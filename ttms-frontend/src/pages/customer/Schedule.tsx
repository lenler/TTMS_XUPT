// 放映安排列表页

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Row, Col, Typography, Spin, Empty, Tag, DatePicker, Select, Space } from 'antd';
import { ClockCircleOutlined, EnvironmentOutlined } from '@ant-design/icons';
import type { Dayjs } from 'dayjs';
import { getSchedules } from '@/services/customer/schedule';

const { Title, Text } = Typography;

interface ScheduleItem {
  id: number; playId: number; playName: string; playPoster: string;
  playType: string; playDuration: number;
  studioId: number; studioName: string;
  showTime: string; ticketPrice: number; availableSeats: number;
}

/** 放映安排列表页 */
function SchedulePage() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<ScheduleItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [date, setDate] = useState<string>('');
  const [playId, setPlayId] = useState<number | undefined>();
  const navigate = useNavigate();
  const pageSize = 9;

  const fetchData = () => {
    setLoading(true);
    const params: { page: number; pageSize: number; date?: string; playId?: number } = { page, pageSize };
    if (date) params.date = date;
    if (playId) params.playId = playId;
    getSchedules(params)
      .then((res) => { setData(res.data.list); setTotal(res.data.total); })
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchData(); }, [page, date, playId]);

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto' }}>
      <Title level={3} style={{ marginBottom: 16 }}>放映安排</Title>

      <Space style={{ marginBottom: 24 }} wrap>
        <DatePicker
          placeholder="选择日期"
          onChange={(d: Dayjs | null) => { setDate(d ? d.format('YYYY-MM-DD') : ''); setPage(1); }}
        />
        <Select
          placeholder="选择剧目"
          allowClear
          style={{ width: 200 }}
          onChange={(v) => { setPlayId(v); setPage(1); }}
          options={[
            { label: '雷雨', value: 1 },
            { label: '歌剧魅影', value: 2 },
            { label: '红色娘子军', value: 3 },
            { label: '茶馆', value: 4 },
          ]}
        />
      </Space>

      <Spin spinning={loading}>
        {data.length === 0 && !loading && <Empty description="暂无放映安排" />}
        <Row gutter={[16, 16]}>
          {data.map((item) => (
            <Col key={item.id} xs={24} sm={12} md={8}>
              <Card
                hoverable
                onClick={() => navigate(`/seats/${item.id}`)}
                cover={
                  <div style={{ height: 180, background: '#f0f0f0', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Text type="secondary" style={{ fontSize: 40 }}>🎬</Text>
                  </div>
                }
              >
                <Title level={5} style={{ marginBottom: 8 }}>{item.playName}</Title>
                <Tag color="blue">{item.playType}</Tag>
                <Tag>{item.playDuration}分钟</Tag>
                <div style={{ marginTop: 8 }}>
                  <EnvironmentOutlined /> <Text>{item.studioName}</Text>
                </div>
                <div>
                  <ClockCircleOutlined /> <Text>{item.showTime}</Text>
                </div>
                <div>
                  <Text type="secondary">票价：</Text>
                  <Text strong style={{ color: '#ff4d4f' }}>¥{item.ticketPrice.toFixed(2)}</Text>
                </div>
                <div>
                  <Text type="secondary">剩余：</Text>
                  <Text style={{ color: item.availableSeats > 0 ? '#52c41a' : '#ff4d4f' }}>
                    {item.availableSeats} 座
                  </Text>
                </div>
              </Card>
            </Col>
          ))}
        </Row>
        {/* 简化分页 */}
        {total > pageSize && (
          <div style={{ textAlign: 'center', marginTop: 24 }}>
            <Space>
              {page > 1 && <a onClick={() => setPage(page - 1)}>上一页</a>}
              <Text>第 {page} 页 / 共 {Math.ceil(total / pageSize)} 页</Text>
              {page < Math.ceil(total / pageSize) && <a onClick={() => setPage(page + 1)}>下一页</a>}
            </Space>
          </div>
        )}
      </Spin>
    </div>
  );
}

export default SchedulePage;
