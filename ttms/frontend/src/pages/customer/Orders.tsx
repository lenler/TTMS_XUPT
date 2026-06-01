// 我的订单页：订单列表 + 退票

import { useEffect, useState, useCallback } from 'react';
import { Typography, Table, Tag, Button, Modal, Checkbox, message, Spin, Empty } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { getMyOrders, getOrder, refundOrder } from '@/services/customer/order';

const { Title, Text } = Typography;

interface OrderItem {
  orderId: number; playName: string; poster: string;
  studioName: string; showTime: string; ticketCount: number;
  totalPrice: number; status: string; createdAt: string;
}

/** 订单详情中的票信息 */
interface RefundTicketInfo {
  ticketId: number; seatRow: number; seatCol: number; price: number; ticketStatus: string;
}

/** 状态映射 */
const statusMap: Record<string, { color: string; label: string }> = {
  unpaid: { color: 'default', label: '待支付' },
  paid: { color: 'green', label: '已支付' },
  refunding: { color: 'orange', label: '退票中' },
  refunded: { color: 'red', label: '已退票' },
  cancelled: { color: 'default', label: '已取消' },
};

/** 我的订单页 */
function OrdersPage() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<OrderItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const pageSize = 10;
  const [refundModal, setRefundModal] = useState<{ open: boolean; orderId: number }>({ open: false, orderId: 0 });
  const [refundTickets, setRefundTickets] = useState<RefundTicketInfo[]>([]);
  const [loadingTickets, setLoadingTickets] = useState(false);
  const [selectedTicketIds, setSelectedTicketIds] = useState<number[]>([]);
  const [refunding, setRefunding] = useState(false);

  const fetchData = useCallback(() => {
    setLoading(true);
    getMyOrders({ page, pageSize })
      .then((res) => { setData(res.data.list); setTotal(res.data.total); })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [page]);

  useEffect(() => { fetchData(); }, [fetchData]);

  /** 打开退票弹窗 —— 拉取订单详情获取真实票列表 */
  const openRefundModal = async (orderId: number) => {
    setRefundModal({ open: true, orderId });
    setSelectedTicketIds([]);
    setRefundTickets([]);
    setLoadingTickets(true);
    try {
      const res = await getOrder(orderId);
      setRefundTickets(res.data.tickets.filter((t) => t.ticketStatus === 'sold'));
    } catch { /* 错误已提示 */ }
    finally { setLoadingTickets(false); }
  };

  /** 关闭退票弹窗 */
  const closeRefundModal = () => {
    setRefundModal({ open: false, orderId: 0 });
    setSelectedTicketIds([]);
    setRefundTickets([]);
  };

  /** 退票 */
  const handleRefund = async () => {
    if (selectedTicketIds.length === 0) { message.warning('请选择要退的票'); return; }
    setRefunding(true);
    try {
      await refundOrder(refundModal.orderId, selectedTicketIds);
      message.success('退票成功');
      closeRefundModal();
      fetchData();
    } catch { /* 错误已提示 */ }
    finally { setRefunding(false); }
  };

  const columns: ColumnsType<OrderItem> = [
    { title: '订单号', dataIndex: 'orderId', key: 'orderId' },
    { title: '剧目', dataIndex: 'playName', key: 'playName' },
    { title: '演出厅', dataIndex: 'studioName', key: 'studioName' },
    { title: '演出时间', dataIndex: 'showTime', key: 'showTime' },
    { title: '票数', dataIndex: 'ticketCount', key: 'ticketCount' },
    {
      title: '金额', dataIndex: 'totalPrice', key: 'totalPrice',
      render: (v: number) => <Text style={{ color: '#ff4d4f' }}>¥{v.toFixed(2)}</Text>,
    },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (s: string) => {
        const m = statusMap[s] || { color: 'default', label: s };
        return <Tag color={m.color}>{m.label}</Tag>;
      },
    },
    { title: '下单时间', dataIndex: 'createdAt', key: 'createdAt' },
    {
      title: '操作', key: 'action',
      render: (_: unknown, record: OrderItem) => (
        record.status === 'paid' ? (
          <Button size="small" danger onClick={() => openRefundModal(record.orderId)}>
            退票
          </Button>
        ) : null
      ),
    },
  ];

  return (
    <div style={{ maxWidth: 1200, margin: '0 auto' }}>
      <Title level={3} style={{ marginBottom: 24 }}>我的订单</Title>
      <Spin spinning={loading}>
        {data.length === 0 && !loading ? (
          <Empty description="暂无订单" />
        ) : (
          <Table
            rowKey="orderId"
            columns={columns}
            dataSource={data}
            pagination={{ current: page, pageSize, total, onChange: setPage }}
          />
        )}
      </Spin>

      {/* 退票弹窗 */}
      <Modal
        title="退票"
        open={refundModal.open}
        onOk={handleRefund}
        onCancel={closeRefundModal}
        confirmLoading={refunding}
        okText="确认退票"
        okButtonProps={{ danger: true }}
      >
        <Spin spinning={loadingTickets}>
          {refundTickets.length === 0 && !loadingTickets ? (
            <Text type="secondary">该订单暂无可退的票</Text>
          ) : (
            <>
              <Text>请选择要退的票：</Text>
              <div style={{ marginTop: 12 }}>
                <Checkbox.Group value={selectedTicketIds} onChange={(v) => setSelectedTicketIds(v as number[])}>
                  {refundTickets.map((t) => (
                    <Checkbox
                      key={t.ticketId}
                      value={t.ticketId}
                      style={{ display: 'block', marginBottom: 6 }}
                    >
                      {t.seatRow}排{t.seatCol}座 — ¥{t.price.toFixed(2)}（票号：{t.ticketId}）
                    </Checkbox>
                  ))}
                </Checkbox.Group>
              </div>
              {selectedTicketIds.length > 0 && (
                <div style={{ marginTop: 12 }}>
                  <Text strong>
                    已选 {selectedTicketIds.length} 张，退款金额：
                    ¥{refundTickets
                      .filter((t) => selectedTicketIds.includes(t.ticketId))
                      .reduce((sum, t) => sum + t.price, 0)
                      .toFixed(2)}
                  </Text>
                </div>
              )}
            </>
          )}
        </Spin>
      </Modal>
    </div>
  );
}

export default OrdersPage;
