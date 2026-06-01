// 财务统计 API 服务

import axios from 'axios';

export interface FinanceSummary {
  salesAmount: number;
  orderCount: number;
  soldTicketCount: number;
  checkedTicketCount: number;
  totalTicketCount: number;
  attendanceRate: number;
}

async function unwrap<T>(promise: Promise<{ data: { code: string; message: string; data: T } }>) {
  const res = await promise;
  if (res.data.code !== '10000') {
    throw new Error(res.data.message);
  }
  return res.data.data;
}

export function getTheaterSummary(params?: { startDate?: string; endDate?: string }) {
  return unwrap<FinanceSummary>(axios.get('/finance/theater', { params }));
}

export function getDailySummary(params?: { date?: string; employeeId?: number }) {
  return unwrap<FinanceSummary>(axios.get('/finance/daily', { params }));
}
