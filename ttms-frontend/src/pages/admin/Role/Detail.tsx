// 角色详情页

import { Card, Descriptions, Tag } from 'antd';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getRoleById, type RoleItem } from '@/services/admin/role';

function RoleDetailPage() {
  const { id } = useParams();
  const [role, setRole] = useState<RoleItem | undefined>();

  useEffect(() => {
    getRoleById(Number(id)).then(setRole);
  }, [id]);

  return (
    <Card title="角色详情">
      <Descriptions column={1} bordered>
        <Descriptions.Item label="角色名称">{role?.name || '-'}</Descriptions.Item>
        <Descriptions.Item label="说明">{role?.description || '-'}</Descriptions.Item>
        <Descriptions.Item label="权限">
          {role?.permissions.map((item) => <Tag key={item}>{item}</Tag>)}
        </Descriptions.Item>
      </Descriptions>
    </Card>
  );
}

export default RoleDetailPage;
