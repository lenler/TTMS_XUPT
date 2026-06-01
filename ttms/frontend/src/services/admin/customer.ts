// 观众管理 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';
import type { Customer } from '@/types/models';

/** 查询观众列表 */
export function getCustomers(params: PageParams): Promise<ApiResponse<PageData<Customer>>> {
  return request.get('/admin/api/customers', { params });
}

/** 查看观众详情 */
export function getCustomerById(id: number): Promise<ApiResponse<Customer>> {
  return request.get(`/admin/api/customers/${id}`);
}

/** 封禁/解封观众 */
export function updateCustomerStatus(id: number, status: number): Promise<ApiResponse<null>> {
  return request.put(`/admin/api/customers/${id}/status`, { status });
}
