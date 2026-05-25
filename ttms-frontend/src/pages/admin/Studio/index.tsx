// 演出厅管理列表页

import { useState } from 'react';
import { message } from 'antd';
import PageTable from '@/components/common/PageTable';
import { useCRUD } from '@/hooks/useCRUD';
import { getStudios, deleteStudio } from '@/services/admin/studio';
import StudioDetailModal from './Detail';
import type { Studio } from '@/types/models';

/** 表格列定义 */
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '行数', dataIndex: 'rowCount', key: 'rowCount', width: 80 },
  { title: '列数', dataIndex: 'colCount', key: 'colCount', width: 80 },
  { title: '简介', dataIndex: 'introduction', key: 'introduction', ellipsis: true },
];

function StudioListPage() {
  const { list, loading, pagination, keyword, setKeyword, setPage, setPageSize, refresh } =
    useCRUD<Studio>(getStudios);

  const [detailOpen, setDetailOpen] = useState(false);
  const [editingStudio, setEditingStudio] = useState<Studio | null>(null);

  /** 新增 */
  const handleAdd = () => {
    setEditingStudio(null);
    setDetailOpen(true);
  };

  /** 编辑 */
  const handleEdit = (record: Studio) => {
    setEditingStudio(record);
    setDetailOpen(true);
  };

  /** 删除 */
  const handleDelete = async (id: number) => {
    try {
      await deleteStudio(id);
      message.success('删除成功');
      refresh();
    } catch {
      // 错误已在 request 拦截器统一处理
    }
  };

  /** 详情弹窗关闭 */
  const handleDetailClose = () => {
    setDetailOpen(false);
    setEditingStudio(null);
  };

  /** 分页变化：更新状态并重新请求 */
  const handlePageChange = (page: number, pageSize: number) => {
    setPage(page);
    setPageSize(pageSize);
    refresh({ page, keyword });
  };

  /** 新增/修改成功 */
  const handleDetailSuccess = () => {
    handleDetailClose();
    refresh();
  };

  return (
    <>
      <PageTable<Studio>
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
        addText="新增演出厅"
      />
      <StudioDetailModal
        open={detailOpen}
        studio={editingStudio}
        onClose={handleDetailClose}
        onSuccess={handleDetailSuccess}
      />
    </>
  );
}

export default StudioListPage;
