// 演出厅新增/修改 Modal 表单

import { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, message } from 'antd';
import { createStudio, updateStudio } from '@/services/admin/studio';
import SeatEditor from '@/components/admin/SeatEditor';
import type { Studio } from '@/types/models';

interface StudioDetailModalProps {
  open: boolean;
  studio: Studio | null;  // null = 新增，非 null = 修改
  onClose: () => void;
  onSuccess: () => void;
}

interface StudioFormValues {
  name: string;
  rowCount: number;
  colCount: number;
  introduction: string;
}

function StudioDetailModal({ open, studio, onClose, onSuccess }: StudioDetailModalProps) {
  const [form] = Form.useForm<StudioFormValues>();
  const [saving, setSaving] = useState(false);
  const isEdit = studio !== null;

  /** 弹窗打开/studio 变化时回填表单 */
  useEffect(() => {
    if (open) {
      if (studio) {
        form.setFieldsValue({
          name: studio.name,
          rowCount: studio.rowCount,
          colCount: studio.colCount,
          introduction: studio.introduction,
        });
      } else {
        form.resetFields();
      }
    }
  }, [open, studio, form]);

  /** 提交 */
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSaving(true);
      if (isEdit) {
        await updateStudio(studio!.id, values);
        message.success('修改成功');
      } else {
        await createStudio(values);
        message.success('新增成功');
      }
      onSuccess();
    } catch {
      // 表单校验失败或接口报错
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '修改演出厅' : '新增演出厅'}
      open={open}
      onOk={handleSubmit}
      onCancel={onClose}
      confirmLoading={saving}
      destroyOnClose
      width={700}
    >
      <Form form={form} layout="vertical" initialValues={{ rowCount: 8, colCount: 10 }}>
        <Form.Item
          name="name"
          label="演出厅名称"
          rules={[{ required: true, message: '请输入名称' }]}
        >
          <Input placeholder="例：1号厅" />
        </Form.Item>
        <Form.Item
          name="rowCount"
          label="座位行数"
          rules={[{ required: true, message: '请输入行数' }]}
        >
          <InputNumber min={1} max={30} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="colCount"
          label="座位列数"
          rules={[{ required: true, message: '请输入列数' }]}
        >
          <InputNumber min={1} max={30} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="introduction" label="简介">
          <Input.TextArea rows={3} placeholder="演出厅简介（选填）" />
        </Form.Item>
      </Form>

      {/* 座位编辑器——仅修改模式显示 */}
      {isEdit && (
        <div style={{ marginTop: 16 }}>
          <SeatEditor studioId={studio!.id} />
        </div>
      )}
    </Modal>
  );
}

export default StudioDetailModal;
