// 观众端排片 API 服务

import axios from 'axios';
import type { Play, Schedule, Ticket } from '@/types/models';

async function unwrap<T>(promise: Promise<{ data: { code?: string; resCode?: string; message?: string; resMsg?: string; data: T } }>) {
  const res = await promise;
  const code = res.data.code ?? res.data.resCode;
  if (code !== '10000') {
    throw new Error(res.data.message ?? res.data.resMsg ?? '请求失败');
  }
  return res.data.data;
}

export function getPublicPlays(params?: { name?: string }) {
  return unwrap<Play[]>(axios.get('/public/plays', { params }));
}

export function getPublicSchedules(params?: { playId?: number }) {
  return unwrap<Schedule[]>(axios.get('/public/schedules', { params }));
}

export async function getScheduleTickets(scheduleId: number) {
  const page = await unwrap<{ list: Ticket[] }>(
    axios.get(`/admin/api/schedules/${scheduleId}/tickets`, { params: { pageSize: 1000 } })
  );
  return page.list;
}
