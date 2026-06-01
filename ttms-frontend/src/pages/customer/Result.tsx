// 支付结果页

import { useParams, useLocation, Link } from 'react-router-dom';
import { Card, Button, Result, Descriptions, Tag, Space } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

interface TicketInfo {
  ticketId: number; seatRow: number; seatCol: number;
  playName: string; studioName: string; showTime: string;
  price: number; ticketStatus: string;
}

interface PayResultData {
  orderId: number; orderStatus: string; tickets: TicketInfo[];
}

/** 支付结果页 */
function ResultPage() {
  const { orderId } = useParams<{ orderId: string }>();
  const location = useLocation();
  const data = (location.state as PayResultData) || null;

  if (!data) {
    return (
      <div style={{ maxWidth: 500, margin: '80px auto', textAlign: 'center' }}>
        <Result
          status="warning"
          title="暂无订单信息"
          extra={<Link to="/orders"><Button type="primary">查看我的订单</Button></Link>}
        />
      </div>
    );
  }

  const isSuccess = data.orderStatus === 'paid';

  return (
    <div style={{ maxWidth: 600, margin: '40px auto' }}>
      <Result
        status={isSuccess ? 'success' : 'error'}
        icon={isSuccess ? <CheckCircleOutlined /> : <CloseCircleOutlined />}
        title={isSuccess ? '支付成功' : '支付失败'}
        subTitle={isSuccess ? `订单号：${orderId}` : '请重新尝试支付'}
      />

      {isSuccess && (
        <Card title="票务信息" style={{ marginBottom: 16 }}>
          {data.tickets.map((t) => (
            <Card key={t.ticketId} size="small" style={{ marginBottom: 8 }}>
              <Descriptions column={2} size="small">
                <Descriptions.Item label="票号">{t.ticketId}</Descriptions.Item>
                <Descriptions.Item label="状态">
                  <Tag color="green">已出票</Tag>
                </Descriptions.Item>
                <Descriptions.Item label="座位">{t.seatRow}排 {t.seatCol}座</Descriptions.Item>
                <Descriptions.Item label="价格">¥{t.price.toFixed(2)}</Descriptions.Item>
                <Descriptions.Item label="剧目">{t.playName}</Descriptions.Item>
                <Descriptions.Item label="演出厅">{t.studioName}</Descriptions.Item>
                <Descriptions.Item label="演出时间" span={2}>{t.showTime}</Descriptions.Item>
              </Descriptions>
            </Card>
          ))}
        </Card>
      )}

      <Space style={{ display: 'flex', justifyContent: 'center' }}>
        <Link to="/"><Button>返回首页</Button></Link>
        <Link to="/orders"><Button type="primary">我的订单</Button></Link>
      </Space>
    </div>
  );
}

export default ResultPage;
