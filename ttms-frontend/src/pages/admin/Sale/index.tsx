// 售票管理页：销售记录 + 线下售票

import { useState, useEffect, useMemo } from 'react';
import { Button, Modal, Select, InputNumber, Descriptions, message, Space, Tag } from 'antd';
import PageTable from '@/components/common/PageTable';
import { useCRUD } from '@/hooks/useCRUD';
import { getSales, createSale } from '@/services/admin/sale';
import { getSchedules, getScheduleTickets } from '@/services/admin/schedule';
import { getCustomers } from '@/services/admin/customer';
import type { Sale } from '@/types/models';
import type { Ticket } from '@/types/models';
import type { Customer } from '@/types/models';

/** 表格列定义 */
const columns = [
  { title: '销售单号', dataIndex: 'id', key: 'id', width: 100 },
  { title: '售票员', dataIndex: 'employeeName', key: 'employeeName', width: 100 },
  { title: '顾客', dataIndex: 'customerName', key: 'customerName', width: 100 },
  { title: '时间', dataIndex: 'saleTime', key: 'saleTime', width: 180 },
  {
    title: '金额',
    dataIndex: 'paymentAmount',
    key: 'paymentAmount',
    width: 100,
    render: (v: number) => `¥${v.toFixed(2)}`,
  },
  {
    title: '票数',
    key: 'ticketCount',
    width: 60,
    render: (_: unknown, record: Sale) => record.items?.length || 0,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 80,
    render: (v: number) => {
      const map: Record<number, { color: string; text: string }> = {
        0: { color: 'orange', text: '待支付' },
        1: { color: 'green', text: '已支付' },
        2: { color: 'orange', text: '退票中' },
        3: { color: 'red', text: '已退票' },
        4: { color: 'default', text: '已取消' },
      };
      const cfg = map[v] || { color: 'default', text: '未知' };
      return <Tag color={cfg.color}>{cfg.text}</Tag>;
    },
  },
];

/** 座位颜色映射 */
function seatColor(ticket: Ticket, selected: boolean): string {
  if (selected) return '#1677ff';  // 蓝色=选中
  if (ticket.status === 2) return '#ff4d4f'; // 红色=已售
  if (ticket.status === 1) return '#faad14'; // 黄色=锁定
  return '#52c41a'; // 绿色=可选
}

function SaleListPage() {
  const { list, loading, pagination, keyword, setKeyword, setPage, setPageSize, refresh } =
    useCRUD<Sale>(getSales);

  const [modalOpen, setModalOpen] = useState(false);

  /** 分页变化 */
  const handlePageChange = (page: number, pageSize: number) => {
    setPage(page);
    setPageSize(pageSize);
    refresh({ page, keyword });
  };

  return (
    <>
      <PageTable<Sale>
        columns={columns}
        dataSource={list}
        loading={loading}
        pagination={pagination}
        keyword={keyword}
        onSearch={setKeyword}
        onAdd={() => setModalOpen(true)}
        onPageChange={handlePageChange}
        addText="新增售票"
        rowKey="id"
      />

      <SaleModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSuccess={() => {
          setModalOpen(false);
          refresh();
        }}
      />
    </>
  );
}

