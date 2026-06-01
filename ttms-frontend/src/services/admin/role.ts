// 角色权限 API 服务

import request from '../request';
import type { ApiResponse } from '@/types/api';

/** 资源项 */
export interface Resource {
  id: number;
  type: string;
  name: string;
  url: string;
  parentName: string;
}

/** 角色 */
export interface Role {
  id: number;
  name: string;
  resources: Resource[];
}

/** 角色列表响应 */
interface RoleListResult {
  list: Role[];
}

/** 查询角色列表 */
export function getRoles(): Promise<ApiResponse<RoleListResult>> {
  return request.get('/admin/api/roles');
}

/** 新增角色 */
export function createRole(data: { name: string; resourceIds: number[] }): Promise<ApiResponse<{ id: number }>> {
  return request.post('/admin/api/roles', data);
}

/** 修改角色 */
export function updateRole(id: number, data: { name: string; resourceIds: number[] }): Promise<ApiResponse<null>> {
  return request.put(`/admin/api/roles/${id}`, data);
}

/** 删除角色 */
export function deleteRole(id: number): Promise<ApiResponse<null>> {
  return request.delete(`/admin/api/roles/${id}`);
}

/** 查询资源列表 */
export function getResources(): Promise<ApiResponse<{ list: Resource[] }>> {
  return request.get('/admin/api/resources');
}

/** 为用户分配角色 */
export function assignEmployeeRoles(empId: number, roleIds: number[]): Promise<ApiResponse<null>> {
  return request.put(`/admin/api/employees/${empId}/roles`, { roleIds });
}
