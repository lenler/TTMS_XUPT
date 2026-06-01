// 我的订单页

import { useEffect, useState } from 'react';
import { Button, Card, Table, Tag, message } from 'antd';
import { getCustomerOrders, refundOrder, type CustomerOrder } from '@/services/customer/order';

function OrdersPage() {
  const [orders, setOrders] = useState<CustomerOrder[]>([]);

  const refresh = () => getCustomerOrders().then(setOrders).catch(() => {});

  useEffect(() => {
    refresh();
  }, []);

  const refund = async (id: number) => {
    try {
      await refundOrder(id);
      message.success('退票成功');
      refresh();
    } catch (err) {
      message.error(err instanceof Error ? err.message : '退票失败');
    }
  };

  return (
    <Card title="我的订单">
      <Table
        rowKey="id"
        dataSource={orders}
        expandable={{
          expandedRowRender: (record) => (
            <Table
              rowKey="ticketId"
              pagination={false}
              dataSource={record.items}
              columns={[
                { title: '票ID', dataIndex: 'ticketId' },
                { title: '座位', render: (_, item) => `${item.rowNo}排${item.colNo}座` },
                { title: '价格', dataIndex: 'price', render: (v: number) => `¥${Number(v).toFixed(2)}` },
              ]}
            />
          ),
        }}
        columns={[
          { title: '订单号', dataIndex: 'id' },
          { title: '时间', dataIndex: 'saleTime' },
          { title: '金额', dataIndex: 'paidAmount', render: (v: number) => `¥${Number(v).toFixed(2)}` },
          { title: '状态', dataIndex: 'status', render: (v: string) => <Tag color={v === 'PAID' ? 'green' : 'default'}>{v}</Tag> },
          { title: '操作', render: (_, record) => <Button disabled={record.status !== 'PAID'} onClick={() => refund(record.id)}>退票</Button> },
        ]}
      />
    </Card>
  );
}

export default OrdersPage;
