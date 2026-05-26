// 剧目管理 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';
import type { Play } from '@/types/models';

/** 查询剧目列表 */
export function getPlays(params: PageParams & { type?: string; lang?: string }): Promise<ApiResponse<PageData<Play>>> {
  return request.get('/admin/api/plays', { params });
}

/** 查询单个剧目 */
export function getPlayById(id: number): Promise<ApiResponse<Play>> {
  return request.get(`/admin/api/plays/${id}`);
}

/** 新增剧目 */
export function createPlay(data: Omit<Play, 'id' | 'typeName' | 'langName' | 'status'>): Promise<ApiResponse<{ id: number }>> {
  return request.post('/admin/api/plays', data);
}

/** 修改剧目 */
export function updatePlay(id: number, data: Omit<Play, 'id' | 'typeName' | 'langName' | 'status'>): Promise<ApiResponse<null>> {
  return request.put(`/admin/api/plays/${id}`, data);
}

/** 删除剧目 */
export function deletePlay(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/api/plays/${id}`);
}

/** 获取数据字典 */
export function getDicts(parentId: string): Promise<ApiResponse<{ list: { id: number; name: string; value: string }[] }>> {
  return request.get('/admin/api/dicts', { params: { parentId } });
}
