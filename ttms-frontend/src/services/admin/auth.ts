// 管理端认证 API 服务

import request from '../request';
import type { ApiResponse } from '@/types/api';

/** 管理端登录 */
export function adminLogin(data: { username: string; password: string }) {
  return request.post<ApiResponse<{
    token: string;
    employee: { id: number; name: string; positionName: string; roles: string[] };
  }>>('/admin/api/login', data);
}

/** 获取当前用户信息 */
export function getCurrentUser() {
  return request.get<ApiResponse<unknown>>('/admin/api/current-user');
}

/** 获取当前用户菜单权限 */
export function getCurrentUserMenus() {
  return request.get<ApiResponse<unknown>>('/admin/api/current-user/menus');
}
