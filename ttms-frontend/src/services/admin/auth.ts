// 管理端认证 API 服务

import request from '../request';
import type { ApiResponse } from '@/types/api';

/** 管理端登录 */
export function adminLogin(data: { username: string; password: string }) {
  return request.post<ApiResponse<{ token: string; employee: unknown }>>('/admin/api/login', data);
}
