// 演出计划 Mock Handler：CRUD + 冲突检测

import { http, HttpResponse } from 'msw';
import schedulesData from '../data/schedules.json';
import playsData from '../data/plays.json';
import studiosData from '../data/studios.json';

type Schedule = {
  id: number;
  playId: number;
  playName: string;
  studioId: number;
  studioName: string;
  showTime: string;
  ticketPrice: number;
  status: number;
};

let schedules = JSON.parse(JSON.stringify(schedulesData)) as Schedule[];
let nextId = Math.max(0, ...schedules.map((s) => s.id)) + 1;

/** 冲突检测：同一演出厅在前后 2 小时内是否已有排期 */
function hasConflict(studioId: number, showTime: string, excludeId?: number): boolean {
  const target = new Date(showTime).getTime();
  const margin = 2 * 60 * 60 * 1000; // 2 小时
  return schedules.some((s) => {
    if (s.studioId !== studioId) return false;
    if (excludeId !== undefined && s.id === excludeId) return false;
    const existing = new Date(s.showTime).getTime();
    return Math.abs(target - existing) < margin;
  });
}

export const scheduleHandlers = [
  /** 查询演出计划列表 */
  http.get('/admin/api/schedules', ({ request }) => {
    const url = new URL(request.url);
    const playId = url.searchParams.get('playId') || '';
    const studioId = url.searchParams.get('studioId') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = schedules;
    if (playId) filtered = filtered.filter((s) => s.playId === Number(playId));
    if (studioId) filtered = filtered.filter((s) => s.studioId === Number(studioId));

    const start = (page - 1) * pageSize;
    const list = filtered.slice(start, start + pageSize);

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { list, total: filtered.length, page, pageSize },
    });
  }),

  /** 查询单个演出计划 */
  http.get('/admin/api/schedules/:id', ({ params }) => {
    const schedule = schedules.find((s) => s.id === Number(params.id));
    if (!schedule) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: schedule });
  }),

  /** 新增演出计划 */
  http.post('/admin/api/schedules', async ({ request }) => {
    const body = (await request.json()) as {
      playId: number;
      studioId: number;
      showTime: string;
      ticketPrice: number;
    };

    // 冲突检测
    if (hasConflict(body.studioId, body.showTime)) {
      return HttpResponse.json({
        resCode: '20005',
        resMsg: '该演出厅在此时段已被占用，请更换时间或演出厅',
        data: null,
      });
    }

    const play = (playsData as Record<string, unknown>[]).find((p: Record<string, unknown>) => p.id === body.playId);
    const studio = (studiosData as Record<string, unknown>[]).find((s: Record<string, unknown>) => s.id === body.studioId);

    const newSchedule: Schedule = {
      ...body,
      id: nextId++,
      playName: (play?.name as string) || '未知',
      studioName: (studio?.name as string) || '未知',
      status: 1,
    };
    schedules.push(newSchedule);
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '添加成功',
      data: { id: newSchedule.id },
    });
  }),

  /** 修改演出计划 */
  http.put('/admin/api/schedules/:id', async ({ request, params }) => {
    const body = (await request.json()) as {
      playId: number;
      studioId: number;
      showTime: string;
      ticketPrice: number;
    };
    const idx = schedules.findIndex((s) => s.id === Number(params.id));
    if (idx === -1) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }

    // 冲突检测（排除自身）
    if (hasConflict(body.studioId, body.showTime, Number(params.id))) {
      return HttpResponse.json({
        resCode: '20005',
        resMsg: '该演出厅在此时段已被占用',
        data: null,
      });
    }

    const play = (playsData as Record<string, unknown>[]).find((p: Record<string, unknown>) => p.id === body.playId);
    const studio = (studiosData as Record<string, unknown>[]).find((s: Record<string, unknown>) => s.id === body.studioId);

    schedules[idx] = {
      ...schedules[idx],
      ...body,
      playName: (play?.name as string) || schedules[idx].playName,
      studioName: (studio?.name as string) || schedules[idx].studioName,
    };
    return HttpResponse.json({ resCode: '10000', resMsg: '修改成功', data: null });
  }),

  /** 删除演出计划 */
  http.delete('/admin/api/schedules/:id', ({ params }) => {
    schedules = schedules.filter((s) => s.id !== Number(params.id));
    return HttpResponse.json({ resCode: '10000', resMsg: '删除成功', data: null });
  }),
];
