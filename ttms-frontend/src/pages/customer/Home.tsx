// 观众端首页：热卖剧目 + 近期演出

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Row, Col, Typography, Spin, Empty, Tag, Button } from 'antd';
import { FireOutlined, ClockCircleOutlined, EnvironmentOutlined } from '@ant-design/icons';
import { getHome } from '@/services/customer/home';

const { Title, Text } = Typography;

interface HotPlay {
  id: number; name: string; poster: string; typeName: string;
  duration: number; basePrice: number; soldCount: number;
}

interface UpcomingSchedule {
  id: number; playId: number; playName: string; studioName: string;
  showTime: string; ticketPrice: number; availableSeats: number;
}

/** 观众端首页 */
function HomePage() {
  const [loading, setLoading] = useState(false);
  const [hotPlays, setHotPlays] = useState<HotPlay[]>([]);
  const [upcoming, setUpcoming] = useState<UpcomingSchedule[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    getHome()
      .then((res) => {
        setHotPlays(res.data.hotPlays);
        setUpcoming(res.data.upcomingSchedules);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  return (
    <Spin spinning={loading}>
      <div style={{ maxWidth: 1200, margin: '0 auto' }}>
        {/* 热卖剧目 */}
        <Title level={3} style={{ marginBottom: 16 }}>
          <FireOutlined style={{ color: '#ff4d4f', marginRight: 8 }} />热卖剧目
        </Title>
        {hotPlays.length === 0 && !loading && <Empty description="暂无剧目" />}
        <Row gutter={[16, 16]} style={{ marginBottom: 40 }}>
          {hotPlays.map((play) => (
            <Col key={play.id} xs={24} sm={12} md={6}>
              <Card
                hoverable
                cover={
                  <div style={{ height: 200, background: '#f0f0f0', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Text type="secondary" style={{ fontSize: 48 }}>🎭</Text>
                  </div>
                }
                onClick={() => navigate('/schedule')}
              >
                <Card.Meta
                  title={play.name}
                  description={
                    <>
                      <Tag color="blue">{play.typeName}</Tag>
                      <Tag>{play.duration}分钟</Tag>
                      <div style={{ marginTop: 8 }}>
                        <Text type="secondary">基础票价：</Text>
                        <Text strong>¥{play.basePrice.toFixed(2)}</Text>
                      </div>
                      <div>
                        <Text type="secondary">已售：</Text>
                        <Text style={{ color: '#ff4d4f' }}>{play.soldCount} 张</Text>
                      </div>
                    </>
                  }
                />
              </Card>
            </Col>
          ))}
        </Row>

        {/* 近期演出 */}
        <Title level={3} style={{ marginBottom: 16 }}>
          <ClockCircleOutlined style={{ marginRight: 8 }} />近期演出
        </Title>
        {upcoming.length === 0 && !loading && <Empty description="暂无近期演出" />}
        <Row gutter={[16, 16]}>
          {upcoming.map((item) => (
            <Col key={item.id} xs={24} sm={12} md={8}>
              <Card hoverable onClick={() => navigate(`/seats/${item.id}`)}>
                <Title level={5} style={{ marginBottom: 8 }}>{item.playName}</Title>
                <div style={{ marginBottom: 4 }}>
                  <EnvironmentOutlined /> <Text>{item.studioName}</Text>
                </div>
                <div style={{ marginBottom: 4 }}>
                  <ClockCircleOutlined /> <Text>{item.showTime}</Text>
                </div>
                <div style={{ marginBottom: 4 }}>
                  <Text type="secondary">票价：</Text>
                  <Text strong style={{ color: '#ff4d4f' }}>¥{item.ticketPrice.toFixed(2)}</Text>
                </div>
                <div>
                  <Text type="secondary">剩余座位：</Text>
                  <Text style={{ color: item.availableSeats > 0 ? '#52c41a' : '#ff4d4f' }}>
                    {item.availableSeats} 座
                  </Text>
                </div>
                <Button type="primary" style={{ marginTop: 12 }} block>
                  立即购票
                </Button>
              </Card>
            </Col>
          ))}
        </Row>
      </div>
    </Spin>
  );
}

export default HomePage;
