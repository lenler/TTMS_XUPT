// 选座订票页：座位图 + 选座 + 锁定 + 确认下单

import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Typography, Button, Spin, Empty, Tag, message, Row, Col } from 'antd';
import { ClockCircleOutlined } from '@ant-design/icons';
import { getScheduleById } from '@/services/customer/schedule';
import { lockSeats, createOrder } from '@/services/customer/order';
import { useCartStore } from '@/stores/cartStore';

const { Title, Text } = Typography;

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
    if (remainSeconds > 0) return; // 已锁定中不可改
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
        selectedSeats: tickets.map((t) => ({ id: t.seatId, row: t.row, col: t.col })),
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

  if (loading) return <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>;
  if (!detail) return <Empty description="场次信息加载失败" />;

  const { play, studio, showTime, ticketPrice, seatLayout } = detail;
  const formatTime = (s: number) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto' }}>
      {/* 演出信息 */}
      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16}>
          <Col span={12}>
            <Title level={4} style={{ margin: 0 }}>{play.name}</Title>
            <Text type="secondary">{play.typeName} · {play.duration}分钟</Text>
          </Col>
          <Col span={12} style={{ textAlign: 'right' }}>
            <Text>{studio.name}</Text><br />
            <Text strong>{showTime}</Text><br />
            <Text style={{ color: '#ff4d4f', fontSize: 18 }}>¥{ticketPrice.toFixed(2)}</Text>
          </Col>
        </Row>
        {remainSeconds > 0 && (
          <div style={{ marginTop: 12, textAlign: 'center' }}>
            <Tag color="processing" style={{ fontSize: 16, padding: '4px 16px' }}>
              <ClockCircleOutlined /> 剩余 {formatTime(remainSeconds)}
            </Tag>
          </div>
        )}
      </Card>

      {/* 座位图 */}
      <Card title="选择座位" style={{ marginBottom: 16 }}>
        <div style={{ textAlign: 'center', marginBottom: 12 }}>
          <Text type="secondary">屏幕</Text>
          <div style={{ height: 6, background: '#d9d9d9', borderRadius: 3, marginTop: 4 }} />
        </div>
        <div style={{ overflowX: 'auto', textAlign: 'center' }}>
          <div style={{ display: 'inline-block' }}>
            {seatLayout.map((rowStr, rIdx) => (
              <div key={rIdx} style={{ display: 'flex', justifyContent: 'center', gap: 4, marginBottom: 4 }}>
                {rowStr.split('').map((ch, cIdx) => {
                  const seat = detail.seats.find((s) => s.row === rIdx + 1 && s.col === cIdx + 1);
                  const seatId = seat?.id || 0;
                  const isSelected = selectedIds.includes(seatId);
                  const isLocked = remainSeconds > 0 && isSelected;
                  let bg = '#f0f0f0';
                  let cursor: React.CSSProperties['cursor'] = 'default';
                  if (ch === '_') bg = 'transparent';
                  else if (isLocked) bg = '#fa8c16';
                  else if (isSelected) bg = '#1677ff';
                  else if (seat?.status === 0) { bg = '#52c41a'; cursor = 'pointer'; }
                  else if (seat?.status === 1) bg = '#fa8c16';
                  else if (seat?.status === 2) bg = '#ff4d4f';
                  return (
                    <div
                      key={cIdx}
                      onClick={() => ch !== '_' && toggleSeat(seatId, seat?.status || -1)}
                      style={{
                        width: 32, height: 32, borderRadius: 4, background: bg,
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontSize: 10, color: (isSelected || isLocked || (seat?.status !== undefined && seat.status !== 0)) ? '#fff' : '#999',
                        cursor: ch === '_' ? 'default' : cursor, transition: 'background 0.2s',
                      }}
                    >
                      {ch !== '_' ? `${rIdx + 1}-${cIdx + 1}` : ''}
                    </div>
                  );
                })}
              </div>
            ))}
          </div>
        </div>
        {/* 图例 */}
        <div style={{ display: 'flex', gap: 16, justifyContent: 'center', marginTop: 16 }}>
          <span><div style={{ width: 16, height: 16, borderRadius: 3, background: '#52c41a', display: 'inline-block', verticalAlign: 'middle', marginRight: 4 }} />可选</span>
          <span><div style={{ width: 16, height: 16, borderRadius: 3, background: '#1677ff', display: 'inline-block', verticalAlign: 'middle', marginRight: 4 }} />已选</span>
          <span><div style={{ width: 16, height: 16, borderRadius: 3, background: '#fa8c16', display: 'inline-block', verticalAlign: 'middle', marginRight: 4 }} />已锁</span>
          <span><div style={{ width: 16, height: 16, borderRadius: 3, background: '#ff4d4f', display: 'inline-block', verticalAlign: 'middle', marginRight: 4 }} />已售</span>
          <span><div style={{ width: 16, height: 16, borderRadius: 3, background: 'transparent', display: 'inline-block', verticalAlign: 'middle', marginRight: 4, border: '1px solid #d9d9d9' }} />过道</span>
        </div>
      </Card>

      {/* 操作栏 */}
      <Card>
        <Row align="middle" justify="space-between">
          <Col>
            <Text>已选 <Text strong>{selectedIds.length}</Text> 座（最多 6 座）</Text>
            {remainSeconds === 0 && selectedIds.length > 0 && (
              <Text type="secondary" style={{ marginLeft: 16 }}>
                预估 ¥{(selectedIds.length * ticketPrice).toFixed(2)}
              </Text>
            )}
          </Col>
          <Col>
            {remainSeconds === 0 ? (
              <Button type="primary" size="large" loading={locking} onClick={handleLock}
                disabled={selectedIds.length === 0}>
                锁定座位
              </Button>
            ) : (
              <Button type="primary" size="large" onClick={handleConfirm}>
                确认下单
              </Button>
            )}
          </Col>
        </Row>
      </Card>
    </div>
  );
}

export default SeatsPage;
