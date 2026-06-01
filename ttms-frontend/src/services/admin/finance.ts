// 财务统计 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';

/** 财务概览 */
export interface FinanceOverview {
  totalSales: number;
  totalOrders: number;
  totalTickets: number;
  avgOccupancy: number;
  topPlay: {
    playId: number;
    playName: string;
    sales: number;
  } | null;
}

/** 剧目销售排名项 */
export interface PlayRankingItem {
  playId: number;
  playName: string;
  showCount: number;
  totalTickets: number;
  soldTickets: number;
  occupancy: number;
  sales: number;
}

/** 剧院业绩项 */
export interface StudioPerformanceItem {
  studioId: number;
  studioName: string;
  showCount: number;
  totalSeats: number;
  soldSeats: number;
  occupancy: number;
  sales: number;
}

/** 售票员业绩项 */
export interface EmployeeSalesItem {
  employeeId: number;
  employeeName: string;
  orderCount: number;
  totalAmount: number;
  refundCount: number;
  refundAmount: number;
}

/** 查询参数 */
interface FinanceDateParams {
  startDate?: string;
  endDate?: string;
}

/** 获取财务概览 */
export function getFinanceOverview(params?: FinanceDateParams): Promise<ApiResponse<FinanceOverview>> {
  return request.get('/admin/api/finance/overview', { params });
}

/** 获取剧目销售排名 */
export function getPlayRanking(
  params: FinanceDateParams & PageParams
): Promise<ApiResponse<PageData<PlayRankingItem>>> {
  return request.get('/admin/api/finance/play-ranking', { params });
}

/** 获取剧院销售业绩 */
export function getStudioPerformance(
  params?: FinanceDateParams
): Promise<ApiResponse<{ list: StudioPerformanceItem[] }>> {
  return request.get('/admin/api/finance/studio-performance', { params });
}

/** 获取售票员销售统计 */
export function getEmployeeSales(
  params?: FinanceDateParams
): Promise<ApiResponse<{ list: EmployeeSalesItem[] }>> {
  return request.get('/admin/api/finance/employee-sales', { params });
}
