// 选座订票页 —— 水墨留白 · 东方极简
// 座位图（暖灰空心 / 墨色选中 / 浅墨已售）+ 锁座倒计时

import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Spin, Empty, message } from 'antd';
import { ClockCircleOutlined } from '@ant-design/icons';
import { getScheduleById } from '@/services/customer/schedule';
import { lockSeats, createOrder } from '@/services/customer/order';
import { useCartStore } from '@/stores/cartStore';

const { Text } = Typography;

interface SeatData {
  id: number; row: number; col: number; status: number;
}

interface ScheduleDetail {
  id: number;
  play: { id: number; name: string; typeName: string; duration: number };
  studio: { id: number; name: string };
  showTime: string;
  ticketPrice: number;
  seats: SeatData[];
  seatLayout: string[];
}

/** 选座订票页 */
function SeatsPage() {
  const { scheduleId } = useParams<{ scheduleId: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [detail, setDetail] = useState<ScheduleDetail | null>(null);
  const [locking, setLocking] = useState(false);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [remainSeconds, setRemainSeconds] = useState(0);

  const { setLockInfo, clearCart } = useCartStore();

  useEffect(() => {
    if (!scheduleId) return;
    setLoading(true);
    getScheduleById(Number(scheduleId))
      .then((res) => setDetail(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
    return () => { clearCart(); };
  }, [scheduleId]);

  // 倒计时
  useEffect(() => {
    if (remainSeconds <= 0) return;
    const timer = setInterval(() => {
      setRemainSeconds((prev) => {
        if (prev <= 1) { message.warning('锁座已过期，请重新选择'); return 0; }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, [remainSeconds]);

  /** 点击座位 */
  const toggleSeat = (seatId: number, status: number) => {
    if (remainSeconds > 0) return;
    if (status !== 0) { message.warning('该座位不可选'); return; }
    setSelectedIds((prev) => {
      if (prev.includes(seatId)) return prev.filter((id) => id !== seatId);
      if (prev.length >= 6) { message.warning('最多选择6个座位'); return prev; }
      return [...prev, seatId];
    });
  };

  /** 锁定座位 */
  const handleLock = async () => {
    if (selectedIds.length === 0) { message.warning('请先选择座位'); return; }
    setLocking(true);
    try {
      const res = await lockSeats({ scheduleId: Number(scheduleId), seatIds: selectedIds });
      const { lockToken, tickets, totalPrice, expireAt } = res.data;
      setLockInfo({ lockToken, expireAt });
      useCartStore.setState({
        scheduleId: Number(scheduleId),
        selectedSeats: tickets.map((t: { seatId: number; row: number; col: number }) => ({
          id: t.seatId, row: t.row, col: t.col,
        })),
        totalPrice,
      });
      setRemainSeconds(300);
      message.success('座位已锁定，请在5分钟内完成支付');
    } catch { /* 错误已提示 */ }
    finally { setLocking(false); }
  };

  /** 确认下单 */
  const handleConfirm = async () => {
    const { lockToken } = useCartStore.getState();
    if (!lockToken) return;
    try {
      const res = await createOrder({ lockToken });
      navigate(`/order?orderId=${res.data.orderId}`, { replace: true });
    } catch { /* 错误已提示 */ }
  };

  if (loading) return <div className="text-center py-20"><Spin size="large" /></div>;
  if (!detail) return <Empty description="场次信息加载失败" />;

  const { play, studio, showTime, ticketPrice, seatLayout } = detail;
  const formatTime = (s: number) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;

  return (
    <div>
      {/* 页标题 */}
      <h1 className="font-serif text-2xl text-ink tracking-wide mb-6">选择座位</h1>

      {/* 演出信息卡片 */}
      <div className="border border-warm bg-cream rounded-sm p-5 mb-5">
        <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-4">
          <div>
            <h2 className="font-serif text-xl text-ink mb-1">{play.name}</h2>
            <p className="text-stone text-sm">{play.typeName} · {play.duration}分钟</p>
          </div>
          <div className="text-right">
            <p className="text-stone text-sm">{studio.name}</p>
            <p className="text-ink font-medium">{showTime}</p>
            <p className="text-2xl text-ink font-medium mt-1">¥{ticketPrice.toFixed(2)}</p>
          </div>
        </div>

        {/* 锁定倒计时 */}
        {remainSeconds > 0 && (
          <div className="mt-4 text-center">
            <span className="inline-flex items-center gap-2 text-gold font-medium text-lg
                             border border-gold px-5 py-1.5 rounded-sm">
              <ClockCircleOutlined /> 剩余 {formatTime(remainSeconds)}
            </span>
          </div>
        )}
      </div>

      {/* 座位图 */}
      <div className="border border-warm bg-cream rounded-sm p-6 mb-5">
        {/* 舞台标识 — 衬线字体 */}
        <div className="text-center mb-8">
          <span className="font-serif text-light-ink text-sm tracking-widest">—— 舞 台 ——</span>
          <div className="h-1 bg-warm rounded-full mt-2 max-w-xs mx-auto" />
        </div>

        {/* 座位网格 */}
        <div className="overflow-x-auto text-center">
          <div className="inline-block">
            {seatLayout.map((rowStr, rIdx) => (
              <div key={rIdx} className="flex justify-center gap-1.5 mb-1.5">
                {rowStr.split('').map((ch, cIdx) => {
                  const seat = detail.seats.find((s) => s.row === rIdx + 1 && s.col === cIdx + 1);
                  const seatId = seat?.id || 0;
                  const isSelected = selectedIds.includes(seatId);
                  const isLocked = remainSeconds > 0 && isSelected;

                  // 座位外观：根据状态组合 Tailwind 类
                  let seatClass = 'w-8 h-8 flex items-center justify-center text-[10px] rounded-sm transition-soft';
                  let label = '';
                  let cursorClass = 'cursor-default';

                  if (ch === '_') {
                    seatClass += ' bg-transparent';
                    label = '';
                  } else if (isLocked) {
                    seatClass += ' bg-ink text-white';
                    label = `${rIdx + 1}-${cIdx + 1}`;
                  } else if (isSelected) {
                    seatClass += ' bg-ink text-white';
                    label = `${rIdx + 1}-${cIdx + 1}`;
                  } else if (seat?.status === 0) {
                    seatClass += ' bg-transparent border border-warm text-stone hover:border-ink hover:text-ink';
                    cursorClass = 'cursor-pointer';
                    label = `${rIdx + 1}-${cIdx + 1}`;
                  } else if (seat?.status === 2) {
                    seatClass += ' bg-cream text-light-ink line-through';
                    label = '✕';
                  } else {
                    // status === 1 或其他
                    seatClass += ' bg-warm text-light-ink';
                    label = '—';
                  }

                  return (
                    <div
                      key={cIdx}
                      onClick={() => ch !== '_' && toggleSeat(seatId, seat?.status || -1)}
                      className={`${seatClass} ${cursorClass}`}
                    >
                      {label}
                    </div>
                  );
                })}
              </div>
            ))}
          </div>
        </div>

        {/* 图例 */}
        <div className="flex flex-wrap justify-center gap-5 mt-6 text-sm text-stone">
          <span className="inline-flex items-center gap-1.5">
            <span className="w-4 h-4 rounded-sm border border-warm bg-transparent inline-block" /> 可选
          </span>
          <span className="inline-flex items-center gap-1.5">
            <span className="w-4 h-4 rounded-sm bg-ink inline-block" /> 已选
          </span>
          <span className="inline-flex items-center gap-1.5">
            <span className="w-4 h-4 rounded-sm bg-warm inline-block" /> 已锁
          </span>
          <span className="inline-flex items-center gap-1.5">
            <span className="w-4 h-4 rounded-sm bg-cream inline-block text-light-ink text-center text-xs leading-4">✕</span> 已售
          </span>
          <span className="inline-flex items-center gap-1.5">
            <span className="w-4 h-4 rounded-sm border border-dashed border-warm bg-transparent inline-block" /> 过道
          </span>
        </div>
      </div>

      {/* 底部操作栏 */}
      <div className="border border-warm bg-cream rounded-sm p-4 flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="text-stone text-sm">
          已选 <Text className="!text-ink !font-medium">{selectedIds.length}</Text> 座（最多 6 座）
          {remainSeconds === 0 && selectedIds.length > 0 && (
            <span className="ml-4">
              预估 <Text className="!text-ink !font-medium">¥{(selectedIds.length * ticketPrice).toFixed(2)}</Text>
            </span>
          )}
        </div>
        <div>
          {remainSeconds === 0 ? (
            <button
              onClick={handleLock}
              disabled={selectedIds.length === 0 || locking}
              className="bg-gold text-white px-8 py-2.5 rounded-sm font-medium
                         hover:bg-[#B8944F] transition-soft cursor-pointer
                         disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {locking ? '锁定中...' : '锁定座位'}
            </button>
          ) : (
            <button
              onClick={handleConfirm}
              className="bg-gold text-white px-8 py-2.5 rounded-sm font-medium
                         hover:bg-[#B8944F] transition-soft cursor-pointer"
            >
              确认下单
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default SeatsPage;
