// 认证 Mock Handler：管理端登录

import { http, HttpResponse } from 'msw';

export const authHandlers = [
  // 管理端登录
  http.post('/admin/api/login', async ({ request }) => {
    const body = (await request.json()) as { username: string; password: string };

    // 模拟校验
    if (body.username === 'admin' && body.password === '123456') {
      return HttpResponse.json({
        resCode: '10000',
        resMsg: '登录成功',
        data: {
          token: 'mock-admin-token-2026',
          employee: {
            id: 1,
            name: '管理员',
            positionName: '系统管理员',
            roles: ['系统管理员'],
          },
        },
      });
    }

    // 登录失败
    return HttpResponse.json({
      resCode: '20001',
      resMsg: '工号或密码错误',
      data: null,
    });
  }),

  // 获取当前用户信息
  http.get('/admin/api/current-user', ({ request }) => {
    const auth = request.headers.get('Authorization');
    if (!auth || auth !== 'Bearer mock-admin-token-2026') {
      return HttpResponse.json(
        { resCode: '20002', resMsg: '未登录', data: null },
        { status: 401 }
      );
    }
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: {
        id: 1,
        name: '管理员',
        positionName: '系统管理员',
        roles: ['系统管理员'],
      },
    });
  }),

  // 获取当前用户菜单权限
  http.get('/admin/api/current-user/menus', ({ request }) => {
    const auth = request.headers.get('Authorization');
    if (!auth || auth !== 'Bearer mock-admin-token-2026') {
      return HttpResponse.json(
        { resCode: '20002', resMsg: '未登录', data: null },
        { status: 401 }
      );
    }
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: {
        menus: [
          {
            name: '剧院管理',
            icon: 'BankOutlined',
            children: [
              { name: '演出厅管理', url: '/admin/studio' },
              { name: '剧目管理', url: '/admin/play' },
              { name: '演出计划', url: '/admin/schedule' },
              { name: '验票管理', url: '/admin/check' },
            ],
          },
        ],
      },
    });
  }),
];
