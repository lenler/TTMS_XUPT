// 演出厅管理 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';
import type { Studio } from '@/types/models';

/** 查询演出厅列表 */
export function getStudios(params: PageParams) {
  return request.get<ApiResponse<PageData<Studio>>, ApiResponse<PageData<Studio>>>('/admin/api/studios', { params });
}

/** 查询单个演出厅 */
export function getStudioById(id: number) {
  return request.get<ApiResponse<Studio>, ApiResponse<Studio>>(`/admin/api/studios/${id}`);
}

/** 新增演出厅 */
export function createStudio(data: Omit<Studio, 'id' | 'status'>) {
  return request.post<ApiResponse<{ id: number }>, ApiResponse<{ id: number }>>('/admin/api/studios', data);
}

/** 修改演出厅 */
export function updateStudio(id: number, data: Omit<Studio, 'id' | 'status'>) {
  return request.put<ApiResponse<null>, ApiResponse<null>>(`/admin/api/studios/${id}`, data);
}

/** 删除演出厅 */
export function deleteStudio(id: number) {
  return request.delete<ApiResponse<null>, ApiResponse<null>>(`/admin/api/studios/${id}`);
}
