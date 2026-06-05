// 个人中心页（我的）—— 水墨留白 · 东方极简
// 集成：余额 + 我的订单 + 账号信息 + 票务信息

import { useEffect, useState, useCallback } from 'react';
import {
  Typography, Table, Tag, Button, Modal, Checkbox, InputNumber,
  Spin, Empty, Tabs, message, Descriptions,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { WalletOutlined, OrderedListOutlined, UserOutlined, QrcodeOutlined } from '@ant-design/icons';
import { getProfile, rechargeWallet, type CustomerProfile } from '@/services/customer/auth';
import { getMyOrders, getOrder, refundOrder } from '@/services/customer/order';

const { Text } = Typography;

// ====================== 类型 ======================

interface OrderItem {
  orderId: number; playName: string; poster: string;
  studioName: string; showTime: string; ticketCount: number;
  totalPrice: number; status: string; createdAt: string;
}

interface RefundTicketInfo {
  ticketId: number; seatRow: number; seatCol: number; price: number; ticketStatus: string;
}

interface TicketRow {
  ticketId: number; seatRow: number; seatCol: number;
  playName: string; studioName: string; showTime: string;
  price: number; status: string; orderId: number;
}

const statusMap: Record<string, { color: string; label: string }> = {
  unpaid: { color: 'default', label: '待支付' },
  paid: { color: 'green', label: '已支付' },
  refunding: { color: 'gold', label: '退票中' },
  refunded: { color: 'red', label: '已退票' },
  cancelled: { color: 'default', label: '已取消' },
};

// ====================== 余额面板 ======================

function BalancePanel() {
  const [loading, setLoading] = useState(false);
  const [recharging, setRecharging] = useState(false);
  const [profile, setProfile] = useState<CustomerProfile | null>(null);
  const [amount, setAmount] = useState<number | null>(100);

  const loadProfile = async () => {
    setLoading(true);
    try {
      const res = await getProfile();
      setProfile(res.data);
    } finally { setLoading(false); }
  };

  useEffect(() => { loadProfile(); }, []);

  const handleRecharge = async () => {
    if (!amount || amount <= 0) { message.warning('请输入有效的充值金额'); return; }
    setRecharging(true);
    try {
      const res = await rechargeWallet(amount);
      setProfile((prev) => prev ? { ...prev, balance: res.data.balance, rechargeTotal: res.data.rechargeTotal, rechargeCount: res.data.rechargeCount } : prev);
      message.success(`充值成功，当前余额 ¥${res.data.balance.toFixed(2)}`);
    } finally { setRecharging(false); }
  };

  if (loading) return <div className="text-center py-10"><Spin /></div>;

  return (
    <div>
      <div className="border border-warm bg-cream rounded-sm p-6 mb-4">
        <div className="flex items-center gap-4 mb-4">
          <div className="w-10 h-10 rounded-sm bg-ink text-white flex items-center justify-center text-xl">
            <WalletOutlined />
          </div>
          <div>
            <p className="text-stone text-sm">当前可用余额</p>
            <p className="text-3xl font-serif text-ink">¥{(profile?.balance ?? 0).toFixed(2)}</p>
          </div>
        </div>
        <div className="grid grid-cols-2 gap-3 text-sm border-t border-warm pt-3">
          <div><p className="text-stone">累计充值</p><p className="text-ink font-medium">¥{(profile?.rechargeTotal ?? 0).toFixed(2)}</p></div>
          <div><p className="text-stone">充值次数</p><p className="text-ink font-medium">{profile?.rechargeCount ?? 0} 次</p></div>
        </div>
      </div>
      <div className="border border-warm bg-cream rounded-sm p-4">
        <p className="font-serif text-ink mb-3">充值</p>
        <div className="flex gap-2">
          <InputNumber min={1} precision={2} value={amount} onChange={(v) => setAmount(v)} prefix="¥" className="flex-1" />
          <Button type="primary" loading={recharging} onClick={handleRecharge}>立即充值</Button>
        </div>
      </div>
    </div>
  );
}

// ====================== 我的订单面板 ======================

function OrdersPanel() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<OrderItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const pageSize = 8;
  const [refundModal, setRefundModal] = useState<{ open: boolean; orderId: number }>({ open: false, orderId: 0 });
  const [refundTickets, setRefundTickets] = useState<RefundTicketInfo[]>([]);
  const [lt, setLt] = useState(false);
  const [selected, setSelected] = useState<number[]>([]);
  const [refunding, setRefunding] = useState(false);

  const fetchData = useCallback(() => {
    setLoading(true);
    getMyOrders({ page, pageSize })
      .then((res) => { setData(res.data.list); setTotal(res.data.total); })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [page]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const openModal = async (orderId: number) => {
    setRefundModal({ open: true, orderId }); setSelected([]); setRefundTickets([]); setLt(true);
    try {
      const res = await getOrder(orderId);
      setRefundTickets(res.data.tickets.filter((t: RefundTicketInfo) => t.ticketStatus === 'sold'));
    } catch { }
    finally { setLt(false); }
  };

  const doRefund = async () => {
    if (selected.length === 0) { message.warning('请选择要退的票'); return; }
    setRefunding(true);
    try { await refundOrder(refundModal.orderId, selected); message.success('退票成功'); setRefundModal({ open: false, orderId: 0 }); fetchData(); }
    catch { }
    finally { setRefunding(false); }
  };

  const columns: ColumnsType<OrderItem> = [
    { title: '订单号', dataIndex: 'orderId', key: 'orderId', width: 90 },
    { title: '剧目', dataIndex: 'playName', key: 'playName' },
    { title: '演出时间', dataIndex: 'showTime', key: 'showTime' },
    { title: '票数', dataIndex: 'ticketCount', key: 'ticketCount', width: 60 },
    { title: '金额', dataIndex: 'totalPrice', key: 'totalPrice', render: (v: number) => <Text className="!text-ink font-medium">¥{v.toFixed(2)}</Text> },
    { title: '状态', dataIndex: 'status', key: 'status', render: (s: string) => { const m = statusMap[s] || { color: 'default', label: s }; return <Tag color={m.color}>{m.label}</Tag>; } },
    { title: '操作', key: 'action', render: (_: unknown, r: OrderItem) => r.status === 'paid' ? <Button size="small" danger onClick={() => openModal(r.orderId)}>退票</Button> : null },
  ];

  return (
    <div>
      <Spin spinning={loading}>
        {data.length === 0 && !loading ? <Empty description="暂无订单" /> : (
          <Table rowKey="orderId" columns={columns} dataSource={data} size="small"
            pagination={{ current: page, pageSize, total, onChange: setPage, size: 'small' }} />
        )}
      </Spin>
      <Modal title="退票" open={refundModal.open} onOk={doRefund} onCancel={() => setRefundModal({ open: false, orderId: 0 })} confirmLoading={refunding} okText="确认退票" cancelText="取消" okButtonProps={{ danger: true }}>
        <Spin spinning={lt}>
          {refundTickets.length === 0 && !lt ? <Text className="text-light-ink">该订单暂无可退的票</Text> : (
            <>
              <Checkbox.Group value={selected} onChange={(v) => setSelected(v as number[])}>
                {refundTickets.map((t) => (
                  <Checkbox key={t.ticketId} value={t.ticketId} className="block mb-1.5">
                    {t.seatRow}排{t.seatCol}座 ¥{t.price.toFixed(2)}（票号：{t.ticketId}）
                  </Checkbox>
                ))}
              </Checkbox.Group>
              {selected.length > 0 && <Text strong className="!text-ink mt-2 block">退款 ¥{refundTickets.filter((t) => selected.includes(t.ticketId)).reduce((s, t) => s + t.price, 0).toFixed(2)}</Text>}
            </>
          )}
        </Spin>
      </Modal>
    </div>
  );
}

// ====================== 账号信息面板 ======================

function AccountPanel() {
  const [loading, setLoading] = useState(false);
  const [profile, setProfile] = useState<CustomerProfile | null>(null);

  useEffect(() => {
    setLoading(true);
    getProfile().then((res) => setProfile(res.data)).catch(() => {}).finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="text-center py-10"><Spin /></div>;

  return (
    <div className="border border-warm bg-cream rounded-sm p-6">
      <Descriptions column={1} size="small" labelStyle={{ color: '#8B7355', width: 80 }}>
        <Descriptions.Item label="昵称">{profile?.name || '-'}</Descriptions.Item>
        <Descriptions.Item label="用户名">{profile?.username || '-'}</Descriptions.Item>
        <Descriptions.Item label="手机号">{profile?.phone || '-'}</Descriptions.Item>
        <Descriptions.Item label="邮箱">{profile?.email || '-'}</Descriptions.Item>
        <Descriptions.Item label="余额"><span className="text-ink font-medium">¥{(profile?.balance ?? 0).toFixed(2)}</span></Descriptions.Item>
      </Descriptions>
    </div>
  );
}

// ====================== 票务信息面板 ======================

function TicketsPanel() {
  const [loading, setLoading] = useState(false);
  const [tickets, setTickets] = useState<TicketRow[]>([]);

  useEffect(() => {
    setLoading(true);
    getMyOrders({ page: 1, pageSize: 50 })
      .then(async (res) => {
        const all: TicketRow[] = [];
        for (const order of res.data.list) {
          try {
            const detail = await getOrder(order.orderId);
            for (const t of detail.data.tickets) {
              all.push({
                ticketId: t.ticketId,
                seatRow: t.seatRow,
                seatCol: t.seatCol,
                playName: t.playName,
                studioName: t.studioName,
                showTime: t.showTime,
                price: t.price,
                status: t.ticketStatus,
                orderId: order.orderId,
              });
            }
          } catch { }
        }
        setTickets(all);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const ticketColumns: ColumnsType<TicketRow> = [
    {
      title: '票号', dataIndex: 'ticketId', key: 'ticketId', width: 80,
      render: (v: number) => <Text code className="!text-base !font-bold !text-ink">{v}</Text>,
    },
    { title: '剧目', dataIndex: 'playName', key: 'playName' },
    { title: '座位', key: 'seat', render: (_: unknown, r: TicketRow) => `${r.seatRow}排${r.seatCol}座` },
    { title: '演出厅', dataIndex: 'studioName', key: 'studioName' },
    { title: '时间', dataIndex: 'showTime', key: 'showTime' },
    { title: '价格', dataIndex: 'price', key: 'price', render: (v: number) => `¥${v.toFixed(2)}` },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (s: string) => {
        const m: Record<string, { color: string; label: string }> = { sold: { color: 'green', label: '已出票' }, checked: { color: 'blue', label: '已入场' }, refunded: { color: 'red', label: '已退票' } };
        const cfg = m[s] || { color: 'default', label: s };
        return <Tag color={cfg.color}>{cfg.label}</Tag>;
      },
    },
    { title: '订单号', dataIndex: 'orderId', key: 'orderId', width: 80 },
  ];

  return (
    <Spin spinning={loading}>
      {tickets.length === 0 && !loading ? <Empty description="暂无票务信息" /> : (
        <Table rowKey="ticketId" columns={ticketColumns} dataSource={tickets} size="small" pagination={{ pageSize: 10, size: 'small' }} />
      )}
    </Spin>
  );
}

// ====================== 主页面 ======================

function ProfilePage() {
  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="font-serif text-2xl text-ink tracking-wide mb-6">我的</h1>
      <Tabs
        defaultActiveKey="balance"
        items={[
          { key: 'balance', label: <span><WalletOutlined /> 余额</span>, children: <BalancePanel /> },
          { key: 'orders', label: <span><OrderedListOutlined /> 我的订单</span>, children: <OrdersPanel /> },
          { key: 'account', label: <span><UserOutlined /> 账号信息</span>, children: <AccountPanel /> },
          { key: 'tickets', label: <span><QrcodeOutlined /> 票务信息</span>, children: <TicketsPanel /> },
        ]}
      />
    </div>
  );
}

export default ProfilePage;
