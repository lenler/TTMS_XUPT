// 财务统计页：概览 + 剧目排名 + 剧院业绩 + 售票员业绩

import { useEffect, useState, useCallback } from 'react';
import {
  Card, Statistic, Row, Col, Table, Tabs, Spin, Empty, DatePicker, Space, Typography,
} from 'antd';
import {
  DollarOutlined, ShoppingCartOutlined, PercentageOutlined, FireOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { Dayjs } from 'dayjs';
import {
  getFinanceOverview,
  getPlayRanking,
  getStudioPerformance,
  getEmployeeSales,
  type FinanceOverview,
  type PlayRankingItem,
  type StudioPerformanceItem,
  type EmployeeSalesItem,
} from '@/services/admin/finance';

const { RangePicker } = DatePicker;
const { Title, Text } = Typography;

/** 财务概览面板 */
function OverviewTab() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<FinanceOverview | null>(null);
  const [dates, setDates] = useState<[Dayjs, Dayjs] | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const params: Record<string, string> = {};
      if (dates) {
        params.startDate = dates[0].format('YYYY-MM-DD');
        params.endDate = dates[1].format('YYYY-MM-DD');
      }
      const res = await getFinanceOverview(params);
      setData(res.data);
    } finally {
      setLoading(false);
    }
  }, [dates]);

  useEffect(() => { fetchData(); }, [fetchData]);

  return (
    <Spin spinning={loading}>
      <Space style={{ marginBottom: 24 }}>
        <RangePicker value={dates} onChange={(v) => setDates(v as [Dayjs, Dayjs] | null)} />
      </Space>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="销售总额"
              value={data?.totalSales ?? 0}
              precision={2}
              prefix="¥"
              suffix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="订单数"
              value={data?.totalOrders ?? 0}
              prefix={<ShoppingCartOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="售票张数"
              value={data?.totalTickets ?? 0}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="平均上座率"
              value={data ? (data.avgOccupancy * 100).toFixed(1) : 0}
              suffix="%"
              prefix={<PercentageOutlined />}
            />
          </Card>
        </Col>
      </Row>

      {data?.topPlay && (
        <Card
          title={<span><FireOutlined style={{ color: '#ff4d4f', marginRight: 8 }} />热卖剧目</span>}
          style={{ maxWidth: 400 }}
        >
          <Title level={4} style={{ margin: 0 }}>{data.topPlay.playName}</Title>
          <Text type="secondary">销售额：¥{data.topPlay.sales.toFixed(2)}</Text>
        </Card>
      )}
      {!data?.topPlay && !loading && (
        <Empty description="暂无数据" />
      )}
    </Spin>
  );
}

/** 剧目销售排名面板 */
function PlayRankingTab() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<PlayRankingItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize] = useState(10);
  const [dates, setDates] = useState<[Dayjs, Dayjs] | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const params: { page: number; pageSize: number; startDate?: string; endDate?: string } = { page, pageSize };
      if (dates) {
        params.startDate = dates[0].format('YYYY-MM-DD');
        params.endDate = dates[1].format('YYYY-MM-DD');
      }
      const res = await getPlayRanking(params);
      setData(res.data.list);
      setTotal(res.data.total);
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, dates]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const columns: ColumnsType<PlayRankingItem> = [
    { title: '排名', key: 'rank', width: 70, render: (_: unknown, __: unknown, idx: number) => (page - 1) * pageSize + idx + 1 },
    { title: '剧目名称', dataIndex: 'playName', key: 'playName' },
    { title: '演出场次', dataIndex: 'showCount', key: 'showCount' },
    { title: '总座位数', dataIndex: 'totalTickets', key: 'totalTickets' },
    { title: '售票数', dataIndex: 'soldTickets', key: 'soldTickets' },
    {
      title: '上座率', dataIndex: 'occupancy', key: 'occupancy',
      render: (v: number) => `${(v * 100).toFixed(1)}%`,
      sorter: (a, b) => a.occupancy - b.occupancy,
    },
    {
      title: '销售额', dataIndex: 'sales', key: 'sales',
      render: (v: number) => `¥${v.toFixed(2)}`,
      sorter: (a, b) => a.sales - b.sales,
    },
  ];

  return (
    <Spin spinning={loading}>
      <Space style={{ marginBottom: 16 }}>
        <RangePicker value={dates} onChange={(v) => setDates(v as [Dayjs, Dayjs] | null)} />
      </Space>
      <Table
        rowKey="playId"
        columns={columns}
        dataSource={data}
        pagination={{ current: page, pageSize, total, onChange: setPage }}
      />
    </Spin>
  );
}

