// 员工管理 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';
import type { Employee } from '@/types/models';

/** 查询员工列表 */
export function getEmployees(params: PageParams & { role?: string }): Promise<ApiResponse<PageData<Employee>>> {
  return request.get('/admin/api/employees', { params });
}

/** 查询单个员工 */
export function getEmployeeById(id: number): Promise<ApiResponse<Employee>> {
  return request.get(`/admin/api/employees/${id}`);
}

/** 新增员工 */
export function createEmployee(data: {
  employeeNo: string;
  name: string;
  gender: number;
  phone: string;
  email: string;
  positionId: number;
  password: string;
}): Promise<ApiResponse<{ id: number }>> {
  return request.post('/admin/api/employees', data);
}

/** 修改员工 */
export function updateEmployee(id: number, data: {
  employeeNo: string;
  name: string;
  gender: number;
  phone: string;
  email: string;
  positionId: number;
  password?: string;
}): Promise<ApiResponse<null>> {
  return request.put(`/admin/api/employees/${id}`, data);
}

/** 删除员工 */
export function deleteEmployee(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/api/employees/${id}`);
}
