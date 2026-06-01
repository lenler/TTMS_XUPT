// 财务统计 Mock Handler：概览 + 剧目排名 + 剧院业绩 + 售票员业绩

import { http, HttpResponse } from 'msw';
import { ticketsStore } from './schedule';
import { sales } from './sale';
import schedulesData from '../data/schedules.json';
import playsData from '../data/plays.json';
import studiosData from '../data/studios.json';
import employeesData from '../data/employees.json';

interface PlayData {
  id: number;
  name: string;
  [key: string]: unknown;
}

interface StudioData {
  id: number;
  name: string;
  rowCount: number;
  colCount: number;
  [key: string]: unknown;
}

interface ScheduleData {
  id: number;
  playId: number;
  studioId: number;
  showTime: string;
  ticketPrice: number;
  [key: string]: unknown;
}

interface EmployeeData {
  id: number;
  name: string;
  positionId: number;
  [key: string]: unknown;
}

/** 计算概览数据 */
function calcOverview(startDate?: string, endDate?: string) {
  const relatedTickets = getTicketsInRange(startDate, endDate);
  const soldTickets = relatedTickets.filter((t) => t.status === 2 || t.status === 3);
  const totalSales = soldTickets.reduce((sum, t) => sum + t.price, 0);

  // 统计涉及到的场次数
  const totalSeatsInShows = relatedTickets.length;
  const soldCount = soldTickets.length;

  const avgOccupancy = totalSeatsInShows > 0 ? soldCount / totalSeatsInShows : 0;

  // 热卖剧目
  const playSales: Record<number, number> = {};
  for (const t of soldTickets) {
    const sched = (schedulesData as ScheduleData[]).find((s) => s.id === t.scheduleId);
    if (sched) {
      playSales[sched.playId] = (playSales[sched.playId] || 0) + t.price;
    }
  }
  let topPlay: { playId: number; playName: string; sales: number } | null = null;
  let maxSales = 0;
  for (const [pid, amount] of Object.entries(playSales)) {
    if (amount > maxSales) {
      const play = (playsData as PlayData[]).find((p) => p.id === Number(pid));
      topPlay = { playId: Number(pid), playName: play?.name || '未知', sales: amount };
      maxSales = amount;
    }
  }

  // 统计订单数（来自 sales 记录）
  const relatedSales = sales.filter((s) => {
    if (!startDate && !endDate) return true;
    const saleTime = new Date(s.saleTime).getTime();
    if (startDate && saleTime < new Date(startDate).getTime()) return false;
    if (endDate && saleTime > new Date(endDate + ' 23:59:59').getTime()) return false;
    return true;
  });

  return {
    totalSales,
    totalOrders: relatedSales.length,
    totalTickets: soldCount,
    avgOccupancy: Math.round(avgOccupancy * 10000) / 10000,
    topPlay,
  };
}

/** 根据日期范围筛选关联的票（通过演出计划时间） */
function getTicketsInRange(startDate?: string, endDate?: string) {
  return ticketsStore.filter((t) => {
    const sched = (schedulesData as ScheduleData[]).find((s) => s.id === t.scheduleId);
    if (!sched) return false;
    if (!startDate && !endDate) return true;
    const showTime = new Date(sched.showTime).getTime();
    if (startDate && showTime < new Date(startDate).getTime()) return false;
    if (endDate && showTime > new Date(endDate + ' 23:59:59').getTime()) return false;
    return true;
  });
}

/** 计算剧目排名 */
function calcPlayRanking(startDate?: string, endDate?: string) {
  const relatedTickets = getTicketsInRange(startDate, endDate);
  const playStats: Record<number, { showIds: Set<number>; totalTickets: number; soldTickets: number; sales: number }> = {};

  for (const t of relatedTickets) {
    const sched = (schedulesData as ScheduleData[]).find((s) => s.id === t.scheduleId);
    if (!sched) continue;
    const pid = sched.playId;
    if (!playStats[pid]) {
      playStats[pid] = { showIds: new Set(), totalTickets: 0, soldTickets: 0, sales: 0 };
    }
    playStats[pid].showIds.add(t.scheduleId);
    playStats[pid].totalTickets++;
    if (t.status === 2 || t.status === 3) {
      playStats[pid].soldTickets++;
      playStats[pid].sales += t.price;
    }
  }

  const list = Object.entries(playStats).map(([pid, stats]) => {
    const play = (playsData as PlayData[]).find((p) => p.id === Number(pid));
    return {
      playId: Number(pid),
      playName: play?.name || '未知',
      showCount: stats.showIds.size,
      totalTickets: stats.totalTickets,
      soldTickets: stats.soldTickets,
      occupancy: stats.totalTickets > 0 ? Math.round((stats.soldTickets / stats.totalTickets) * 10000) / 10000 : 0,
      sales: Math.round(stats.sales * 100) / 100,
    };
  });

  // 按销售额降序
  list.sort((a, b) => b.sales - a.sales);
  return list;
}

