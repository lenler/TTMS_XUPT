// 员工新增/修改 Modal 表单

import { useEffect, useState } from 'react';
import { Modal, Form, Input, Select, message } from 'antd';
import { createEmployee, updateEmployee } from '@/services/admin/employee';
import type { Employee } from '@/types/models';

interface EmployeeDetailModalProps {
  open: boolean;
  employee: Employee | null; // null = 新增，非 null = 修改
  onClose: () => void;
  onSuccess: () => void;
}

interface EmployeeFormValues {
  employeeNo: string;
  name: string;
  gender: number;
  phone: string;
  email: string;
  positionId: number;
  password?: string;
}

/** 岗位选项 */
const positionOptions = [
  { value: 1, label: '售票员' },
  { value: 2, label: '运营经理' },
  { value: 3, label: '系统管理员' },
  { value: 4, label: '会计' },
  { value: 5, label: '财务经理' },
  { value: 6, label: '场务员' },
  { value: 7, label: '设备运维' },
];

function EmployeeDetailModal({ open, employee, onClose, onSuccess }: EmployeeDetailModalProps) {
  const [form] = Form.useForm<EmployeeFormValues>();
  const [saving, setSaving] = useState(false);
  const isEdit = employee !== null;

  /** 弹窗打开/employee 变化时回填表单 */
  useEffect(() => {
    if (open) {
      if (employee) {
        form.setFieldsValue({
          employeeNo: employee.employeeNo,
          name: employee.name,
          gender: employee.gender,
          phone: employee.phone,
          email: employee.email,
          positionId: employee.positionId,
          password: undefined,
        });
      } else {
        form.resetFields();
      }
    }
  }, [open, employee, form]);

  /** 提交 */
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSaving(true);
      if (isEdit) {
        await updateEmployee(employee!.id, values);
        message.success('修改成功');
      } else {
        // 新增时密码已通过表单校验，必定存在
        await createEmployee(values as Required<EmployeeFormValues>);
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
      title={isEdit ? '修改员工' : '新增员工'}
      open={open}
      onOk={handleSubmit}
      onCancel={onClose}
      confirmLoading={saving}
      destroyOnClose
      width={560}
    >
      <Form form={form} layout="vertical" initialValues={{ gender: 1 }}>
        <Form.Item
          name="employeeNo"
          label="工号"
          rules={[{ required: true, message: '请输入工号' }]}
        >
          <Input placeholder="例：EMP001" />
        </Form.Item>

        <Form.Item
          name="name"
          label="姓名"
          rules={[{ required: true, message: '请输入姓名' }]}
        >
          <Input placeholder="例：张三" />
        </Form.Item>

        <Form.Item
          name="gender"
          label="性别"
          rules={[{ required: true, message: '请选择性别' }]}
        >
          <Select
            options={[
              { value: 1, label: '男' },
              { value: 0, label: '女' },
            ]}
          />
        </Form.Item>

        <Form.Item
          name="positionId"
          label="岗位"
          rules={[{ required: true, message: '请选择岗位' }]}
        >
          <Select placeholder="选择岗位" options={positionOptions} />
        </Form.Item>

        <Form.Item
          name="phone"
          label="联系电话"
          rules={[
            { required: true, message: '请输入电话' },
            { pattern: /^1\d{10}$/, message: '请输入正确手机号' },
          ]}
        >
          <Input placeholder="例：13800138000" />
        </Form.Item>

        <Form.Item
          name="email"
          label="邮箱"
          rules={[{ type: 'email', message: '请输入正确邮箱格式' }]}
        >
          <Input placeholder="例：zhangsan@example.com" />
        </Form.Item>

        <Form.Item
          name="password"
          label="密码"
          rules={isEdit ? [] : [{ required: true, message: '请输入密码' }]}
        >
          <Input.Password placeholder={isEdit ? '留空则不修改密码' : '请输入密码'} />
        </Form.Item>
      </Form>
    </Modal>
  );
}

export default EmployeeDetailModal;
