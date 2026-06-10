// 支付结果页 —— 水墨留白 · 东方极简
// 成功/失败结果 + 票务信息卡片
// 支持两种数据来源：location.state（立即展示） 或 API 回退（页面刷新/直接访问）

import { useEffect, useState } from 'react';
import { useParams, useLocation, Link } from 'react-router-dom';
import { Spin } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { getOrder } from '@/services/customer/order';

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
  const locationData = (location.state as PayResultData) || null;
  const [data, setData] = useState<PayResultData | null>(locationData);
  const [loading, setLoading] = useState(!locationData);

  // 如果 location.state 为空，尝试通过 API 获取订单数据
  useEffect(() => {
    if (locationData || !orderId) return;
    setLoading(true);
    getOrder(Number(orderId))
      .then((res) => {
        setData({
          orderId: res.data.orderId,
          orderStatus: res.data.orderStatus,
          tickets: res.data.tickets,
        });
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [orderId, locationData]);

  // 加载中
  if (loading) {
    return (
      <div className="max-w-md mx-auto text-center py-20">
        <Spin size="large" />
        <p className="text-stone mt-4">正在获取订单信息...</p>
      </div>
    );
  }

  // 无数据
  if (!data) {
    return (
      <div className="max-w-md mx-auto text-center py-20">
        <ExclamationCircleOutlined className="text-5xl text-gold mb-4" />
        <h1 className="font-serif text-2xl text-ink mb-2">暂无订单信息</h1>
        <p className="text-stone mb-6">无法获取订单结果，请查看我的订单</p>
        <div className="flex justify-center gap-3">
          {orderId && (
            <Link
              to={`/order?orderId=${orderId}`}
              className="inline-block border border-warm text-stone px-6 py-2 rounded-sm
                         hover:border-ink hover:text-ink transition-soft"
            >
              返回订单页
            </Link>
          )}
          <Link
            to="/orders"
            className="inline-block border border-ink text-ink px-6 py-2 rounded-sm
                       hover:bg-ink hover:text-white transition-soft"
          >
            查看我的订单
          </Link>
        </div>
      </div>
    );
  }

  const isSuccess = data.orderStatus === 'PAID' || data.orderStatus === 'paid';

  return (
    <div className="max-w-lg mx-auto">
      {/* 结果图标 + 标题 */}
      <div className="text-center py-12">
        {isSuccess ? (
          <CheckCircleOutlined className="text-6xl text-[#52c41a] mb-4" />
        ) : (
          <CloseCircleOutlined className="text-6xl text-[#ff4d4f] mb-4" />
        )}
        <h1 className="font-serif text-3xl text-ink mb-2 tracking-wide">
          {isSuccess ? '购票成功' : '支付失败'}
        </h1>
        <p className="text-stone">
          {isSuccess ? `订单号：${orderId}` : '请重新尝试支付'}
        </p>
      </div>

      {/* 支付失败：导向订单页重新支付 */}
      {!isSuccess && (
        <div className="text-center mb-8">
          <Link
            to={`/order?orderId=${data.orderId}`}
            className="inline-block bg-gold text-white px-8 py-2.5 rounded-sm font-medium
                       hover:bg-[#B8944F] transition-soft"
          >
            返回订单页重新支付
          </Link>
        </div>
      )}

      {/* 票务信息 */}
      {isSuccess && (
        <div className="border border-warm bg-cream rounded-sm p-6 mb-8">
          <h3 className="font-serif text-lg text-ink mb-4">票务信息</h3>
          <div className="space-y-2">
            {data.tickets.map((t) => (
              <div key={t.ticketId}
                className="border border-warm bg-white rounded-sm p-4"
              >
                <div className="grid grid-cols-2 gap-x-4 gap-y-1.5 text-sm">
                  <div className="flex justify-between col-span-2 pb-2 border-b border-warm mb-1">
                    <span className="text-stone">票号</span>
                    <span className="text-ink font-medium">{t.ticketId}</span>
                    <span className="text-stone">状态</span>
                    <span className="text-[#52c41a]">已出票</span>
                  </div>
                  <span className="text-stone">座位</span>
                  <span className="text-ink text-right">{t.seatRow}排 {t.seatCol}座</span>
                  <span className="text-stone">价格</span>
                  <span className="text-ink font-medium text-right">¥{t.price.toFixed(2)}</span>
                  <span className="text-stone">剧目</span>
                  <span className="text-ink text-right">{t.playName}</span>
                  <span className="text-stone">演出厅</span>
                  <span className="text-ink text-right">{t.studioName}</span>
                  <span className="text-stone col-span-2 pt-1 border-t border-warm">演出时间</span>
                  <span className="text-ink text-right col-span-2">{t.showTime}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 操作按钮 */}
      <div className="flex justify-center gap-4">
        <Link
          to="/"
          className="border border-ink text-ink px-6 py-2 rounded-sm hover:bg-ink hover:text-white transition-soft"
        >
          返回首页
        </Link>
        <Link
          to="/orders"
          className="bg-ink text-white px-6 py-2 rounded-sm hover:bg-gold transition-soft"
        >
          我的订单
        </Link>
      </div>
    </div>
  );
}

export default ResultPage;
