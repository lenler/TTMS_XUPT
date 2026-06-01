// 确认订单页：订单详情 + 支付

import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Card, Typography, Button, Spin, Descriptions, Tag, Input, Radio, message, Divider } from 'antd';
import { getOrder, payOrder } from '@/services/customer/order';
import { useCartStore } from '@/stores/cartStore';

const { Title, Text } = Typography;

interface TicketInfo {
  ticketId: number; seatRow: number; seatCol: number;
  playName: string; studioName: string; showTime: string;
  price: number; ticketStatus: string;
}

interface OrderData {
  orderId: number; orderStatus: string; tickets: TicketInfo[];
}

/** 确认订单页 */
function OrderPage() {
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get('orderId');
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [paying, setPaying] = useState(false);
  const [order, setOrder] = useState<OrderData | null>(null);
  const [paymentMethod] = useState('balance');
  const [paymentPassword, setPaymentPassword] = useState('');
  const { clearCart } = useCartStore();

  useEffect(() => {
    if (!orderId) return;
    setLoading(true);
    getOrder(Number(orderId))
      .then((res) => setOrder(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [orderId]);

  /** 支付 */
  const handlePay = async () => {
    if (!paymentPassword) { message.warning('请输入支付密码'); return; }
    if (!orderId) return;
    setPaying(true);
    try {
      const res = await payOrder(Number(orderId), { paymentMethod, paymentPassword });
      clearCart();
      navigate(`/result/${orderId}`, { replace: true, state: res.data });
    } catch {
      // 错误已提示
    } finally {
      setPaying(false);
    }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>;
  if (!order) return <div style={{ textAlign: 'center', padding: 80 }}>订单加载失败</div>;

  const totalPrice = order.tickets.reduce((sum, t) => sum + t.price, 0);

  return (
    <div style={{ maxWidth: 600, margin: '0 auto' }}>
      <Title level={3} style={{ marginBottom: 24 }}>确认订单</Title>

      <Card style={{ marginBottom: 16 }}>
        <Descriptions column={1} size="small">
          <Descriptions.Item label="订单编号">{order.orderId}</Descriptions.Item>
          <Descriptions.Item label="剧目">{order.tickets[0]?.playName || '—'}</Descriptions.Item>
          <Descriptions.Item label="演出厅">{order.tickets[0]?.studioName || '—'}</Descriptions.Item>
          <Descriptions.Item label="演出时间">{order.tickets[0]?.showTime || '—'}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="已选座位" style={{ marginBottom: 16 }}>
        {order.tickets.map((t) => (
          <Tag key={t.ticketId} style={{ marginBottom: 4 }}>{t.seatRow}排{t.seatCol}座 ¥{t.price.toFixed(2)}</Tag>
        ))}
        <Divider />
        <div style={{ textAlign: 'right' }}>
          <Text style={{ fontSize: 18 }}>合计：</Text>
          <Text strong style={{ fontSize: 24, color: '#ff4d4f' }}>¥{totalPrice.toFixed(2)}</Text>
        </div>
      </Card>

      <Card title="支付方式" style={{ marginBottom: 16 }}>
        <Radio.Group value={paymentMethod}>
          <Radio value="balance">余额支付</Radio>
        </Radio.Group>
        <div style={{ marginTop: 12 }}>
          <Text type="secondary">支付密码（Mock 默认 123456）</Text>
          <Input.Password
            placeholder="请输入支付密码"
            value={paymentPassword}
            onChange={(e) => setPaymentPassword(e.target.value)}
            style={{ marginTop: 4 }}
          />
        </div>
      </Card>

      <Button type="primary" size="large" block loading={paying} onClick={handlePay}>
        确认支付 ¥{totalPrice.toFixed(2)}
      </Button>
    </div>
  );
}

export default OrderPage;
