// 角色管理列表页

import { useState } from 'react';
import { message, Tag } from 'antd';
import PageTable from '@/components/common/PageTable';
import { getRoles, deleteRole } from '@/services/admin/role';
import RoleDetailModal from './Detail';
import type { Role } from '@/services/admin/role';

/** 将 roles 转为扁平列表（配合 PageTable 的泛型约束） */
interface RoleRow {
  id: number;
  name: string;
  resourceCount: number;
  resourceNames: string;
}

/** 表格列定义 */
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '角色名称', dataIndex: 'name', key: 'name', width: 120 },
  {
    title: '资源数',
    dataIndex: 'resourceCount',
    key: 'resourceCount',
    width: 80,
  },
  {
    title: '关联资源',
    dataIndex: 'resourceNames',
    key: 'resourceNames',
    render: (v: string) =>
      v ? v.split(',').map((n) => <Tag key={n}>{n}</Tag>) : null,
  },
];

function RoleListPage() {
  const [list, setList] = useState<RoleRow[]>([]);
  const [loading, setLoading] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [editingRole, setEditingRole] = useState<Role | null>(null);

  /** 加载角色列表 */
  const fetchList = async () => {
    setLoading(true);
    try {
      const res = await getRoles();
      const rows: RoleRow[] = res.data.list.map((r) => ({
        id: r.id,
        name: r.name,
        resourceCount: r.resources?.length || 0,
        resourceNames: r.resources?.map((r) => r.name).join(',') || '',
      }));
      setList(rows);
    } catch {
      setList([]);
    } finally {
      setLoading(false);
    }
  };

  /** 新增 */
  const handleAdd = () => {
    setEditingRole(null);
    setDetailOpen(true);
  };

  /** 编辑 */
  const handleEdit = async (record: RoleRow) => {
    // 加载完整角色数据（含 resources）
    try {
      const res = await getRoles();
      const role = res.data.list.find((r) => r.id === record.id) || null;
      setEditingRole(role);
      setDetailOpen(true);
    } catch {
      message.error('加载角色详情失败');
    }
  };

  /** 删除 */
  const handleDelete = async (id: number) => {
    try {
      await deleteRole(id);
      message.success('删除成功');
      fetchList();
    } catch {
      // 错误已在拦截器处理
    }
  };

  /** 弹窗关闭 */
  const handleDetailClose = () => {
    setDetailOpen(false);
    setEditingRole(null);
  };

  /** 新增/修改成功 */
  const handleDetailSuccess = () => {
    handleDetailClose();
    fetchList();
  };

  /** 分页（角色数量少，前端假分页） */
  const fakePagination = { page: 1, pageSize: 50, total: list.length };

  return (
    <>
      <PageTable<RoleRow>
        columns={columns}
        dataSource={list}
        loading={loading}
        pagination={fakePagination}
        keyword=""
        onSearch={() => {}}
        onAdd={handleAdd}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={() => {}}
        addText="新增角色"
      />

      <RoleDetailModal
        open={detailOpen}
        role={editingRole}
        onClose={handleDetailClose}
        onSuccess={handleDetailSuccess}
      />
    </>
  );
}

export default RoleListPage;
