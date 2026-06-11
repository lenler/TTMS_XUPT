// 剧目新增/修改 Modal 表单

import { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Select, Upload, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import type { UploadFile } from 'antd/es/upload';
import { createPlay, updatePlay, getDicts } from '@/services/admin/play';
import request from '@/services/request';
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

  /** 自定义上传：校验后用 axios 发送，正确解析 API 响应 */
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const customUpload = async (options: any) => {
    const { file, onSuccess, onError } = options;

    // 客户端校验
    if (!file.type?.startsWith('image/')) {
      message.error('只能上传图片文件');
      onError?.(new Error('非图片文件'));
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      message.error('图片大小不能超过5MB');
      onError?.(new Error('文件过大'));
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    try {
      const res = await request.post('/admin/api/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      const uploadedUrl: string = res.data?.url || '';
      onSuccess?.({ url: uploadedUrl }, file);
    } catch (err) {
      onError?.(err as Error);
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      // 优先取上传成功的 URL，其次取现有海报 URL，最后取手动输入的 URL
      let posterUrl = values.poster || '';
      if (posterFile.length > 0) {
        const f = posterFile[0];
        const uploaded = (f as UploadFile & { url?: string }).url;
        if (uploaded && uploaded !== '__uploading__') {
          posterUrl = uploaded;
        }
      }
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
          <Upload
            accept="image/*"
            maxCount={1}
            fileList={posterFile}
            customRequest={customUpload}
            onChange={({ fileList }) => setPosterFile(fileList)}
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
          <Input placeholder="或填写图片URL，例：https://example.com/poster.jpg" className="mt-2" />
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
