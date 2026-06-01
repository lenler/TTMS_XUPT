// 角色权限 API 服务

export interface RoleItem {
  id: number;
  name: string;
  description: string;
  permissions: string[];
}

const roles: RoleItem[] = [
  { id: 1, name: '系统管理员', description: '维护基础资料、用户和系统配置', permissions: ['剧院管理', '用户管理', '权限管理'] },
  { id: 2, name: '售票员', description: '负责线下售票、退票和销售记录', permissions: ['售票记录', '退票处理'] },
  { id: 3, name: '场务员', description: '负责现场验票和入场核验', permissions: ['验票管理'] },
  { id: 4, name: '财务经理', description: '查看销售收入、票房和上座率', permissions: ['财务管理'] },
];

export async function getRoles(): Promise<RoleItem[]> {
  return roles;
}

export async function getRoleById(id: number): Promise<RoleItem | undefined> {
  return roles.find((role) => role.id === id);
}
