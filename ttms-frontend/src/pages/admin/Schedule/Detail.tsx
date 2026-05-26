// 演出计划新增/修改 Modal 表单

import { useEffect, useState } from 'react';
import { Modal, Form, Select, DatePicker, InputNumber, Alert, message } from 'antd';
import dayjs from 'dayjs';
import { createSchedule, updateSchedule } from '@/services/admin/schedule';
import type { Schedule } from '@/types/models';

interface ScheduleDetailModalProps {
  open: boolean;
  schedule: Schedule | null;
  playOptions: { value: number; label: string }[];
  studioOptions: { value: number; label: string }[];
  onClose: () => void;
  onSuccess: () => void;
}

interface ScheduleFormValues {
  playId: number;
  studioId: number;
  showTime: string;
  ticketPrice: number;
}

function ScheduleDetailModal({
  open,
  schedule,
  playOptions,
  studioOptions,
  onClose,
  onSuccess,
}: ScheduleDetailModalProps) {
  const [form] = Form.useForm<ScheduleFormValues>();
  const [saving, setSaving] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const isEdit = schedule !== null;

  useEffect(() => {
    if (open) {
      if (schedule) {
        form.setFieldsValue({
          playId: schedule.playId,
          studioId: schedule.studioId,
          showTime: schedule.showTime,
          ticketPrice: schedule.ticketPrice,
        });
      } else {
        form.resetFields();
      }
      setErrorMsg(null);
    }
  }, [open, schedule, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSaving(true);
      setErrorMsg(null);

      // showTime 统一为字符串格式
      const data = {
        ...values,
        showTime:
          typeof values.showTime === 'string'
            ? values.showTime
            : (values.showTime as unknown as dayjs.Dayjs).format('YYYY-MM-DD HH:mm:ss'),
      };

      if (isEdit) {
        await updateSchedule(schedule!.id, data);
        message.success('修改成功');
      } else {
        await createSchedule(data);
        message.success('新增成功');
      }
      onSuccess();
    } catch (err: unknown) {
      if (err instanceof Error) {
        // 业务规则错误（20005 冲突）：Modal 内联展示
        if (err.message.includes('占用')) {
          setErrorMsg(err.message);
        }
        // 其他错误（网络、参数等）已在 request 拦截器中 message.error 处理
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      title={isEdit ? '修改演出计划' : '新增演出计划'}
      open={open}
      onOk={handleSubmit}
      onCancel={onClose}
      confirmLoading={saving}
      destroyOnClose
      width={560}
    >
      {errorMsg && (
        <Alert
          type="error"
          message={errorMsg}
          closable
          onClose={() => setErrorMsg(null)}
          style={{ marginBottom: 16 }}
        />
      )}

      <Form form={form} layout="vertical">
        <Form.Item
          name="playId"
          label="选择剧目"
          rules={[{ required: true, message: '请选择剧目' }]}
        >
          <Select
            placeholder="选择剧目"
            options={playOptions}
            showSearch
            filterOption={(input, option) =>
              (option?.label as string)?.includes(input) ?? false
            }
          />
        </Form.Item>

        <Form.Item
          name="studioId"
          label="选择演出厅"
          rules={[{ required: true, message: '请选择演出厅' }]}
        >
          <Select placeholder="选择演出厅" options={studioOptions} />
        </Form.Item>

        <Form.Item
          name="showTime"
          label="演出时间"
          rules={[{ required: true, message: '请选择时间' }]}
          getValueFromEvent={(val: dayjs.Dayjs | null) =>
            val ? val.format('YYYY-MM-DD HH:mm:ss') : ''
          }
          getValueProps={(val: string) => ({
            value: val ? dayjs(val) : undefined,
          })}
        >
          <DatePicker
            showTime={{ format: 'HH:mm' }}
            format="YYYY-MM-DD HH:mm"
            placeholder="选择演出时间"
            style={{ width: '100%' }}
          />
        </Form.Item>

        <Form.Item
          name="ticketPrice"
          label="票价"
          rules={[{ required: true, message: '请输入票价' }]}
        >
          <InputNumber min={0} precision={2} prefix="¥" style={{ width: '100%' }} />
        </Form.Item>
      </Form>
    </Modal>
  );
}

export default ScheduleDetailModal;
