// 演出计划管理 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';
import type { Schedule } from '@/types/models';

/** 演出计划列表查询参数 */
interface ScheduleListParams extends PageParams {
  playId?: string;
  studioId?: string;
}

/** 查询演出计划列表 */
export function getSchedules(params: ScheduleListParams): Promise<ApiResponse<PageData<Schedule>>> {
  return request.get('/admin/api/schedules', { params });
}

/** 查询单个演出计划 */
export function getScheduleById(id: number): Promise<ApiResponse<Schedule>> {
  return request.get(`/admin/api/schedules/${id}`);
}

/** 新增演出计划 */
export function createSchedule(data: {
  playId: number;
  studioId: number;
  showTime: string;
  ticketPrice: number;
}): Promise<ApiResponse<{ id: number }>> {
  return request.post('/admin/api/schedules', data);
}

/** 修改演出计划 */
export function updateSchedule(
  id: number,
  data: { playId: number; studioId: number; showTime: string; ticketPrice: number }
): Promise<ApiResponse<null>> {
  return request.put(`/admin/api/schedules/${id}`, data);
}

/** 删除演出计划 */
export function deleteSchedule(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/api/schedules/${id}`);
}
