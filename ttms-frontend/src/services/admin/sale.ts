// 售票/退票 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';
import type { Sale } from '@/types/models';

/** 售票请求参数 */
interface CreateSaleParams {
  scheduleId: number;
  ticketIds: number[];
  customerId: number;
  paymentAmount: number;
}

/** 售票响应 */
interface CreateSaleResult {
  saleId: number;
  change: number;
}

/** 退票响应 */
interface RefundResult {
  saleId: number;
  refundedTickets: number[];
  refundAmount: number;
  orderStatus: string;
  ticketStatus: string;
}

/** 线下售票 */
export function createSale(data: CreateSaleParams): Promise<ApiResponse<CreateSaleResult>> {
  return request.post('/admin/api/sales', data);
}

/** 查询销售记录 */
export function getSales(
  params: PageParams & { type?: string; startDate?: string; endDate?: string }
): Promise<ApiResponse<PageData<Sale>>> {
  return request.get('/admin/api/sales', { params });
}

/** 查询单个销售单 */
export function getSaleById(id: number): Promise<ApiResponse<Sale>> {
  return request.get(`/admin/api/sales/${id}`);
}

/** 退票 */
export function refundSale(id: number, ticketIds: number[]): Promise<ApiResponse<RefundResult>> {
  return request.post(`/admin/api/sales/${id}/refund`, { ticketIds });
}
