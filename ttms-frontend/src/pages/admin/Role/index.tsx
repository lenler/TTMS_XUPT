// 角色管理列表页

import { useEffect, useState } from 'react';
import { Table, Tag } from 'antd';
import { getRoles, type RoleItem } from '@/services/admin/role';

function RoleListPage() {
  const [roles, setRoles] = useState<RoleItem[]>([]);

  useEffect(() => {
    getRoles().then(setRoles);
  }, []);

  return (
    <Table
      rowKey="id"
      dataSource={roles}
      pagination={false}
      columns={[
        { title: 'ID', dataIndex: 'id', width: 80 },
        { title: '角色名称', dataIndex: 'name', width: 160 },
        { title: '说明', dataIndex: 'description' },
        {
          title: '权限',
          dataIndex: 'permissions',
          render: (items: string[]) => items.map((item) => <Tag key={item}>{item}</Tag>),
        },
      ]}
    />
  );
}

export default RoleListPage;
