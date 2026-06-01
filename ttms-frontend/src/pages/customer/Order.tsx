// 确认订单页

import { Button, Card, Descriptions, InputNumber, message } from 'antd';
import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createCustomerOrder } from '@/services/customer/order';

function OrderPage() {
  const navigate = useNavigate();
  const pending = useMemo(() => JSON.parse(localStorage.getItem('pendingOrder') || 'null') as null | { ticketIds: number[]; total: number }, []);
  const [paidAmount, setPaidAmount] = useState(pending?.total || 0);
  const [saving, setSaving] = useState(false);

  const submit = async () => {
    if (!pending) {
      message.warning('没有待支付订单');
      return;
    }
    setSaving(true);
    try {
      const order = await createCustomerOrder(pending.ticketIds, paidAmount);
      localStorage.removeItem('pendingOrder');
      navigate(`/result/${order.id}`);
    } catch (err) {
      message.error(err instanceof Error ? err.message : '支付失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Card title="确认订单" style={{ maxWidth: 640, margin: '0 auto' }}>
      <Descriptions column={1} bordered>
        <Descriptions.Item label="票数">{pending?.ticketIds.length || 0}</Descriptions.Item>
        <Descriptions.Item label="应付金额">¥{(pending?.total || 0).toFixed(2)}</Descriptions.Item>
        <Descriptions.Item label="实付金额">
          <InputNumber min={0} precision={2} value={paidAmount} onChange={(val) => setPaidAmount(val || 0)} />
        </Descriptions.Item>
      </Descriptions>
      <Button type="primary" block style={{ marginTop: 16 }} loading={saving} onClick={submit}>确认支付</Button>
    </Card>
  );
}

export default OrderPage;
