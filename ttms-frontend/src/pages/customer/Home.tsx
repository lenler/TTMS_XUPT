// 观众端首页

import { useEffect, useState } from 'react';
import { Button, Card, Col, Row, Typography } from 'antd';
import { Link } from 'react-router-dom';
import { getHomeData } from '@/services/customer/home';
import type { Play, Schedule } from '@/types/models';

const { Title, Paragraph } = Typography;

function HomePage() {
  const [plays, setPlays] = useState<Play[]>([]);
  const [schedules, setSchedules] = useState<Schedule[]>([]);

  useEffect(() => {
    getHomeData().then((data) => {
      setPlays(data.plays);
      setSchedules(data.schedules);
    }).catch(() => {});
  }, []);

  return (
    <div>
      <Title level={2}>汉唐剧院</Title>
      <Paragraph>在线浏览剧目、选择场次并完成订票。</Paragraph>
      <Row gutter={[16, 16]}>
        {plays.map((play) => (
          <Col xs={24} md={8} key={play.id}>
            <Card title={play.name} extra={<Link to="/schedule">排片</Link>}>
              <Paragraph ellipsis={{ rows: 3 }}>{play.introduction || '暂无简介'}</Paragraph>
              <div>类型：{play.typeName || play.typeId || '-'}</div>
              <div>时长：{play.duration || 0} 分钟</div>
            </Card>
          </Col>
        ))}
      </Row>
      <Card title="近期排片" style={{ marginTop: 16 }} extra={<Button type="link"><Link to="/schedule">全部排片</Link></Button>}>
        {schedules.map((item) => (
          <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid #eee' }}>
            <span>{item.playName} / {item.studioName} / {item.showTime}</span>
            <Link to={`/seats/${item.id}`}>选座</Link>
          </div>
        ))}
      </Card>
    </div>
  );
}

export default HomePage;
