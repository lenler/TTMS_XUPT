// 退票处理页：销售记录 + 退票操作

import { useState } from 'react';
import { Button, Modal, Table, Checkbox, Descriptions, message, Tag } from 'antd';
import PageTable from '@/components/common/PageTable';
import { useCRUD } from '@/hooks/useCRUD';
import { getSales, refundSale } from '@/services/admin/sale';
import type { Sale, SaleItem } from '@/types/models';

/** 表格列定义 */
const columns = [
  { title: '销售单号', dataIndex: 'id', key: 'id', width: 100 },
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

function RefundPage() {
  const { list, loading, pagination, keyword, setKeyword, setPage, setPageSize, refresh } =
    useCRUD<Sale>(getSales);

  const [refundModalOpen, setRefundModalOpen] = useState(false);
  const [refundingSale, setRefundingSale] = useState<Sale | null>(null);
  const [selectedTicketIds, setSelectedTicketIds] = useState<Set<number>>(new Set());
  const [saving, setSaving] = useState(false);

  /** 分页变化 */
  const handlePageChange = (page: number, pageSize: number) => {
    setPage(page);
    setPageSize(pageSize);
    refresh({ page, keyword });
  };

  /** 打开退票弹窗 */
  const handleOpenRefund = (record: Sale) => {
    if (record.status !== 1) {
      message.warning('仅已支付订单可退票');
      return;
    }
    if (!record.items || record.items.length === 0) {
      message.warning('该订单无票可退');
      return;
    }
    setRefundingSale(record);
    setSelectedTicketIds(new Set());
    setRefundModalOpen(true);
  };

  /** 提交退票 */
  const handleSubmitRefund = async () => {
    if (!refundingSale || selectedTicketIds.size === 0) {
      message.warning('请选择要退的票');
      return;
    }
    setSaving(true);
    try {
      await refundSale(refundingSale.id, Array.from(selectedTicketIds));
      message.success('退票成功');
      setRefundModalOpen(false);
      refresh();
    } catch {
      // 错误已处理
    } finally {
      setSaving(false);
    }
  };

  /** 退票金额 */
  const refundAmount = refundingSale?.items
    ?.filter((it) => selectedTicketIds.has(it.ticketId))
    .reduce((sum, it) => sum + it.price, 0) || 0;

  return (
    <>
      <PageTable<Sale>
        columns={columns}
        dataSource={list}
        loading={loading}
        pagination={pagination}
        keyword={keyword}
        onSearch={setKeyword}
        onPageChange={handlePageChange}
        rowKey="id"
        // 自定义操作列
        renderActions={(record) => (
          <Button
            type="link"
            size="small"
            danger={record.status === 1}
            disabled={record.status !== 1}
            onClick={() => handleOpenRefund(record)}
          >
            退票
          </Button>
        )}
      />

      {/* 退票 Modal */}
      <Modal
        title={`退票 — 销售单 #${refundingSale?.id}`}
        open={refundModalOpen}
        onCancel={() => setRefundModalOpen(false)}
        onOk={handleSubmitRefund}
        confirmLoading={saving}
        okText="确认退票"
        okButtonProps={{ danger: true }}
        destroyOnClose
        width={600}
      >
        {refundingSale && (
          <>
            <Descriptions column={2} size="small" style={{ marginBottom: 16 }}>
              <Descriptions.Item label="顾客">{refundingSale.customerName}</Descriptions.Item>
              <Descriptions.Item label="时间">{refundingSale.saleTime}</Descriptions.Item>
              <Descriptions.Item label="金额">¥{refundingSale.paymentAmount.toFixed(2)}</Descriptions.Item>
            </Descriptions>

            <div style={{ fontWeight: 'bold', marginBottom: 8 }}>选择要退的票：</div>
            <Table<SaleItem>
              dataSource={refundingSale.items || []}
              rowKey="ticketId"
              pagination={false}
              size="small"
              columns={[
                {
                  title: '',
                  key: 'select',
                  width: 40,
                  render: (_: unknown, record: SaleItem) => (
                    <Checkbox
                      checked={selectedTicketIds.has(record.ticketId)}
                      onChange={(e) => {
                        setSelectedTicketIds((prev) => {
                          const next = new Set(prev);
                          if (e.target.checked) {
                            next.add(record.ticketId);
                          } else {
                            next.delete(record.ticketId);
                          }
                          return next;
                        });
                      }}
                    />
                  ),
                },
                { title: '票ID', dataIndex: 'ticketId', key: 'ticketId', width: 80 },
                {
                  title: '座位',
                  key: 'seat',
                  render: (_: unknown, r: SaleItem) => `${r.seatRow}排${r.seatCol}座`,
                },
                {
                  title: '价格',
                  dataIndex: 'price',
                  key: 'price',
                  render: (v: number) => `¥${v.toFixed(2)}`,
                },
              ]}
            />

            {selectedTicketIds.size > 0 && (
              <div style={{ marginTop: 12, fontWeight: 'bold', color: '#ff4d4f' }}>
                将退还 ¥{refundAmount.toFixed(2)}
              </div>
            )}
          </>
        )}
      </Modal>
    </>
  );
}

export default RefundPage;
