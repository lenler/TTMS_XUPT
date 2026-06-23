// 管理端工作台首页：关键经营指标概览 + 快捷功能入口卡片

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Card, Statistic, Row, Col, Spin, Typography, Tag, message,
} from 'antd';
import {
  DollarOutlined, ShoppingCartOutlined, PercentageOutlined,
  BankOutlined, ScheduleOutlined, TeamOutlined, SafetyOutlined,
  AuditOutlined, PlayCircleOutlined, BarChartOutlined,
} from '@ant-design/icons';
import { getFinanceOverview, type FinanceOverview } from '@/services/admin/finance';

const { Title, Text } = Typography;

/** 快捷入口卡片配置 */
const quickActions = [
  { title: '演出厅管理', icon: <BankOutlined />, path: '/admin/studio', color: '#1677ff' },
  { title: '剧目管理', icon: <PlayCircleOutlined />, path: '/admin/play', color: '#52c41a' },
  { title: '演出计划', icon: <ScheduleOutlined />, path: '/admin/schedule', color: '#fa8c16' },
  { title: '线下售票', icon: <ShoppingCartOutlined />, path: '/admin/sale', color: '#eb2f96' },
  { title: '验票管理', icon: <AuditOutlined />, path: '/admin/check', color: '#722ed1' },
  { title: '员工管理', icon: <TeamOutlined />, path: '/admin/employee', color: '#13c2c2' },
  { title: '角色权限', icon: <SafetyOutlined />, path: '/admin/role', color: '#f5222d' },
  { title: '财务统计', icon: <BarChartOutlined />, path: '/admin/finance', color: '#2f54eb' },
];

/** 管理端工作台页 */
function DashboardPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [overview, setOverview] = useState<FinanceOverview | null>(null);
  const topPlay = overview?.topPlay;
  const hasTopPlay = !!topPlay?.playName && typeof topPlay.sales === 'number';

  useEffect(() => {
    setLoading(true);
    getFinanceOverview()
      .then((res) => setOverview(res.data))
      .catch(() => { message.error('加载工作台数据失败，请稍后重试'); })
      .finally(() => setLoading(false));
  }, []);

  return (
    <Spin spinning={loading}>
      <div>
        <Title level={3} style={{ marginBottom: 8 }}>工作台</Title>
        <Text type="secondary">欢迎使用奥斯卡剧院票务管理系统，以下是经营概览</Text>

        {/* 关键指标卡片 */}
        <Row gutter={[16, 16]} style={{ marginTop: 24, marginBottom: 24 }}>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="销售总额"
                value={overview?.totalSales ?? 0}
                precision={2}
                prefix="¥"
                suffix={<DollarOutlined />}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="订单数"
                value={overview?.totalOrders ?? 0}
                prefix={<ShoppingCartOutlined />}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="售票张数"
                value={overview?.totalTickets ?? 0}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="平均上座率"
                value={overview ? (overview.avgOccupancy * 100).toFixed(1) : 0}
                suffix="%"
                prefix={<PercentageOutlined />}
              />
            </Card>
          </Col>
        </Row>

        {/* 热卖剧目 */}
        {hasTopPlay && (
          <Card style={{ marginBottom: 24 }} size="small">
            <Text type="secondary">热卖剧目：</Text>
            <Text strong style={{ fontSize: 16 }}>{topPlay.playName}</Text>
            <Tag color="red" style={{ marginLeft: 12 }}>¥{topPlay.sales.toFixed(2)}</Tag>
          </Card>
        )}

        {/* 快捷入口 */}
        <Title level={4} style={{ marginBottom: 16 }}>快捷入口</Title>
        <Row gutter={[16, 16]}>
          {quickActions.map((action) => (
            <Col key={action.path} xs={12} sm={8} md={6} lg={6}>
              <Card
                hoverable
                onClick={() => navigate(action.path)}
                style={{ textAlign: 'center', borderTop: `3px solid ${action.color}` }}
              >
                <div style={{ fontSize: 32, color: action.color, marginBottom: 8 }}>
                  {action.icon}
                </div>
                <Text strong>{action.title}</Text>
              </Card>
            </Col>
          ))}
        </Row>
      </div>
    </Spin>
  );
}

export default DashboardPage;
