// 验票管理 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';

/** 验票结果 */
export interface VerifyResult {
  ticketId: number;
  seatRow: number;
  seatCol: number;
  playName: string;
  studioName: string;
  showTime: string;
  status: string;
  message: string;
}

/** 验票记录 */
export interface CheckRecord {
  id: number;
  ticketId: number;
  seatRow: number;
  seatCol: number;
  playName: string;
  studioName: string;
  showTime: string;
  verifyTime: string;
  operatorName: string;
  result: string;
}

/** 查询验票记录 */
export function getChecks(
  params: PageParams & { scheduleId?: string; ticketId?: string }
): Promise<ApiResponse<PageData<CheckRecord>>> {
  return request.get('/admin/api/checks', { params });
}

/** 验票 */
export function verifyTicket(
  ticketId: number,
  operatorId?: number
): Promise<ApiResponse<VerifyResult>> {
  return request.post(`/admin/api/tickets/${ticketId}/verify`, operatorId ? { operatorId } : {});
}