/** 剧院业绩面板 */
function StudioPerformanceTab() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<StudioPerformanceItem[]>([]);
  const [dates, setDates] = useState<[Dayjs, Dayjs] | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const params: Record<string, string> = {};
      if (dates) {
        params.startDate = dates[0].format('YYYY-MM-DD');
        params.endDate = dates[1].format('YYYY-MM-DD');
      }
      const res = await getStudioPerformance(params);
      setData(res.data.list);
    } finally {
      setLoading(false);
    }
  }, [dates]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const columns: ColumnsType<StudioPerformanceItem> = [
    { title: '演出厅', dataIndex: 'studioName', key: 'studioName' },
    { title: '演出场次', dataIndex: 'showCount', key: 'showCount' },
    { title: '总座位数', dataIndex: 'totalSeats', key: 'totalSeats' },
    { title: '售出座位', dataIndex: 'soldSeats', key: 'soldSeats' },
    {
      title: '上座率', dataIndex: 'occupancy', key: 'occupancy',
      render: (v: number) => `${(v * 100).toFixed(1)}%`,
      sorter: (a, b) => a.occupancy - b.occupancy,
    },
    {
      title: '销售额', dataIndex: 'sales', key: 'sales',
      render: (v: number) => `¥${v.toFixed(2)}`,
      sorter: (a, b) => a.sales - b.sales,
    },
  ];

  return (
    <Spin spinning={loading}>
      <Space style={{ marginBottom: 16 }}>
        <RangePicker value={dates} onChange={(v) => setDates(v as [Dayjs, Dayjs] | null)} />
      </Space>
      <Table rowKey="studioId" columns={columns} dataSource={data} pagination={false} />
    </Spin>
  );
}

/** 售票员业绩面板 */
function EmployeeSalesTab() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<EmployeeSalesItem[]>([]);
  const [dates, setDates] = useState<[Dayjs, Dayjs] | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const params: Record<string, string> = {};
      if (dates) {
        params.startDate = dates[0].format('YYYY-MM-DD');
        params.endDate = dates[1].format('YYYY-MM-DD');
      }
      const res = await getEmployeeSales(params);
      setData(res.data.list);
    } finally {
      setLoading(false);
    }
  }, [dates]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const columns: ColumnsType<EmployeeSalesItem> = [
    { title: '售票员', dataIndex: 'employeeName', key: 'employeeName' },
    { title: '订单数', dataIndex: 'orderCount', key: 'orderCount', sorter: (a, b) => a.orderCount - b.orderCount },
    {
      title: '销售总额', dataIndex: 'totalAmount', key: 'totalAmount',
      render: (v: number) => `¥${v.toFixed(2)}`,
      sorter: (a, b) => a.totalAmount - b.totalAmount,
    },
    { title: '退票数', dataIndex: 'refundCount', key: 'refundCount' },
    {
      title: '退票金额', dataIndex: 'refundAmount', key: 'refundAmount',
      render: (v: number) => `¥${v.toFixed(2)}`,
    },
  ];

  return (
    <Spin spinning={loading}>
      <Space style={{ marginBottom: 16 }}>
        <RangePicker value={dates} onChange={(v) => setDates(v as [Dayjs, Dayjs] | null)} />
      </Space>
      <Table rowKey="employeeId" columns={columns} dataSource={data} pagination={false} />
    </Spin>
  );
}

/** 财务统计页主组件 */
function FinancePage() {
  const tabItems = [
    { key: 'overview', label: '概览', children: <OverviewTab /> },
    { key: 'play-ranking', label: '剧目销售排名', children: <PlayRankingTab /> },
    { key: 'studio-performance', label: '剧院业绩', children: <StudioPerformanceTab /> },
    { key: 'employee-sales', label: '售票员业绩', children: <EmployeeSalesTab /> },
  ];

  return (
    <div>
      <Title level={3} style={{ marginBottom: 24 }}>财务统计</Title>
      <Tabs defaultActiveKey="overview" items={tabItems} />
    </div>
  );
}

export default FinancePage;
