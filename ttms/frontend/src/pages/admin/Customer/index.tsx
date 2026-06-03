// 观众管理列表页

import { useState } from 'react';
import { Button, Drawer, Descriptions, Popconfirm, message } from 'antd';
import PageTable from '@/components/common/PageTable';
import StatusTag from '@/components/common/StatusTag';
import { useCRUD } from '@/hooks/useCRUD';
import { getCustomers, updateCustomerStatus } from '@/services/admin/customer';
import type { Customer } from '@/types/models';

/** 表格列定义 */
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '姓名', dataIndex: 'name', key: 'name', width: 80 },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender',
    width: 60,
    render: (v: number) => (v === 1 ? '男' : '女'),
  },
  { title: '电话', dataIndex: 'phone', key: 'phone', width: 130 },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '用户名', dataIndex: 'username', key: 'username', width: 100 },
  {
    title: '余额',
    dataIndex: 'balance',
    key: 'balance',
    width: 100,
    render: (v: number) => `¥${(v ?? 0).toFixed(2)}`,
  },
  {
    title: '累计充值',
    dataIndex: 'rechargeTotal',
    key: 'rechargeTotal',
    width: 110,
    render: (v: number) => `¥${(v ?? 0).toFixed(2)}`,
  },
  {
    title: '充值次数',
    dataIndex: 'rechargeCount',
    key: 'rechargeCount',
    width: 90,
    render: (v: number) => `${v ?? 0}次`,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 80,
    render: (v: number) => <StatusTag status={v} />,
  },
];

function CustomerListPage() {
  const { list, loading, pagination, keyword, setKeyword, setPage, setPageSize, refresh } =
    useCRUD<Customer>(getCustomers);

  const [drawerOpen, setDrawerOpen] = useState(false);
  const [viewCustomer, setViewCustomer] = useState<Customer | null>(null);

  /** 查看详情 */
  const handleView = (record: Customer) => {
    setViewCustomer(record);
    setDrawerOpen(true);
  };

  /** 封禁/解封 */
  const handleToggleStatus = async (record: Customer) => {
    try {
      const newStatus = record.status === 1 ? 0 : 1;
      await updateCustomerStatus(record.id, newStatus);
      message.success(newStatus === 1 ? '解封成功' : '封禁成功');
      refresh();
    } catch {
      // 错误已在拦截器处理
    }
  };

  /** 分页变化 */
  const handlePageChange = (page: number, pageSize: number) => {
    setPage(page);
    setPageSize(pageSize);
    refresh({ page, keyword });
  };

  return (
    <>
      <PageTable<Customer>
        columns={columns}
        dataSource={list}
        loading={loading}
        pagination={pagination}
        keyword={keyword}
        onSearch={setKeyword}
        onPageChange={handlePageChange}
        rowKey="id"
        // 自定义操作列渲染（覆盖默认的编辑/删除）
        renderActions={(record) => (
          <>
            <Button type="link" size="small" onClick={() => handleView(record)}>
              查看
            </Button>
            <Popconfirm
              title={record.status === 1 ? '确定封禁该观众？' : '确定解封该观众？'}
              onConfirm={() => handleToggleStatus(record)}
            >
              <Button type="link" size="small" danger={record.status === 1}>
                {record.status === 1 ? '封禁' : '解封'}
              </Button>
            </Popconfirm>
          </>
        )}
      />

      {/* 查看详情 Drawer */}
      <Drawer
        title="观众详情"
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        width={480}
      >
        {viewCustomer && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="ID">{viewCustomer.id}</Descriptions.Item>
            <Descriptions.Item label="姓名">{viewCustomer.name}</Descriptions.Item>
            <Descriptions.Item label="性别">
              {viewCustomer.gender === 1 ? '男' : '女'}
            </Descriptions.Item>
            <Descriptions.Item label="电话">{viewCustomer.phone}</Descriptions.Item>
            <Descriptions.Item label="邮箱">{viewCustomer.email}</Descriptions.Item>
            <Descriptions.Item label="用户名">{viewCustomer.username}</Descriptions.Item>
            <Descriptions.Item label="账户余额">
              ¥{(viewCustomer.balance ?? 0).toFixed(2)}
            </Descriptions.Item>
            <Descriptions.Item label="累计充值">
              ¥{(viewCustomer.rechargeTotal ?? 0).toFixed(2)}
            </Descriptions.Item>
            <Descriptions.Item label="充值次数">
              {viewCustomer.rechargeCount ?? 0}次
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              <StatusTag status={viewCustomer.status} />
            </Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>
    </>
  );
}

export default CustomerListPage;
