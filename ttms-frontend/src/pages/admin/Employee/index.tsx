// 员工管理列表页

import { useState } from 'react';
import { Select, message } from 'antd';
import PageTable from '@/components/common/PageTable';
import { useCRUD } from '@/hooks/useCRUD';
import { getEmployees, deleteEmployee } from '@/services/admin/employee';
import EmployeeDetailModal from './Detail';
import type { Employee } from '@/types/models';

/** 岗位下拉选项 */
const positionOptions = [
  { value: 1, label: '售票员' },
  { value: 2, label: '运营经理' },
  { value: 3, label: '系统管理员' },
  { value: 4, label: '会计' },
  { value: 5, label: '财务经理' },
  { value: 6, label: '场务员' },
  { value: 7, label: '设备运维' },
];

/** 表格列定义 */
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '工号', dataIndex: 'employeeNo', key: 'employeeNo', width: 100 },
  { title: '姓名', dataIndex: 'name', key: 'name', width: 80 },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender',
    width: 60,
    render: (v: number) => (v === 1 ? '男' : '女'),
  },
  { title: '电话', dataIndex: 'phone', key: 'phone', width: 130 },
  { title: '岗位', dataIndex: 'positionName', key: 'positionName', width: 100 },
];

function EmployeeListPage() {
  const {
    list,
    loading,
    pagination,
    keyword,
    setKeyword,
    setPage,
    setPageSize,
    refresh,
    extraParams,
    setExtraParams,
  } = useCRUD<Employee>(getEmployees);

  const [detailOpen, setDetailOpen] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);

  /** 新增 */
  const handleAdd = () => {
    setEditingEmployee(null);
    setDetailOpen(true);
  };

  /** 编辑 */
  const handleEdit = (record: Employee) => {
    setEditingEmployee(record);
    setDetailOpen(true);
  };

  /** 删除 */
  const handleDelete = async (id: number) => {
    try {
      await deleteEmployee(id);
      message.success('删除成功');
      refresh();
    } catch {
      // 错误已在 request 拦截器统一处理
    }
  };

  /** 弹窗关闭 */
  const handleDetailClose = () => {
    setDetailOpen(false);
    setEditingEmployee(null);
  };

  /** 新增/修改成功 */
  const handleDetailSuccess = () => {
    handleDetailClose();
    refresh();
  };

  /** 分页变化 */
  const handlePageChange = (page: number, pageSize: number) => {
    setPage(page);
    setPageSize(pageSize);
    refresh({ page, keyword });
  };

  return (
    <>
      {/* 筛选栏：按岗位筛选 */}
      <div style={{ marginBottom: 16, display: 'flex', gap: 12 }}>
        <Select
          placeholder="按岗位筛选"
          allowClear
          style={{ width: 160 }}
          value={extraParams.role || undefined}
          onChange={(val) =>
            setExtraParams((prev) => ({ ...prev, role: val || '' }))
          }
          options={positionOptions.map((p) => ({ value: String(p.value), label: p.label }))}
        />
      </div>

      <PageTable<Employee>
        columns={columns}
        dataSource={list}
        loading={loading}
        pagination={pagination}
        keyword={keyword}
        onSearch={setKeyword}
        onAdd={handleAdd}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={handlePageChange}
        addText="新增员工"
      />

      <EmployeeDetailModal
        open={detailOpen}
        employee={editingEmployee}
        onClose={handleDetailClose}
        onSuccess={handleDetailSuccess}
      />
    </>
  );
}

export default EmployeeListPage;
