// 角色新增/修改 Modal 表单（含资源权限勾选）

import { useEffect, useState } from 'react';
import { Modal, Form, Input, Card, Checkbox, Button, Space, message } from 'antd';
import { createRole, updateRole, getResources } from '@/services/admin/role';
import type { Role, Resource } from '@/services/admin/role';

interface RoleDetailModalProps {
  open: boolean;
  role: Role | null;
  onClose: () => void;
  onSuccess: () => void;
}

function RoleDetailModal({ open, role, onClose, onSuccess }: RoleDetailModalProps) {
  const [form] = Form.useForm<{ name: string }>();
  const [saving, setSaving] = useState(false);
  const [resources, setResources] = useState<Resource[]>([]);
  const [checkedIds, setCheckedIds] = useState<number[]>([]);
  const isEdit = role !== null;

  /** 加载资源列表 */
  useEffect(() => {
    getResources()
      .then((res) => setResources(res.data.list))
      .catch(() => {});
  }, []);

  /** 弹窗打开/role 变化时回填 */
  useEffect(() => {
    if (open) {
      if (role) {
        form.setFieldsValue({ name: role.name });
        setCheckedIds(role.resources?.map((r) => r.id) || []);
      } else {
        form.resetFields();
        setCheckedIds([]);
      }
    }
  }, [open, role, form]);

  /** 按 parentName 分组资源 */
  const groupedResources = resources.reduce(
    (acc, r) => {
      const key = r.parentName || '顶级';
      if (!acc[key]) acc[key] = [];
      acc[key].push(r);
      return acc;
    },
    {} as Record<string, Resource[]>
  );

  /** 提交 */
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (checkedIds.length === 0) {
        message.warning('请至少勾选一项资源权限');
        return;
      }
      setSaving(true);
      const data = { name: values.name, resourceIds: checkedIds };
      if (isEdit) {
        await updateRole(role!.id, data);
        message.success('修改成功');
      } else {
        await createRole(data);
        message.success('新增成功');
      }
      onSuccess();
    } catch {
      // 校验失败或接口报错
    } finally {
      setSaving(false);
    }
  };

  /** 全选/取消 */
  const allResourceIds = resources.map((r) => r.id);
  const isAllChecked = allResourceIds.length > 0 && checkedIds.length === allResourceIds.length;

  const handleSelectAll = () => {
    setCheckedIds(isAllChecked ? [] : allResourceIds);
  };

  return (
    <Modal
      title={isEdit ? '修改角色' : '新增角色'}
      open={open}
      onOk={handleSubmit}
      onCancel={onClose}
      confirmLoading={saving}
      destroyOnClose
      width={640}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="角色名称"
          rules={[{ required: true, message: '请输入角色名称' }]}
        >
          <Input placeholder="例：售票员" />
        </Form.Item>
      </Form>

      <div style={{ marginBottom: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span style={{ fontWeight: 'bold' }}>资源权限</span>
        <Button size="small" onClick={handleSelectAll}>
          {isAllChecked ? '取消全选' : '全选'}
        </Button>
      </div>

      <Checkbox.Group
        value={checkedIds}
        onChange={(vals) => setCheckedIds(vals as number[])}
        style={{ width: '100%' }}
      >
        {Object.entries(groupedResources).map(([groupName, items]) => (
          <Card
            key={groupName}
            size="small"
            title={groupName}
            style={{ marginBottom: 8 }}
          >
            <Space wrap>
              {items.map((r) => (
                <Checkbox key={r.id} value={r.id}>
                  {r.name}
                </Checkbox>
              ))}
            </Space>
          </Card>
        ))}
      </Checkbox.Group>
    </Modal>
  );
}

export default RoleDetailModal;
