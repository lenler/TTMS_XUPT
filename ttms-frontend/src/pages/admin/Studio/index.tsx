// 演出厅管理列表页

import { Button, Form, Input, InputNumber, Modal, Popconfirm, Space, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { DeleteOutlined, EditOutlined, EyeOutlined } from '@ant-design/icons';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PageTable from '@/components/common/PageTable';
import { useCRUD } from '@/hooks/useCRUD';
import { createStudio, deleteStudio, getStudios, updateStudio } from '@/services/admin/studio';
import type { Studio } from '@/types/models';

type StudioFormValues = Omit<Studio, 'id' | 'status'>;

/** 渲染演出厅管理列表，支持查询、新增、修改和删除 */
function StudioListPage() {
  const navigate = useNavigate();
  const [form] = Form.useForm<StudioFormValues>();
  const [messageApi, contextHolder] = message.useMessage();
  const [modalOpen, setModalOpen] = useState(false);
  const [editingStudio, setEditingStudio] = useState<Studio | null>(null);
  const { list, loading, page, pageSize, total, keyword, loadData, search, changePage } = useCRUD<Studio>({
    fetchList: getStudios,
  });

  /** 打开新增演出厅弹窗 */
  const openCreateModal = () => {
    setEditingStudio(null);
    form.resetFields();
    setModalOpen(true);
  };

  /** 打开编辑演出厅弹窗 */
  const openEditModal = (studio: Studio) => {
    setEditingStudio(studio);
    form.setFieldsValue({
      name: studio.name,
      rowCount: studio.rowCount,
      colCount: studio.colCount,
      introduction: studio.introduction,
    });
    setModalOpen(true);
  };

  /** 提交新增或修改表单 */
  const submitForm = async () => {
    const values = await form.validateFields();
    if (editingStudio) {
      await updateStudio(editingStudio.id, values);
      messageApi.success('演出厅修改成功');
    } else {
      await createStudio(values);
      messageApi.success('演出厅新增成功');
    }
    setModalOpen(false);
    await loadData();
  };

  /** 删除指定演出厅并刷新列表 */
  const removeStudio = async (id: number) => {
    await deleteStudio(id);
    messageApi.success('演出厅删除成功');
    await loadData();
  };

  const columns: TableProps<Studio>['columns'] = [
    {
      title: '演出厅名称',
      dataIndex: 'name',
      key: 'name',
      width: 180,
    },
    {
      title: '座位规模',
      key: 'seatCount',
      width: 160,
      render: (_, record) => `${record.rowCount} 行 x ${record.colCount} 列`,
    },
    {
      title: '座位总数',
      key: 'totalSeat',
      width: 120,
      render: (_, record) => record.rowCount * record.colCount,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) => (status === 1 ? <Tag color="green">启用</Tag> : <Tag color="red">停用</Tag>),
    },
    {
      title: '简介',
      dataIndex: 'introduction',
      key: 'introduction',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 240,
      render: (_, record) => (
        <Space>
          <Button icon={<EyeOutlined />} onClick={() => navigate(`/admin/studio/${record.id}`)}>
            详情
          </Button>
          <Button icon={<EditOutlined />} onClick={() => openEditModal(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确认删除该演出厅？"
            description="删除后本地模拟数据将移除该记录。"
            okText="确认"
            cancelText="取消"
            onConfirm={() => removeStudio(record.id)}
          >
            <Button danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      {contextHolder}
      <PageTable<Studio>
        title="演出厅管理"
        rowKey="id"
        columns={columns}
        dataSource={list}
        loading={loading}
        total={total}
        page={page}
        pageSize={pageSize}
        keyword={keyword}
        addText="新增演出厅"
        onSearch={search}
        onRefresh={() => void loadData()}
        onAdd={openCreateModal}
        onPageChange={changePage}
      />

      <Modal
        title={editingStudio ? '编辑演出厅' : '新增演出厅'}
        open={modalOpen}
        okText="保存"
        cancelText="取消"
        onOk={() => void submitForm()}
        onCancel={() => setModalOpen(false)}
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="演出厅名称" rules={[{ required: true, message: '请输入演出厅名称' }]}>
            <Input placeholder="例如：一号激光厅" maxLength={30} />
          </Form.Item>
          <Form.Item name="rowCount" label="座位行数" rules={[{ required: true, message: '请输入座位行数' }]}>
            <InputNumber min={1} max={30} precision={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="colCount" label="座位列数" rules={[{ required: true, message: '请输入座位列数' }]}>
            <InputNumber min={1} max={40} precision={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="introduction"
            label="演出厅简介"
            rules={[{ required: true, message: '请输入演出厅简介' }]}
          >
            <Input.TextArea rows={4} maxLength={200} showCount placeholder="请输入演出厅特点、用途或设备说明" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}

export default StudioListPage;
