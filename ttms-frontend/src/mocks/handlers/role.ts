// 角色权限 Mock Handler：角色 CRUD + 资源列表 + 员工角色分配

import { http, HttpResponse } from 'msw';

/** 资源 */
interface Resource {
  id: number;
  type: string;
  name: string;
  url: string;
  parentName: string;
}

/** 角色 */
interface Role {
  id: number;
  name: string;
  resourceIds: number[];
}

/** 静态资源列表（菜单项→URL 映射） */
const resources: Resource[] = [
  { id: 1, type: 'menu', name: '工作台', url: '/admin/dashboard', parentName: '' },
  { id: 2, type: 'menu', name: '演出厅管理', url: '/admin/studio', parentName: '剧院管理' },
  { id: 3, type: 'menu', name: '剧目管理', url: '/admin/play', parentName: '剧院管理' },
  { id: 4, type: 'menu', name: '演出计划', url: '/admin/schedule', parentName: '剧院管理' },
  { id: 5, type: 'menu', name: '验票管理', url: '/admin/check', parentName: '剧院管理' },
  { id: 6, type: 'menu', name: '售票记录', url: '/admin/sale', parentName: '票务管理' },
  { id: 7, type: 'menu', name: '退票处理', url: '/admin/sale/refund', parentName: '票务管理' },
  { id: 8, type: 'menu', name: '员工管理', url: '/admin/employee', parentName: '用户管理' },
  { id: 9, type: 'menu', name: '观众管理', url: '/admin/customer', parentName: '用户管理' },
  { id: 10, type: 'menu', name: '角色管理', url: '/admin/role', parentName: '权限管理' },
  { id: 11, type: 'menu', name: '财务统计', url: '/admin/finance', parentName: '财务管理' },
];

let roles: Role[] = [
  { id: 1, name: '系统管理员', resourceIds: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11] },
  { id: 2, name: '售票员', resourceIds: [1, 6, 7, 9] },
];
let roleNextId = 3;

// 员工角色映射（内存存储）
const employeeRoles: Map<number, number[]> = new Map();

/** 根据 resourceIds 展开为 Resource 数组 */
function expandResources(resourceIds: number[]): Resource[] {
  return resourceIds
    .map((rid) => resources.find((r) => r.id === rid))
    .filter((r): r is Resource => r !== undefined);
}

/** 构建菜单树 */
export function buildMenuTree(resourceIds: number[]): unknown[] {
  const expanded = expandResources(resourceIds);
  const topLevel = expanded.filter((r) => !r.parentName);
  const groups = new Map<string, Resource[]>();

  for (const r of expanded) {
    if (r.parentName) {
      const group = groups.get(r.parentName) || [];
      group.push(r);
      groups.set(r.parentName, group);
    }
  }

  const menus: unknown[] = [];
  for (const top of topLevel) {
    menus.push({ name: top.name, url: top.url });
  }

  for (const [parentName, children] of groups) {
    menus.push({
      name: parentName,
      children: children.map((c) => ({ name: c.name, url: c.url })),
    });
  }

  return menus;
}

export const roleHandlers = [
  /** 查询角色列表 */
  http.get('/admin/api/roles', () => {
    const list = roles.map((r) => ({
      ...r,
      resources: expandResources(r.resourceIds),
    }));
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { list },
    });
  }),

  /** 新增角色 */
  http.post('/admin/api/roles', async ({ request }) => {
    const body = (await request.json()) as { name: string; resourceIds: number[] };
    const newRole: Role = {
      id: roleNextId++,
      name: body.name,
      resourceIds: body.resourceIds,
    };
    roles.push(newRole);
    return HttpResponse.json({ resCode: '10000', resMsg: '添加成功', data: { id: newRole.id } });
  }),

  /** 修改角色 */
  http.put('/admin/api/roles/:id', async ({ request, params }) => {
    const body = (await request.json()) as { name: string; resourceIds: number[] };
    const idx = roles.findIndex((r) => r.id === Number(params.id));
    if (idx === -1) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    roles[idx] = { ...roles[idx], ...body };
    return HttpResponse.json({ resCode: '10000', resMsg: '修改成功', data: null });
  }),

  /** 删除角色 */
  http.delete('/admin/api/roles/:id', ({ params }) => {
    roles = roles.filter((r) => r.id !== Number(params.id));
    return HttpResponse.json({ resCode: '10000', resMsg: '删除成功', data: null });
  }),

  /** 查询资源列表 */
  http.get('/admin/api/resources', () => {
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { list: resources },
    });
  }),

  /** 为用户分配角色 */
  http.put('/admin/api/employees/:id/roles', async ({ request, params }) => {
    const body = (await request.json()) as { roleIds: number[] };
    employeeRoles.set(Number(params.id), body.roleIds);
    return HttpResponse.json({ resCode: '10000', resMsg: '分配成功', data: null });
  }),

  /** 查询用户角色（供 Employee Detail 回填） */
  http.get('/admin/api/employees/:id/roles', ({ params }) => {
    const empRoles = employeeRoles.get(Number(params.id)) || [];
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { roleIds: empRoles },
    });
  }),
];
