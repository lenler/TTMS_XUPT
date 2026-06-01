// 验票 Mock Handler：验票记录 + 验票操作

import { http, HttpResponse } from 'msw';
import { ticketsStore } from './schedule';
import schedulesData from '../data/schedules.json';
import playsData from '../data/plays.json';
import studiosData from '../data/studios.json';

interface CheckRecord {
  id: number;
  ticketId: number;
  seatRow: number;
  seatCol: number;
  playName: string;
  studioName: string;
  showTime: string;
  verifyTime: string;
  operatorName: string;
  result: string; // 'passed' | 'rejected'
  message: string;
}

let checkRecords: CheckRecord[] = [];
let checkNextId = 1;

export const checkHandlers = [
  /** 查询验票记录 */
  http.get('/admin/api/checks', ({ request }) => {
    const url = new URL(request.url);
    const scheduleId = url.searchParams.get('scheduleId') || '';
    const ticketId = url.searchParams.get('ticketId') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = checkRecords;
    if (scheduleId) {
      // 简单处理：filter by matching ticket's schedule
    }
    if (ticketId) {
      filtered = filtered.filter((r) => r.ticketId === Number(ticketId));
    }

    const start = (page - 1) * pageSize;
    const list = filtered.slice(start, start + pageSize);

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { list, total: filtered.length, page, pageSize },
    });
  }),

  /** 验票 */
  http.post('/admin/api/tickets/:ticketId/verify', async ({ params }) => {
    const ticketId = Number(params.ticketId);
    const ticket = ticketsStore.find((t) => t.ticketId === ticketId);

    if (!ticket) {
      const record: CheckRecord = {
        id: checkNextId++,
        ticketId,
        seatRow: 0,
        seatCol: 0,
        playName: '',
        studioName: '',
        showTime: '',
        verifyTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
        operatorName: '当前场务员',
        result: 'rejected',
        message: '票不存在',
      };
      checkRecords.unshift(record);
      return HttpResponse.json({
        resCode: '20004',
        resMsg: '票不存在',
        data: { ...record, status: 'rejected' },
      });
    }

    if (ticket.status !== 2) {
      const record: CheckRecord = {
        id: checkNextId++,
        ticketId,
        seatRow: ticket.seatRow,
        seatCol: ticket.seatCol,
        playName: '',
        studioName: '',
        showTime: '',
        verifyTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
        operatorName: '当前场务员',
        result: 'rejected',
        message: `票状态异常，当前不可验（状态: ${ticket.status}）`,
      };
      checkRecords.unshift(record);
      return HttpResponse.json({
        resCode: '20005',
        resMsg: '票状态异常',
        data: { ...record, status: 'rejected' },
      });
    }

    // 查找关联的演出计划
    const schedule = (schedulesData as Record<string, unknown>[]).find(
      (s: Record<string, unknown>) => s.id === ticket.scheduleId
    );
    const play = schedule
      ? (playsData as Record<string, unknown>[]).find(
          (p: Record<string, unknown>) => p.id === (schedule as Record<string, unknown>).playId
        )
      : null;
    const studio = schedule
      ? (studiosData as Record<string, unknown>[]).find(
          (st: Record<string, unknown>) => st.id === (schedule as Record<string, unknown>).studioId
        )
      : null;

    // 更新票状态为 checked
    ticket.status = 3;

    const record: CheckRecord = {
      id: checkNextId++,
      ticketId,
      seatRow: ticket.seatRow,
      seatCol: ticket.seatCol,
      playName: (play?.name as string) || '未知',
      studioName: (studio?.name as string) || '未知',
      showTime: (schedule?.showTime as string) || '',
      verifyTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
      operatorName: '当前场务员',
      result: 'passed',
      message: '验票通过',
    };
    checkRecords.unshift(record);

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '验票通过',
      data: { ...record, status: 'checked' },
    });
  }),
];
