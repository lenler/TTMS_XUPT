// 确认订单页 —— 水墨留白 · 东方极简
// 订单信息分区卡片 + 支付密码 + 暗金 CTA

import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Spin, Input, Radio, message, Divider } from 'antd';
import { getOrder, payOrder } from '@/services/customer/order';
import { useCartStore } from '@/stores/cartStore';

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

  if (loading) return <div className="text-center py-20"><Spin size="large" /></div>;
  if (!order) return (
    <div className="text-center py-20">
      <div className="border border-warm rounded-sm p-10 max-w-md mx-auto bg-cream">
        <p className="text-stone">订单加载失败</p>
      </div>
    </div>
  );

  const totalPrice = order.tickets.reduce((sum, t) => sum + t.price, 0);

  return (
    <div className="max-w-xl mx-auto">
      <h1 className="font-serif text-2xl text-ink tracking-wide mb-8 text-center">确认订单</h1>

      {/* 订单信息卡片 */}
      <div className="border border-warm bg-cream rounded-sm p-6 mb-4">
        <h3 className="font-serif text-lg text-ink mb-4">订单信息</h3>
        <div className="space-y-2.5 text-sm">
          <div className="flex justify-between">
            <span className="text-stone">订单编号</span>
            <span className="text-ink font-medium">{order.orderId}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-stone">剧目</span>
            <span className="text-ink">{order.tickets[0]?.playName || '—'}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-stone">演出厅</span>
            <span className="text-ink">{order.tickets[0]?.studioName || '—'}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-stone">演出时间</span>
            <span className="text-ink">{order.tickets[0]?.showTime || '—'}</span>
          </div>
        </div>
      </div>

      {/* 座位信息卡片 */}
      <div className="border border-warm bg-cream rounded-sm p-6 mb-4">
        <h3 className="font-serif text-lg text-ink mb-4">已选座位</h3>
        <div className="flex flex-wrap gap-2 mb-4">
          {order.tickets.map((t) => (
            <span key={t.ticketId}
              className="inline-block border border-warm rounded-sm px-3 py-1 text-sm text-ink"
            >
              {t.seatRow}排{t.seatCol}座 ¥{t.price.toFixed(2)}
            </span>
          ))}
        </div>
        <Divider className="!my-3 !border-warm" />
        <div className="flex justify-end items-baseline">
          <span className="text-stone text-sm mr-2">合计</span>
          <span className="text-2xl text-ink font-serif">¥{totalPrice.toFixed(2)}</span>
        </div>
      </div>

      {/* 支付方式卡片 */}
      <div className="border border-warm bg-cream rounded-sm p-6 mb-6">
        <h3 className="font-serif text-lg text-ink mb-4">支付方式</h3>
        <Radio.Group value={paymentMethod}>
          <Radio value="balance" className="text-ink">余额支付</Radio>
        </Radio.Group>
        <div className="mt-4">
          <p className="text-light-ink text-sm mb-2">支付密码（Mock 默认 123456）</p>
          <Input.Password
            placeholder="请输入支付密码"
            value={paymentPassword}
            onChange={(e) => setPaymentPassword(e.target.value)}
            className="max-w-xs"
          />
        </div>
      </div>

      {/* 支付按钮 */}
      <button
        onClick={handlePay}
        disabled={paying}
        className="w-full bg-gold text-white py-3 rounded-sm font-medium text-lg
                   hover:bg-[#B8944F] transition-soft cursor-pointer
                   disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {paying ? '支付中...' : `确认支付 ¥${totalPrice.toFixed(2)}`}
      </button>
    </div>
  );
}

export default OrderPage;
