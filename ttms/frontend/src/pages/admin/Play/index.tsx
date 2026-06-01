// 剧目管理列表页

import { useState, useEffect } from 'react';
import { Select, message } from 'antd';
import PageTable from '@/components/common/PageTable';
import { useCRUD } from '@/hooks/useCRUD';
import { getPlays, deletePlay, getDicts } from '@/services/admin/play';
import PlayDetailModal from './Detail';
import type { Play } from '@/types/models';

/** 表格列定义 */
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '类型', dataIndex: 'typeName', key: 'typeName', width: 80 },
  { title: '语种', dataIndex: 'langName', key: 'langName', width: 80 },
  { title: '时长(分钟)', dataIndex: 'duration', key: 'duration', width: 100 },
  {
    title: '基准票价',
    dataIndex: 'basePrice',
    key: 'basePrice',
    width: 100,
    render: (v: number) => `¥${v.toFixed(2)}`,
  },
];

function PlayListPage() {
  const { list, loading, pagination, keyword, setKeyword, setPage, setPageSize, refresh, extraParams, setExtraParams } =
    useCRUD<Play>(getPlays);

  const [detailOpen, setDetailOpen] = useState(false);
  const [editingPlay, setEditingPlay] = useState<Play | null>(null);
  const [typeOptions, setTypeOptions] = useState<{ value: number; label: string }[]>([]);
  const [langOptions, setLangOptions] = useState<{ value: number; label: string }[]>([]);

  /** 加载字典 */
  useEffect(() => {
    getDicts('type')
      .then((res) => setTypeOptions(res.data.list.map((d) => ({ value: d.id, label: d.name }))))
      .catch(() => {});
    getDicts('lang')
      .then((res) => setLangOptions(res.data.list.map((d) => ({ value: d.id, label: d.name }))))
      .catch(() => {});
  }, []);

  /** 新增 */
  const handleAdd = () => {
    setEditingPlay(null);
    setDetailOpen(true);
  };

  /** 编辑 */
  const handleEdit = (record: Play) => {
    setEditingPlay(record);
    setDetailOpen(true);
  };

  /** 删除 */
  const handleDelete = async (id: number) => {
    try {
      await deletePlay(id);
      message.success('删除成功');
      refresh();
    } catch {
      // 已在拦截器处理
    }
  };

  /** 弹窗关闭 */
  const handleDetailClose = () => {
    setDetailOpen(false);
    setEditingPlay(null);
  };

  /** 添加/修改成功 */
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
      {/* 筛选栏：类型 + 语种 */}
      <div style={{ marginBottom: 16, display: 'flex', gap: 12 }}>
        <Select
          placeholder="剧目类型"
          allowClear
          style={{ width: 140 }}
          value={extraParams.type || undefined}
          onChange={(val) =>
            setExtraParams((prev) => ({
              ...prev,
              type: val || '',
            }))
          }
          options={typeOptions}
        />
        <Select
          placeholder="语种"
          allowClear
          style={{ width: 140 }}
          value={extraParams.lang || undefined}
          onChange={(val) =>
            setExtraParams((prev) => ({
              ...prev,
              lang: val || '',
            }))
          }
          options={langOptions}
        />
      </div>

      <PageTable<Play>
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
        addText="新增剧目"
      />

      <PlayDetailModal
        open={detailOpen}
        play={editingPlay}
        onClose={handleDetailClose}
        onSuccess={handleDetailSuccess}
      />
    </>
  );
}

export default PlayListPage;
