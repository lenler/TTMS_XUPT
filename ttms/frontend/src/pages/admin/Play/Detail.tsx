// 剧目新增/修改 Modal 表单

import { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Select, Upload, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import { createPlay, updatePlay, getDicts } from '@/services/admin/play';
import type { Play } from '@/types/models';
import type { UploadFile, RcFile } from 'antd/es/upload';

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
  const [posterFile, setPosterFile] = useState<UploadFile[]>([]);
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
        // 编辑时，如果已有海报URL则展示预览
        setPosterFile(
          play.poster
            ? [{ uid: '-1', name: '海报', status: 'done', url: play.poster }]
            : []
        );
      } else {
        form.resetFields();
        setPosterFile([]);
      }
    } else {
      setPosterFile([]);
    }
  }, [open, play, form]);

  /** 上传前校验 */
  const beforeUpload = (file: RcFile) => {
    const isImage = file.type.startsWith('image/');
    if (!isImage) {
      message.error('只能上传图片文件');
      return Upload.LIST_IGNORE;
    }
    const isLt5M = file.size / 1024 / 1024 < 5;
    if (!isLt5M) {
      message.error('图片大小不能超过5MB');
      return Upload.LIST_IGNORE;
    }
    return true;
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      // 优先使用上传的海报URL
      const posterUrl =
        posterFile.length > 0 && posterFile[0].response?.url
          ? posterFile[0].response.url
          : posterFile.length > 0 && posterFile[0].url
            ? posterFile[0].url
            : values.poster || '';
      setSaving(true);
      const payload = { ...values, poster: posterUrl };
      if (isEdit) {
        await updatePlay(play!.id, payload);
        message.success('修改成功');
      } else {
        await createPlay(payload);
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
        <Form.Item name="poster" label="海报图片">
          <div className="flex items-center gap-3">
            <Upload
              accept="image/*"
              maxCount={1}
              fileList={posterFile}
              beforeUpload={beforeUpload}
              onChange={({ fileList }) => setPosterFile(fileList)}
              action="/admin/api/upload"
              name="file"
              showUploadList={{ showPreviewIcon: true, showRemoveIcon: true }}
              listType="picture-card"
            >
              {posterFile.length === 0 && (
                <div>
                  <UploadOutlined />
                  <div className="mt-2">上传海报</div>
                </div>
              )}
            </Upload>
            <span className="text-light-ink text-xs">或填写URL：</span>
          </div>
          <Input placeholder="例：/images/plays/thunderstorm.jpg" className="mt-2" />
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
