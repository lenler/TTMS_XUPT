// 选座订票页

import { useEffect, useMemo, useState } from 'react';
import { Button, Card, Space, Tag, message } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { getScheduleTickets } from '@/services/customer/schedule';
import type { Ticket } from '@/types/models';

function SeatsPage() {
  const { scheduleId } = useParams();
  const navigate = useNavigate();
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [selected, setSelected] = useState<Set<number>>(new Set());

  useEffect(() => {
    if (scheduleId) {
      getScheduleTickets(Number(scheduleId)).then(setTickets).catch(() => message.error('票据加载失败'));
    }
  }, [scheduleId]);

  const selectedTickets = useMemo(() => tickets.filter((ticket) => selected.has(ticket.ticketId)), [tickets, selected]);
  const total = selectedTickets.reduce((sum, ticket) => sum + ticket.price, 0);

  const maxRow = Math.max(0, ...tickets.map((ticket) => ticket.seatRow));
  const maxCol = Math.max(0, ...tickets.map((ticket) => ticket.seatCol));

  const submit = () => {
    if (!localStorage.getItem('customerToken')) {
      message.warning('请先登录');
      navigate('/login');
      return;
    }
    if (selected.size === 0) {
      message.warning('请选择座位');
      return;
    }
    localStorage.setItem('pendingOrder', JSON.stringify({ ticketIds: Array.from(selected), total }));
    navigate('/order');
  };

  return (
    <Card title="选座订票" extra={<Space><Tag color="green">可选</Tag><Tag color="blue">已选</Tag><Tag color="red">不可选</Tag></Space>}>
      <div style={{ overflow: 'auto', padding: 16 }}>
        {Array.from({ length: maxRow }, (_, r) => (
          <div key={r} style={{ display: 'flex', justifyContent: 'center', gap: 6, marginBottom: 6 }}>
            {Array.from({ length: maxCol }, (_, c) => {
              const ticket = tickets.find((item) => item.seatRow === r + 1 && item.seatCol === c + 1);
              const active = ticket && ticket.status === 0;
              const checked = ticket && selected.has(ticket.ticketId);
              return (
                <button
                  key={c}
                  disabled={!active}
                  onClick={() => ticket && setSelected((prev) => {
                    const next = new Set(prev);
                    if (next.has(ticket.ticketId)) next.delete(ticket.ticketId);
                    else next.add(ticket.ticketId);
                    return next;
                  })}
                  style={{
                    width: 34,
                    height: 30,
                    border: 0,
                    borderRadius: 4,
                    color: '#fff',
                    cursor: active ? 'pointer' : 'not-allowed',
                    background: checked ? '#1677ff' : active ? '#52c41a' : '#ff4d4f',
                  }}
                >
                  {r + 1}-{c + 1}
                </button>
              );
            })}
          </div>
        ))}
      </div>
      <div style={{ textAlign: 'right' }}>
        已选 {selected.size} 张，合计 ¥{total.toFixed(2)}
        <Button type="primary" style={{ marginLeft: 16 }} onClick={submit}>提交订单</Button>
      </div>
    </Card>
  );
}

export default SeatsPage;
