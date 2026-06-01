// 财务统计页

import { useEffect, useState } from 'react';
import { Card, Col, DatePicker, Row, Statistic, Table } from 'antd';
import dayjs from 'dayjs';
import { getDailySummary, getTheaterSummary, type FinanceSummary } from '@/services/admin/finance';

function FinancePage() {
  const [theater, setTheater] = useState<FinanceSummary | null>(null);
  const [daily, setDaily] = useState<FinanceSummary | null>(null);
  const [date, setDate] = useState(dayjs());

  useEffect(() => {
    getTheaterSummary().then(setTheater).catch(() => {});
  }, []);

  useEffect(() => {
    getDailySummary({ date: date.format('YYYY-MM-DD') }).then(setDaily).catch(() => {});
  }, [date]);

  const rows = [
    { key: 'theater', name: '剧院累计', ...(theater || {}) },
    { key: 'daily', name: date.format('YYYY-MM-DD'), ...(daily || {}) },
  ];

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card><Statistic title="累计销售额" prefix="¥" value={theater?.salesAmount || 0} precision={2} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="累计订单" value={theater?.orderCount || 0} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="已售票数" value={theater?.soldTicketCount || 0} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="上座率" suffix="%" value={theater?.attendanceRate || 0} precision={2} /></Card>
        </Col>
      </Row>
      <Card title="财务明细" extra={<DatePicker value={date} onChange={(val) => val && setDate(val)} />}>
        <Table
          rowKey="key"
          pagination={false}
          dataSource={rows}
          columns={[
            { title: '范围', dataIndex: 'name' },
            { title: '销售额', dataIndex: 'salesAmount', render: (v = 0) => `¥${Number(v).toFixed(2)}` },
            { title: '订单数', dataIndex: 'orderCount' },
            { title: '售出票数', dataIndex: 'soldTicketCount' },
            { title: '验票数', dataIndex: 'checkedTicketCount' },
            { title: '总票数', dataIndex: 'totalTicketCount' },
            { title: '上座率', dataIndex: 'attendanceRate', render: (v = 0) => `${Number(v).toFixed(2)}%` },
          ]}
        />
      </Card>
    </div>
  );
}

export default FinancePage;
