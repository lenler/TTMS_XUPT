// 剧目新增/修改 Modal 表单

import { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Select, message } from 'antd';
import { createPlay, updatePlay, getDicts } from '@/services/admin/play';
import type { Play } from '@/types/models';

interface PlayDetailModalProps {
  open: boolean;
  play: Play | null;
  onClose: () => void;
  onSuccess: () => void;
}

interface PlayFormValues {
  name: string;
  typeId: number;
  langId: number;
  duration: number;
  basePrice: number;
  poster: string;
  video: string;
  introduction: string;
}

function PlayDetailModal({ open, play, onClose, onSuccess }: PlayDetailModalProps) {
  const [form] = Form.useForm<PlayFormValues>();
  const [saving, setSaving] = useState(false);
  const [typeOptions, setTypeOptions] = useState<{ value: number; label: string }[]>([]);
  const [langOptions, setLangOptions] = useState<{ value: number; label: string }[]>([]);
  const isEdit = play !== null;

  useEffect(() => {
    getDicts('type').then((res) =>
      setTypeOptions(res.data.list.map((d) => ({ value: d.id, label: d.name })))
    ).catch(() => {});
    getDicts('lang').then((res) =>
      setLangOptions(res.data.list.map((d) => ({ value: d.id, label: d.name })))
    ).catch(() => {});
  }, []);

  useEffect(() => {
    if (open) {
      if (play) {
        form.setFieldsValue({
          name: play.name,
          typeId: play.typeId,
          langId: play.langId,
          duration: play.duration,
          basePrice: play.basePrice,
          poster: play.poster,
          video: play.video,
          introduction: play.introduction,
        });
      } else {
        form.resetFields();
      }
    }
  }, [open, play, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSaving(true);
      if (isEdit) {
        await updatePlay(play!.id, values);
        message.success('修改成功');
      } else {
        await createPlay(values);
        message.success('新增成功');
      }
      onSuccess();
    } catch {
      // 校验失败或接口报错
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '修改剧目' : '新增剧目'}
      open={open}
      onOk={handleSubmit}
      onCancel={onClose}
      confirmLoading={saving}
      destroyOnClose
      width={640}
    >
      <Form form={form} layout="vertical" initialValues={{ duration: 120, basePrice: 100 }}>
        <Form.Item
          name="name"
          label="剧目名称"
          rules={[{ required: true, message: '请输入剧目名称' }]}
        >
          <Input placeholder="例：雷雨" />
        </Form.Item>
        <div style={{ display: 'flex', gap: 16 }}>
          <Form.Item
            name="typeId"
            label="剧目类型"
            rules={[{ required: true, message: '请选择类型' }]}
            style={{ flex: 1 }}
          >
            <Select placeholder="选择类型" options={typeOptions} />
          </Form.Item>
          <Form.Item
            name="langId"
            label="语种"
            rules={[{ required: true, message: '请选择语种' }]}
            style={{ flex: 1 }}
          >
            <Select placeholder="选择语种" options={langOptions} />
          </Form.Item>
        </div>
        <div style={{ display: 'flex', gap: 16 }}>
          <Form.Item
            name="duration"
            label="演出时长（分钟）"
            rules={[{ required: true, message: '请输入时长' }]}
            style={{ flex: 1 }}
          >
            <InputNumber min={1} max={600} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="basePrice"
            label="基准票价"
            rules={[{ required: true, message: '请输入票价' }]}
            style={{ flex: 1 }}
          >
            <InputNumber min={0} precision={2} prefix="¥" style={{ width: '100%' }} />
          </Form.Item>
        </div>
        <Form.Item name="poster" label="海报地址">
          <Input placeholder="例：/images/plays/thunderstorm.jpg" />
        </Form.Item>
        <Form.Item name="video" label="宣传片地址">
          <Input placeholder="例：/videos/plays/thunderstorm.mp4（选填）" />
        </Form.Item>
        <Form.Item name="introduction" label="剧情简介">
          <Input.TextArea rows={4} placeholder="剧情简介（选填）" />
        </Form.Item>
      </Form>
    </Modal>
  );
}

export default PlayDetailModal;
