// 确认订单页 —— 水墨留白 · 东方极简
// 订单信息分区卡片 + 支付密码 + 暗金 CTA
// 支持三种订单状态：待支付/已支付/已退票

import { useEffect, useState, useMemo } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Spin, Input, Radio, message, Divider, Result, Button } from 'antd';
import { CheckCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';
import { getOrder, payOrder } from '@/services/customer/order';
import { getProfile } from '@/services/customer/auth';
import { useCartStore } from '@/stores/cartStore';

interface TicketInfo {
  ticketId: number; seatRow: number; seatCol: number;
  playName: string; studioName: string; showTime: string;
  price: number; ticketStatus: string;
}

interface OrderData {
  orderId: number; orderStatus: string; tickets: TicketInfo[];
}

/** 订单状态中文映射 */
const STATUS_LABEL: Record<string, string> = {
  PENDING_PAYMENT: '待支付',
  PAID: '已支付',
  REFUNDED: '已退票',
  CANCELLED: '已取消',
};

/** 确认订单页 */
function OrderPage() {
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get('orderId');
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [paying, setPaying] = useState(false);
  const [order, setOrder] = useState<OrderData | null>(null);
  const [balance, setBalance] = useState(0);
  const [paymentMethod] = useState('balance');
  const [paymentPassword, setPaymentPassword] = useState('');
  const { clearCart } = useCartStore();

  useEffect(() => {
    if (!orderId) return;
    setLoading(true);
    Promise.all([getOrder(Number(orderId)), getProfile()])
      .then(([orderRes, profileRes]) => {
        setOrder(orderRes.data);
        setBalance(profileRes.data.balance);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [orderId]);

  /** 是否可支付 */
  const canPay = useMemo(() => {
    return order?.orderStatus === 'PENDING_PAYMENT';
  }, [order?.orderStatus]);

  /** 支付 */
  const handlePay = async () => {
    const totalPrice = order?.tickets.reduce((sum, t) => sum + t.price, 0) ?? 0;
    if (balance < totalPrice) {
      message.warning('余额不足，请先充值');
      return;
    }
    if (!paymentPassword) { message.warning('请输入支付密码'); return; }
    if (!orderId) return;
    setPaying(true);
    try {
      const res = await payOrder(Number(orderId), { paymentMethod, paymentPassword });
      if (typeof res.data.balance === 'number') {
        setBalance(res.data.balance);
        const stored = localStorage.getItem('customerInfo');
        if (stored) {
          localStorage.setItem('customerInfo', JSON.stringify({ ...JSON.parse(stored), balance: res.data.balance }));
        }
      }
      // 更新本地订单状态
      setOrder((prev) => prev ? { ...prev, orderStatus: 'PAID' } : prev);
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
        <Button type="link" onClick={() => navigate('/')}>返回首页</Button>
      </div>
    </div>
  );

  const totalPrice = order.tickets.reduce((sum, t) => sum + t.price, 0);
  const statusLabel = STATUS_LABEL[order.orderStatus] || order.orderStatus;

  return (
    <div className="max-w-xl mx-auto">
      <h1 className="font-serif text-2xl text-ink tracking-wide mb-8 text-center">确认订单</h1>

      {/* 订单状态标签 */}
      <div className="text-center mb-4">
        <span className={`inline-flex items-center gap-1.5 px-4 py-1 rounded-sm text-sm font-medium ${
          order.orderStatus === 'PAID' ? 'bg-green-50 text-green-700 border border-green-200' :
          order.orderStatus === 'REFUNDED' ? 'bg-stone-50 text-stone-600 border border-warm' :
          'bg-cream text-gold border border-gold'
        }`}>
          {order.orderStatus === 'PAID' ? <CheckCircleOutlined /> : <ClockCircleOutlined />}
          {statusLabel}
        </span>
      </div>

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

      {/* 已支付 / 已退票 / 已取消：展示结果，不显示支付表单 */}
      {!canPay && (
        <div className="text-center mt-8">
          {order.orderStatus === 'PAID' && (
            <Result
              status="success"
              title="支付成功"
              subTitle={`订单 #${order.orderId} 已支付，共 ¥${totalPrice.toFixed(2)}`}
            />
          )}
          {order.orderStatus === 'REFUNDED' && (
            <Result
              status="info"
              title="已退票"
              subTitle={`订单 #${order.orderId} 已退票，金额已退回余额`}
            />
          )}
          <div className="flex justify-center gap-4">
            <button
              onClick={() => navigate('/')}
              className="border border-warm text-stone px-6 py-2 rounded-sm hover:border-ink hover:text-ink transition-soft cursor-pointer"
            >
              返回首页
            </button>
            <button
              onClick={() => navigate('/orders')}
              className="bg-gold text-white px-6 py-2 rounded-sm hover:bg-[#B8944F] transition-soft cursor-pointer"
            >
              我的订单
            </button>
          </div>
        </div>
      )}

      {/* 待支付：展示支付表单 */}
      {canPay && (
        <>
          {/* 支付方式卡片 */}
          <div className="border border-warm bg-cream rounded-sm p-6 mb-6">
            <h3 className="font-serif text-lg text-ink mb-4">支付方式</h3>
            <Radio.Group value={paymentMethod}>
              <Radio value="balance" className="text-ink">余额支付</Radio>
            </Radio.Group>
            <div className="mt-3 text-sm">
              <span className="text-stone">当前余额：</span>
              <span className={balance >= totalPrice ? 'text-ink font-medium' : 'text-[#ff4d4f] font-medium'}>
                ¥{balance.toFixed(2)}
              </span>
              {balance < totalPrice && (
                <span className="text-[#ff4d4f] ml-3">余额不足，请先充值</span>
              )}
            </div>
            <div className="mt-4">
              <p className="text-light-ink text-sm mb-2">支付密码（默认 123456）</p>
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
            disabled={paying || balance < totalPrice}
            className="w-full bg-gold text-white py-3 rounded-sm font-medium text-lg
                       hover:bg-[#B8944F] transition-soft cursor-pointer
                       disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {paying ? '支付中...' : `确认支付 ¥${totalPrice.toFixed(2)}`}
          </button>
        </>
      )}
    </div>
  );
}

export default OrderPage;
