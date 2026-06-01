// 演出厅管理 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';
import type { Studio } from '@/types/models';

/** 查询演出厅列表 */
export function getStudios(params: PageParams): Promise<ApiResponse<PageData<Studio>>> {
  return request.get('/admin/api/studios', { params });
}

/** 查询单个演出厅 */
export function getStudioById(id: number): Promise<ApiResponse<Studio>> {
  return request.get(`/admin/api/studios/${id}`);
}

/** 新增演出厅 */
export function createStudio(data: Omit<Studio, 'id' | 'status'>): Promise<ApiResponse<{ id: number }>> {
  return request.post('/admin/api/studios', data);
}

/** 修改演出厅 */
export function updateStudio(id: number, data: Omit<Studio, 'id' | 'status'>): Promise<ApiResponse<null>> {
  return request.put(`/admin/api/studios/${id}`, data);
}

/** 删除演出厅 */
export function deleteStudio(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/api/studios/${id}`);
}