/** 售票弹窗 */
function SaleModal({
  open,
  onClose,
  onSuccess,
}: {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
}) {
  const [scheduleId, setScheduleId] = useState<number | null>(null);
  const [scheduleOptions, setScheduleOptions] = useState<{ value: number; label: string }[]>([]);
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [customerId, setCustomerId] = useState<number | null>(null);
  const [customerOptions, setCustomerOptions] = useState<{ value: number; label: string }[]>([]);
  const [paymentAmount, setPaymentAmount] = useState<number>(0);
  const [saving, setSaving] = useState(false);

  /** 加载场次下拉 */
  useEffect(() => {
    if (open) {
      getSchedules({ page: 1, pageSize: 100 }).then((res) =>
        setScheduleOptions(
          res.data.list.map((s) => ({
            value: s.id,
            label: `${s.playName} / ${s.studioName} / ${s.showTime}`,
          }))
        )
      ).catch(() => {});
      getCustomers({ page: 1, pageSize: 100 }).then((res) =>
        setCustomerOptions(
          res.data.list.map((c: Customer) => ({
            value: c.id,
            label: `${c.name}（${c.phone}）`,
          }))
        )
      ).catch(() => {});
    }
  }, [open]);

  /** 场次变化时加载票数据 */
  useEffect(() => {
    if (scheduleId) {
      getScheduleTickets(scheduleId, { pageSize: 500 }).then((res) => {
        setTickets(res.data.list);
        setSelectedIds(new Set());
      }).catch(() => {});
    } else {
      setTickets([]);
      setSelectedIds(new Set());
    }
  }, [scheduleId]);

  /** 点击座位 */
  const handleSeatClick = (ticket: Ticket) => {
    if (ticket.status !== 0) return; // 只允许选择 available 票
    setSelectedIds((prev) => {
      const next = new Set(prev);
      if (next.has(ticket.ticketId)) {
        next.delete(ticket.ticketId);
      } else {
        next.add(ticket.ticketId);
      }
      return next;
    });
  };

  /** 选中的票列表 */
  const selectedTickets = useMemo(
    () => tickets.filter((t) => selectedIds.has(t.ticketId)),
    [tickets, selectedIds]
  );

  /** 总价 */
  const totalPrice = useMemo(
    () => selectedTickets.reduce((sum, t) => sum + t.price, 0),
    [selectedTickets]
  );

  /** 找零 */
  const change = paymentAmount - totalPrice;

  /** 计算最大行列 */
  const maxRow = useMemo(() => {
    if (tickets.length === 0) return 0;
    return Math.max(...tickets.map((t) => t.seatRow));
  }, [tickets]);
  const maxCol = useMemo(() => {
    if (tickets.length === 0) return 0;
    return Math.max(...tickets.map((t) => t.seatCol));
  }, [tickets]);

  /** 构建座位网格 */
  const seatGrid = useMemo(() => {
    const grid: (Ticket | null)[][] = [];
    for (let r = 1; r <= maxRow; r++) {
      const row: (Ticket | null)[] = [];
      for (let c = 1; c <= maxCol; c++) {
        const ticket = tickets.find((t) => t.seatRow === r && t.seatCol === c) || null;
        row.push(ticket);
      }
      grid.push(row);
    }
    return grid;
  }, [tickets, maxRow, maxCol]);

  /** 提交售票 */
  const handleSubmit = async () => {
    if (!scheduleId) {
      message.warning('请选择演出场次');
      return;
    }
    if (selectedIds.size === 0) {
      message.warning('请选择座位');
      return;
    }
    if (!customerId) {
      message.warning('请选择顾客');
      return;
    }
    if (paymentAmount < totalPrice) {
      message.warning('收款金额不足');
      return;
    }
    setSaving(true);
    try {
      await createSale({
        scheduleId,
        ticketIds: Array.from(selectedIds),
        customerId,
        paymentAmount,
      });
      message.success('售票成功');
      onSuccess();
    } catch {
      // 错误已处理
    } finally {
      setSaving(false);
    }
  };

  /** 关闭时重置 */
  const handleClose = () => {
    setScheduleId(null);
    setTickets([]);
    setSelectedIds(new Set());
    setCustomerId(null);
    setPaymentAmount(0);
    onClose();
  };

  return (
    <Modal
      title="新增售票"
      open={open}
      onCancel={handleClose}
      width={900}
      footer={null}
      destroyOnClose
    >
      <div style={{ display: 'flex', gap: 24 }}>
        {/* 左侧：选场次 + 座位图 */}
        <div style={{ flex: 1 }}>
          <div style={{ marginBottom: 12 }}>
            <span style={{ fontWeight: 'bold', marginRight: 8 }}>演出场次：</span>
            <Select
              placeholder="选择演出场次"
              style={{ width: '100%' }}
              value={scheduleId}
              onChange={(val) => setScheduleId(val)}
              options={scheduleOptions}
              showSearch
              filterOption={(input, option) =>
                (option?.label as string)?.includes(input) ?? false
              }
            />
          </div>

          {tickets.length > 0 && (
            <div>
              {/* 图例 */}
              <Space style={{ marginBottom: 8 }}>
                <span style={{ display: 'inline-block', width: 12, height: 12, background: '#52c41a', borderRadius: 2 }} />
                <span style={{ fontSize: 12 }}>可选</span>
                <span style={{ display: 'inline-block', width: 12, height: 12, background: '#ff4d4f', borderRadius: 2 }} />
                <span style={{ fontSize: 12 }}>已售</span>
                <span style={{ display: 'inline-block', width: 12, height: 12, background: '#1677ff', borderRadius: 2 }} />
                <span style={{ fontSize: 12 }}>已选</span>
              </Space>

              {/* 座位网格 */}
              <div style={{ maxHeight: 400, overflow: 'auto' }}>
                {seatGrid.map((row, ri) => (
                  <div key={ri} style={{ display: 'flex', justifyContent: 'center', gap: 3, marginBottom: 3 }}>
                    <span style={{ width: 24, fontSize: 12, lineHeight: '28px', textAlign: 'right', marginRight: 4, color: '#999' }}>
                      {ri + 1}
                    </span>
                    {row.map((ticket, ci) => (
                      <div
                        key={`${ri}-${ci}`}
                        onClick={() => ticket && handleSeatClick(ticket)}
                        style={{
                          width: 28,
                          height: 28,
                          borderRadius: 4,
                          background: ticket ? seatColor(ticket, selectedIds.has(ticket.ticketId)) : '#f0f0f0',
                          border: ticket ? '1px solid rgba(0,0,0,0.1)' : 'none',
                          cursor: ticket && ticket.status === 0 ? 'pointer' : 'not-allowed',
                          transition: 'background 0.15s',
                        }}
                        title={
                          ticket
                            ? `${ticket.seatRow}排${ticket.seatCol}座 ¥${ticket.price}`
                            : ''
                        }
                      />
                    ))}
                  </div>
                ))}
              </div>
              <div style={{ textAlign: 'center', marginTop: 4, fontSize: 12, color: '#999' }}>
                屏幕
                <div style={{ width: 80, height: 6, background: '#d9d9d9', borderRadius: 3, margin: '2px auto' }} />
              </div>
            </div>
          )}
        </div>

        {/* 右侧：汇总 + 顾客 + 收款 */}
        <div style={{ width: 260, borderLeft: '1px solid #f0f0f0', paddingLeft: 16 }}>
          <div style={{ fontWeight: 'bold', marginBottom: 12 }}>已选座位</div>
          {selectedTickets.length === 0 ? (
            <div style={{ color: '#999', fontSize: 13, marginBottom: 16 }}>暂未选择</div>
          ) : (
            <div style={{ marginBottom: 16, maxHeight: 200, overflow: 'auto' }}>
              {selectedTickets.map((t) => (
                <div key={t.ticketId} style={{ fontSize: 13, marginBottom: 4 }}>
                  {t.seatRow}排{t.seatCol}座 — ¥{t.price.toFixed(2)}
                </div>
              ))}
            </div>
          )}

          <Descriptions column={1} size="small" style={{ marginBottom: 16 }}>
            <Descriptions.Item label="票数">{selectedTickets.length}</Descriptions.Item>
            <Descriptions.Item label="总价">¥{totalPrice.toFixed(2)}</Descriptions.Item>
          </Descriptions>

          <div style={{ marginBottom: 12 }}>
            <div style={{ fontWeight: 'bold', marginBottom: 4 }}>选择顾客</div>
            <Select
              placeholder="选择顾客"
              style={{ width: '100%' }}
              value={customerId}
              onChange={(val) => setCustomerId(val)}
              options={customerOptions}
              showSearch
              filterOption={(input, option) =>
                (option?.label as string)?.includes(input) ?? false
              }
            />
          </div>

          <div style={{ marginBottom: 12 }}>
            <div style={{ fontWeight: 'bold', marginBottom: 4 }}>实收金额</div>
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              precision={2}
              prefix="¥"
              value={paymentAmount || null}
              onChange={(val) => setPaymentAmount(val || 0)}
            />
          </div>

          {paymentAmount > 0 && (
            <Descriptions column={1} size="small" style={{ marginBottom: 16 }}>
              <Descriptions.Item label="找零">
                {change >= 0 ? `¥${change.toFixed(2)}` : <span style={{ color: 'red' }}>不足 ¥{Math.abs(change).toFixed(2)}</span>}
              </Descriptions.Item>
            </Descriptions>
          )}

          <Button type="primary" block loading={saving} onClick={handleSubmit}>
            确认售票
          </Button>
        </div>
      </div>
    </Modal>
  );
}

export default SaleListPage;
