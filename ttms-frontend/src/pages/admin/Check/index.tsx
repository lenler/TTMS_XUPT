// 验票管理页：验票操作 + 验票记录

import { useState } from 'react';
import { Input, Button, Card, Result, Descriptions, Tag } from 'antd';
import { SearchOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import PageTable from '@/components/common/PageTable';
import { useCRUD } from '@/hooks/useCRUD';
import { getChecks, verifyTicket } from '@/services/admin/check';
import type { VerifyResult, CheckRecord } from '@/services/admin/check';

/** 表格列定义 */
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '票ID', dataIndex: 'ticketId', key: 'ticketId', width: 80 },
  {
    title: '座位',
    key: 'seat',
    width: 80,
    render: (_: unknown, r: CheckRecord) => `${r.seatRow}排${r.seatCol}座`,
  },
  { title: '剧目', dataIndex: 'playName', key: 'playName', width: 120 },
  { title: '演出厅', dataIndex: 'studioName', key: 'studioName', width: 80 },
  { title: '演出时间', dataIndex: 'showTime', key: 'showTime', width: 160 },
  { title: '验票时间', dataIndex: 'verifyTime', key: 'verifyTime', width: 160 },
  { title: '操作员', dataIndex: 'operatorName', key: 'operatorName', width: 100 },
  {
    title: '结果',
    dataIndex: 'result',
    key: 'result',
    width: 80,
    render: (v: string) =>
      v === 'passed' ? <Tag color="green">通过</Tag> : <Tag color="red">拒绝</Tag>,
  },
];

function CheckListPage() {
  const { list, loading, pagination, keyword, setKeyword, setPage, setPageSize, refresh } =
    useCRUD<CheckRecord>(getChecks);

  const [ticketIdInput, setTicketIdInput] = useState('');
  const [verifying, setVerifying] = useState(false);
  const [verifyResult, setVerifyResult] = useState<VerifyResult | null>(null);

  /** 分页变化 */
  const handlePageChange = (page: number, pageSize: number) => {
    setPage(page);
    setPageSize(pageSize);
    refresh({ page, keyword });
  };

  /** 执行验票 */
  const handleVerify = async () => {
    const tid = Number(ticketIdInput);
    if (!tid) {
      return;
    }
    setVerifying(true);
    setVerifyResult(null);
    try {
      const res = await verifyTicket(tid);
      setVerifyResult(res.data);
      refresh();
    } catch (err: unknown) {
      // 验票失败返回 20004/20005，由 request 拦截器 message.error
      // 同时尝试从 error 中提取信息展示
      if (err instanceof Error) {
        setVerifyResult({
          ticketId: tid,
          seatRow: 0,
          seatCol: 0,
          playName: '',
          studioName: '',
          showTime: '',
          status: 'rejected',
          message: err.message,
        });
      }
    } finally {
      setVerifying(false);
    }
  };

  return (
    <div>
      {/* 验票操作区 */}
      <Card title="验票操作" style={{ marginBottom: 24 }}>
        <div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
          <Input
            placeholder="输入票ID"
            value={ticketIdInput}
            onChange={(e) => setTicketIdInput(e.target.value)}
            onPressEnter={handleVerify}
            style={{ width: 260 }}
            prefix={<SearchOutlined />}
          />
          <Button type="primary" loading={verifying} onClick={handleVerify}>
            验票
          </Button>
        </div>

        {/* 验票结果 */}
        {verifyResult && (
          <Result
            status={verifyResult.status === 'checked' ? 'success' : 'error'}
            icon={
              verifyResult.status === 'checked' ? (
                <CheckCircleOutlined />
              ) : (
                <CloseCircleOutlined />
              )
            }
            title={verifyResult.status === 'checked' ? '验票通过' : '验票拒绝'}
            subTitle={verifyResult.message}
          >
            {verifyResult.status === 'checked' && (
              <Descriptions column={3} size="small" bordered>
                <Descriptions.Item label="票ID">{verifyResult.ticketId}</Descriptions.Item>
                <Descriptions.Item label="座位">
                  {verifyResult.seatRow}排{verifyResult.seatCol}座
                </Descriptions.Item>
                <Descriptions.Item label="状态">{verifyResult.status}</Descriptions.Item>
                <Descriptions.Item label="剧目">{verifyResult.playName}</Descriptions.Item>
                <Descriptions.Item label="演出厅">{verifyResult.studioName}</Descriptions.Item>
                <Descriptions.Item label="演出时间">{verifyResult.showTime}</Descriptions.Item>
              </Descriptions>
            )}
          </Result>
        )}
      </Card>

      {/* 验票记录 */}
      <Card title="验票记录">
        <PageTable<CheckRecord>
          columns={columns}
          dataSource={list}
          loading={loading}
          pagination={pagination}
          keyword={keyword}
          onSearch={setKeyword}
          onPageChange={handlePageChange}
          rowKey="id"
        />
      </Card>
    </div>
  );
}

export default CheckListPage;
