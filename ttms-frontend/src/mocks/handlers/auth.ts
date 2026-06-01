// 认证 Mock Handler：管理端登录 + 用户信息 + 菜单权限

import { http, HttpResponse } from 'msw';
import { buildMenuTree } from './role';

/** Mock 用户数据 */
const users: Record<string, { password: string; id: number; name: string; positionName: string; roles: string[]; roleIds: number[] }> = {
  admin: {
    password: '123456',
    id: 1,
    name: '管理员',
    positionName: '系统管理员',
    roles: ['系统管理员'],
    roleIds: [1],
  },
  EMP001: {
    password: '123456',
    id: 2,
    name: '张三',
    positionName: '售票员',
    roles: ['售票员'],
    roleIds: [2],
  },
};

export const authHandlers = [
  /** 管理端登录 */
  http.post('/admin/api/login', async ({ request }) => {
    const body = (await request.json()) as { username: string; password: string };

    const user = users[body.username];
    if (user && user.password === body.password) {
      return HttpResponse.json({
        resCode: '10000',
        resMsg: '登录成功',
        data: {
          token: `mock-token-${body.username}`,
          employee: {
            id: user.id,
            name: user.name,
            positionName: user.positionName,
            roles: user.roles,
          },
        },
      });
    }

    return HttpResponse.json({
      resCode: '20001',
      resMsg: '工号或密码错误',
      data: null,
    });
  }),

  /** 获取当前用户信息 */
  http.get('/admin/api/current-user', ({ request }) => {
    const auth = request.headers.get('Authorization') || '';
    const username = auth.replace('Bearer mock-token-', '');
    const user = users[username];

    if (!user) {
      return HttpResponse.json(
        { resCode: '20002', resMsg: '未登录', data: null },
        { status: 401 }
      );
    }
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: {
        id: user.id,
        name: user.name,
        positionName: user.positionName,
        roles: user.roles,
      },
    });
  }),

  /** 获取当前用户菜单权限 —— 按角色返回不同菜单 */
  http.get('/admin/api/current-user/menus', ({ request }) => {
    const auth = request.headers.get('Authorization') || '';
    const username = auth.replace('Bearer mock-token-', '');
    const user = users[username];

    if (!user) {
      return HttpResponse.json(
        { resCode: '20002', resMsg: '未登录', data: null },
        { status: 401 }
      );
    }

    // 聚合该用户所有角色的资源ID
    const resourceIds = new Set<number>();
    for (const rid of user.roleIds) {
      // 从 role handler 的 roles 数组获取 resourceIds
      // 这里直接硬编码：系统管理员(roleId=1)全权限，售票员(roleId=2)有限
      if (rid === 1) {
        // 系统管理员：全部资源
        [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11].forEach((id) => resourceIds.add(id));
      } else if (rid === 2) {
        // 售票员：工作台 + 售票 + 退票 + 观众
        [1, 6, 7, 9].forEach((id) => resourceIds.add(id));
      }
    }

    const menus = buildMenuTree(Array.from(resourceIds));
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { menus },
    });
  }),
];
