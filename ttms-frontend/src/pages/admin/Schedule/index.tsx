// 演出计划管理列表页

import { useState, useEffect } from 'react';
import { Select, message } from 'antd';
import PageTable from '@/components/common/PageTable';
import { useCRUD } from '@/hooks/useCRUD';
import { getSchedules, deleteSchedule } from '@/services/admin/schedule';
import { getPlays } from '@/services/admin/play';
import { getStudios } from '@/services/admin/studio';
import ScheduleDetailModal from './Detail';
import type { Schedule } from '@/types/models';

/** 表格列定义 */
const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '剧目', dataIndex: 'playName', key: 'playName' },
  { title: '演出厅', dataIndex: 'studioName', key: 'studioName', width: 100 },
  { title: '演出时间', dataIndex: 'showTime', key: 'showTime', width: 180 },
  {
    title: '票价',
    dataIndex: 'ticketPrice',
    key: 'ticketPrice',
    width: 100,
    render: (v: number) => `¥${v.toFixed(2)}`,
  },
];

function ScheduleListPage() {
  const { list, loading, pagination, keyword, setKeyword, setPage, setPageSize, refresh, extraParams, setExtraParams } =
    useCRUD<Schedule>(getSchedules);

  const [detailOpen, setDetailOpen] = useState(false);
  const [editingSchedule, setEditingSchedule] = useState<Schedule | null>(null);
  const [playOptions, setPlayOptions] = useState<{ value: number; label: string }[]>([]);
  const [studioOptions, setStudioOptions] = useState<{ value: number; label: string }[]>([]);

  /** 加载下拉选项 */
  useEffect(() => {
    getPlays({ page: 1, pageSize: 100 }).then((res) =>
      setPlayOptions(res.data.list.map((p) => ({ value: p.id, label: p.name })))
    ).catch(() => {});
    getStudios({ page: 1, pageSize: 100 }).then((res) =>
      setStudioOptions(res.data.list.map((s) => ({ value: s.id, label: s.name })))
    ).catch(() => {});
  }, []);

  const handleAdd = () => {
    setEditingSchedule(null);
    setDetailOpen(true);
  };

  const handleEdit = (record: Schedule) => {
    setEditingSchedule(record);
    setDetailOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteSchedule(id);
      message.success('删除成功');
      refresh();
    } catch {
      // 已处理
    }
  };

  const handleDetailClose = () => {
    setDetailOpen(false);
    setEditingSchedule(null);
  };

  const handleDetailSuccess = () => {
    handleDetailClose();
    refresh();
  };

  const handlePageChange = (page: number, pageSize: number) => {
    setPage(page);
    setPageSize(pageSize);
    refresh({ page, keyword });
  };

  return (
    <>
      {/* 筛选栏 */}
      <div style={{ marginBottom: 16, display: 'flex', gap: 12 }}>
        <Select
          placeholder="按剧目筛选"
          allowClear
          style={{ width: 200 }}
          value={extraParams.playId || undefined}
          onChange={(val) =>
            setExtraParams((prev) => ({ ...prev, playId: val || '' }))
          }
          options={playOptions}
        />
        <Select
          placeholder="按演出厅筛选"
          allowClear
          style={{ width: 200 }}
          value={extraParams.studioId || undefined}
          onChange={(val) =>
            setExtraParams((prev) => ({ ...prev, studioId: val || '' }))
          }
          options={studioOptions}
        />
      </div>

      <PageTable<Schedule>
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
        addText="新增演出计划"
      />

      <ScheduleDetailModal
        open={detailOpen}
        schedule={editingSchedule}
        playOptions={playOptions}
        studioOptions={studioOptions}
        onClose={handleDetailClose}
        onSuccess={handleDetailSuccess}
      />
    </>
  );
}

export default ScheduleListPage;
