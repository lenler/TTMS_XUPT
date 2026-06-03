// 观众端订单 API 服务：锁座 + 下单 + 支付 + 查询 + 退票

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';

/** 锁座请求 */
interface LockSeatsParams {
  scheduleId: number;
  seatIds: number[];
}

/** 锁座响应中的票信息 */
interface LockedTicket {
  ticketId: number;
  seatId: number;
  row: number;
  col: number;
  price: number;
}

/** 锁座响应 */
interface LockResult {
  lockToken: string;
  tickets: LockedTicket[];
  totalPrice: number;
  expireAt: string;
}

/** 下单响应 */
interface OrderResult {
  orderId: number;
  totalPrice: number;
  status: string;
  expireAt: string;
}

/** 支付请求 */
interface PayParams {
  paymentMethod: string;
  paymentPassword: string;
}

/** 支付响应票 */
interface PaidTicket {
  ticketId: number;
  seatRow: number;
  seatCol: number;
  playName: string;
  studioName: string;
  showTime: string;
  price: number;
  ticketStatus: string;
}

/** 支付响应 */
interface PayResult {
  orderId: number;
  orderStatus: string;
  balance?: number;
  tickets: PaidTicket[];
}

/** 订单列表项 */
interface OrderItem {
  orderId: number;
  playName: string;
  poster: string;
  studioName: string;
  showTime: string;
  ticketCount: number;
  totalPrice: number;
  status: string;
  createdAt: string;
}

/** 退票响应 */
interface RefundResult {
  orderId: number;
  refundedTickets: number[];
  refundAmount: number;
  orderStatus: string;
  ticketStatus: string;
}

/** 锁定座位 */
export function lockSeats(data: LockSeatsParams): Promise<ApiResponse<LockResult>> {
  return request.post('/customer/api/orders/lock', data);
}

/** 下单 */
export function createOrder(data: { lockToken: string }): Promise<ApiResponse<OrderResult>> {
  return request.post('/customer/api/orders', data);
}

/** 支付 */
export function payOrder(id: number, data: PayParams): Promise<ApiResponse<PayResult>> {
  return request.post(`/customer/api/orders/${id}/pay`, data);
}

/** 查询单个订单 */
export function getOrder(id: number): Promise<ApiResponse<PayResult>> {
  return request.get(`/customer/api/orders/${id}`);
}

/** 查询我的订单列表 */
export function getMyOrders(
  params: PageParams & { status?: string }
): Promise<ApiResponse<PageData<OrderItem>>> {
  return request.get('/customer/api/orders', { params });
}

/** 退票 */
export function refundOrder(id: number, ticketIds: number[]): Promise<ApiResponse<RefundResult>> {
  return request.post(`/customer/api/orders/${id}/refund`, { ticketIds });
}
