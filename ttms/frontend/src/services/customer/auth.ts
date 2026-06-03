// 观众端认证 API 服务：登录 + 注册 + 个人信息

import request from '../request';
import type { ApiResponse } from '@/types/api';

/** 登录请求 */
interface CustomerLoginParams {
  username: string;
  password: string;
}

/** 登录响应 */
interface CustomerLoginResult {
  token: string;
  customer: {
    id: number;
    name: string;
    username: string;
    balance: number;
  };
}

/** 注册请求 */
interface CustomerRegisterParams {
  name: string;
  gender: number;
  phone: string;
  email: string;
  username: string;
  password: string;
  paymentPassword: string;
}

/** 个人信息 */
export interface CustomerProfile {
  id: number;
  name: string;
  gender: number;
  phone: string;
  email: string;
  username: string;
  balance: number;
  rechargeTotal?: number;
  rechargeCount?: number;
  status: number;
}

/** 修改密码请求 */
interface ChangePasswordParams {
  oldPassword: string;
  newPassword: string;
}

/** 观众登录 */
export function customerLogin(data: CustomerLoginParams): Promise<ApiResponse<CustomerLoginResult>> {
  return request.post('/customer/api/login', data);
}

/** 观众注册 */
export function customerRegister(data: CustomerRegisterParams): Promise<ApiResponse<{ id: number }>> {
  return request.post('/customer/api/register', data);
}

/** 获取个人信息 */
export function getProfile(): Promise<ApiResponse<CustomerProfile>> {
  return request.get('/customer/api/profile');
}

/** 余额充值 */
export function rechargeWallet(amount: number): Promise<ApiResponse<{
  amount: number;
  balance: number;
  rechargeTotal: number;
  rechargeCount: number;
}>> {
  return request.post('/customer/api/wallet/recharge', { amount });
}

/** 修改个人信息 */
export function updateProfile(data: Partial<CustomerProfile>): Promise<ApiResponse<null>> {
  return request.put('/customer/api/profile', data);
}

/** 修改密码 */
export function changePassword(data: ChangePasswordParams): Promise<ApiResponse<null>> {
  return request.put('/customer/api/profile/password', data);
}