/** 计算剧院业绩 */
function calcStudioPerformance(startDate?: string, endDate?: string) {
  const relatedTickets = getTicketsInRange(startDate, endDate);
  const studioStats: Record<number, { showIds: Set<number>; totalSeats: number; soldSeats: number; sales: number }> = {};

  for (const t of relatedTickets) {
    const sched = (schedulesData as ScheduleData[]).find((s) => s.id === t.scheduleId);
    if (!sched) continue;
    const sid = sched.studioId;
    if (!studioStats[sid]) {
      studioStats[sid] = { showIds: new Set(), totalSeats: 0, soldSeats: 0, sales: 0 };
    }
    studioStats[sid].showIds.add(t.scheduleId);
    studioStats[sid].totalSeats++;
    if (t.status === 2 || t.status === 3) {
      studioStats[sid].soldSeats++;
      studioStats[sid].sales += t.price;
    }
  }

  const list = Object.entries(studioStats).map(([sid, stats]) => {
    const studio = (studiosData as StudioData[]).find((s) => s.id === Number(sid));
    return {
      studioId: Number(sid),
      studioName: studio?.name || '未知',
      showCount: stats.showIds.size,
      totalSeats: stats.totalSeats,
      soldSeats: stats.soldSeats,
      occupancy: stats.totalSeats > 0 ? Math.round((stats.soldSeats / stats.totalSeats) * 10000) / 10000 : 0,
      sales: Math.round(stats.sales * 100) / 100,
    };
  });

  return list;
}

/** 计算售票员业绩 */
function calcEmployeeSales(startDate?: string, endDate?: string) {
  const filteredSales = sales.filter((s) => {
    if (!startDate && !endDate) return true;
    const saleTime = new Date(s.saleTime).getTime();
    if (startDate && saleTime < new Date(startDate).getTime()) return false;
    if (endDate && saleTime > new Date(endDate + ' 23:59:59').getTime()) return false;
    return true;
  });

  const employeeStats: Record<string, { name: string; orderCount: number; totalAmount: number; refundCount: number; refundAmount: number }> = {};

  for (const s of filteredSales) {
    const key = s.employeeName;
    if (!employeeStats[key]) {
      employeeStats[key] = { name: s.employeeName, orderCount: 0, totalAmount: 0, refundCount: 0, refundAmount: 0 };
    }
    if (s.type === 1) {
      // 销售
      employeeStats[key].orderCount++;
      employeeStats[key].totalAmount += s.paymentAmount;
    } else if (s.type === 2) {
      // 退票
      employeeStats[key].refundCount++;
      employeeStats[key].refundAmount += s.paymentAmount;
    }
  }

  // 如果 sales 为空，则用 employees 数据生成零值占位
  if (Object.keys(employeeStats).length === 0) {
    for (const emp of employeesData as EmployeeData[]) {
      employeeStats[emp.name] = { name: emp.name, orderCount: 0, totalAmount: 0, refundCount: 0, refundAmount: 0 };
    }
  }

  return Object.entries(employeeStats).map(([, stats], idx) => ({
    employeeId: idx + 1,
    employeeName: stats.name,
    orderCount: stats.orderCount,
    totalAmount: Math.round(stats.totalAmount * 100) / 100,
    refundCount: stats.refundCount,
    refundAmount: Math.round(stats.refundAmount * 100) / 100,
  }));
}

export const financeHandlers = [
  /** 财务概览 */
  http.get('/admin/api/finance/overview', ({ request }) => {
    const url = new URL(request.url);
    const startDate = url.searchParams.get('startDate') || undefined;
    const endDate = url.searchParams.get('endDate') || undefined;
    const data = calcOverview(startDate, endDate);
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data });
  }),

  /** 剧目销售排名 */
  http.get('/admin/api/finance/play-ranking', ({ request }) => {
    const url = new URL(request.url);
    const startDate = url.searchParams.get('startDate') || undefined;
    const endDate = url.searchParams.get('endDate') || undefined;
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    const all = calcPlayRanking(startDate, endDate);
    const start = (page - 1) * pageSize;
    const list = all.slice(start, start + pageSize);

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { list, total: all.length, page, pageSize },
    });
  }),

  /** 剧院业绩 */
  http.get('/admin/api/finance/studio-performance', ({ request }) => {
    const url = new URL(request.url);
    const startDate = url.searchParams.get('startDate') || undefined;
    const endDate = url.searchParams.get('endDate') || undefined;
    const list = calcStudioPerformance(startDate, endDate);
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: { list } });
  }),

  /** 售票员业绩 */
  http.get('/admin/api/finance/employee-sales', ({ request }) => {
    const url = new URL(request.url);
    const startDate = url.searchParams.get('startDate') || undefined;
    const endDate = url.searchParams.get('endDate') || undefined;
    const list = calcEmployeeSales(startDate, endDate);
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: { list } });
  }),
];
