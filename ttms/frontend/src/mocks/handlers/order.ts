// 观众端 Mock Handler：首页 + 放映 + 锁座 + 下单 + 支付 + 订单 + 退票 + 登录 + 注册 + 榜单

import { http, HttpResponse } from 'msw';
import { ticketsStore } from './schedule';
import schedulesData from '../data/schedules.json';
import playsData from '../data/plays.json';
import studiosData from '../data/studios.json';
import customersData from '../data/customers.json';

interface ScheduleData {
  id: number; playId: number; studioId: number; showTime: string; ticketPrice: number;
  playName?: string; studioName?: string;
}
interface PlayData { id: number; name: string; typeName?: string; typeId?: number; langName?: string; langId?: number; poster: string; video?: string; introduction?: string; duration: number; basePrice: number; status?: number; }
interface StudioData { id: number; name: string; rowCount: number; colCount: number; introduction?: string; status?: number; }
interface CustomerData { id: number; name: string; gender: number; phone: string; email: string; username: string; password?: string; paymentPassword?: string; balance: number; status: number; }

/** 订单 */
interface CustomerOrder {
  orderId: number;
  customerId: number;
  lockToken: string;
  tickets: { ticketId: number; seatRow: number; seatCol: number; price: number; status: string }[];
  totalPrice: number;
  status: string; // unpaid | paid | refunded | cancelled
  playName: string;
  poster: string;
  studioName: string;
  showTime: string;
  createdAt: string;
}

let customerOrders: CustomerOrder[] = [];
let orderNextId = 202610001;

/** 锁座记录：lockToken → { scheduleId, ticketIds, expireAt } */
const locks: Record<string, { scheduleId: number; ticketIds: number[]; expireAt: number }> = {};

let registeredCustomers: (CustomerData & { password: string; paymentPassword: string })[] = JSON.parse(JSON.stringify(customersData)).map((c: CustomerData) => ({
  ...c,
  password: '123456',
  paymentPassword: '123456',
}));

// 初始化静态 schedules 列表
const schedules: ScheduleData[] = JSON.parse(JSON.stringify(schedulesData));
// 用 play/studio 数据补充名称
for (const s of schedules) {
  const play = (playsData as PlayData[]).find((p) => p.id === s.playId);
  const studio = (studiosData as StudioData[]).find((st) => st.id === s.studioId);
  s.playName = play?.name || '未知';
  s.studioName = studio?.name || '未知';
}

function getPlayById(id: number) { return (playsData as PlayData[]).find((p) => p.id === id); }
function getStudioById(id: number) { return (studiosData as StudioData[]).find((s) => s.id === id); }
function getScheduleById(id: number) { return schedules.find((s) => s.id === id); }

/** 清除过期锁 */
function clearExpiredLocks() {
  const now = Date.now();
  for (const [token, lock] of Object.entries(locks)) {
    if (now > lock.expireAt) {
      for (const tid of lock.ticketIds) {
        const t = ticketsStore.find((tk) => tk.ticketId === tid);
        if (t && t.status === 1) t.status = 0; // locked → available
      }
      delete locks[token];
    }
  }
}

export const orderHandlers = [
  // ======================== 首页 ========================
  http.get('/customer/api/home', () => {
    clearExpiredLocks();
    const plays = playsData as PlayData[];
    // 热卖剧目：按已售票数排序取前 4
    const playSoldCounts: Record<number, number> = {};
    for (const t of ticketsStore) {
      if (t.status === 2 || t.status === 3) {
        const s = getScheduleById(t.scheduleId);
        if (s) playSoldCounts[s.playId] = (playSoldCounts[s.playId] || 0) + 1;
      }
    }
    const hotPlays = plays
      .map((p) => ({ ...p, soldCount: playSoldCounts[p.id] || 0 }))
      .sort((a, b) => b.soldCount - a.soldCount)
      .slice(0, 4)
      .map((p) => ({
        id: p.id, name: p.name, poster: p.poster, typeName: p.typeName || '未知',
        duration: p.duration, basePrice: p.basePrice, soldCount: p.soldCount,
      }));

    // 近期演出：未来场次，按时间升序取前 6
    const now = new Date();
    const upcoming = schedules
      .filter((s) => new Date(s.showTime).getTime() > now.getTime())
      .sort((a, b) => new Date(a.showTime).getTime() - new Date(b.showTime).getTime())
      .slice(0, 6)
      .map((s) => {
        const scheduleTickets = ticketsStore.filter((t) => t.scheduleId === s.id);
        const available = scheduleTickets.filter((t) => t.status === 0).length;
        return {
          id: s.id, playId: s.playId, playName: s.playName, studioName: s.studioName,
          showTime: s.showTime, ticketPrice: s.ticketPrice, availableSeats: available,
        };
      });

    return HttpResponse.json({
      resCode: '10000', resMsg: '请求成功',
      data: { hotPlays, upcomingSchedules: upcoming },
    });
  }),

  // ======================== 放映安排 ========================
  http.get('/customer/api/schedules', ({ request }) => {
    clearExpiredLocks();
    const url = new URL(request.url);
    const playId = url.searchParams.get('playId') || '';
    const date = url.searchParams.get('date') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = schedules;
    if (playId) filtered = filtered.filter((s) => s.playId === Number(playId));
    if (date) filtered = filtered.filter((s) => s.showTime.startsWith(date));

    const start = (page - 1) * pageSize;
    const list = filtered.slice(start, start + pageSize).map((s) => {
      const play = getPlayById(s.playId);
      const scheduleTickets = ticketsStore.filter((t) => t.scheduleId === s.id);
      const available = scheduleTickets.filter((t) => t.status === 0).length;
      return {
        id: s.id, playId: s.playId, playName: s.playName,
        playPoster: play?.poster || '', playType: play?.typeName || '未知',
        playDuration: play?.duration || 0,
        studioId: s.studioId, studioName: s.studioName,
        showTime: s.showTime, ticketPrice: s.ticketPrice, availableSeats: available,
      };
    });

    return HttpResponse.json({
      resCode: '10000', resMsg: '请求成功',
      data: { list, total: filtered.length, page, pageSize },
    });
  }),

  http.get('/customer/api/schedules/:id', ({ params }) => {
    clearExpiredLocks();
    const s = getScheduleById(Number(params.id));
    if (!s) return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });

    const play = getPlayById(s.playId);
    const studio = getStudioById(s.studioId);
    const scheduleTickets = ticketsStore.filter((t) => t.scheduleId === s.id);

    const seats = scheduleTickets.map((t) => ({
      id: t.seatId, row: t.seatRow, col: t.seatCol,
      status: t.status === 0 ? 0 : t.status === 1 ? 1 : 2, // available/locked/sold
    }));

    // 生成座位布局字符串
    const rowCount = studio?.rowCount || 7;
    const colCount = studio?.colCount || 7;
    const layout: string[] = [];
    for (let r = 1; r <= rowCount; r++) {
      let rowStr = '';
      for (let c = 1; c <= colCount; c++) {
        const seat = seats.find((st) => st.row === r && st.col === c);
        rowStr += seat ? 'a' : '_';
      }
      layout.push(rowStr);
    }

    return HttpResponse.json({
      resCode: '10000', resMsg: '请求成功',
      data: {
        id: s.id,
        play: {
          id: play?.id || 0, name: play?.name || '', poster: play?.poster || '',
          typeName: play?.typeName || '', langName: play?.langName || '',
          introduction: play?.introduction || '', duration: play?.duration || 0,
        },
        studio: { id: studio?.id || 0, name: studio?.name || '', introduction: studio?.introduction || '' },
        showTime: s.showTime, ticketPrice: s.ticketPrice, seats, seatLayout: layout,
      },
    });
  }),

  // ======================== 锁座 ========================
  http.post('/customer/api/orders/lock', async ({ request }) => {
    clearExpiredLocks();
    const body = (await request.json()) as { scheduleId: number; seatIds: number[] };

    if (!body.seatIds || body.seatIds.length === 0) {
      return HttpResponse.json({ resCode: '20001', resMsg: '请至少选择一个座位', data: null });
    }
    if (body.seatIds.length > 6) {
      return HttpResponse.json({ resCode: '20005', resMsg: '每人最多锁定 6 张票', data: null });
    }

    const s = getScheduleById(body.scheduleId);
    if (!s) return HttpResponse.json({ resCode: '20004', resMsg: '场次不存在', data: null });

    const tickets: { ticketId: number; seatId: number; row: number; col: number; price: number }[] = [];
    for (const seatId of body.seatIds) {
      const ticket = ticketsStore.find(
        (t) => t.scheduleId === body.scheduleId && t.seatId === seatId
      );
      if (!ticket) {
        return HttpResponse.json({ resCode: '20004', resMsg: `座位 ${seatId} 不存在`, data: null });
      }
      if (ticket.status !== 0) {
        return HttpResponse.json({ resCode: '20005', resMsg: `座位 ${seatId} 已被锁定或售出`, data: null });
      }
      tickets.push({
        ticketId: ticket.ticketId, seatId: ticket.seatId,
        row: ticket.seatRow, col: ticket.seatCol, price: ticket.price,
      });
    }

    // 锁定票
    const lockToken = `lock-${Date.now()}-${Math.random().toString(36).slice(2)}`;
    const expireAt = new Date(Date.now() + 5 * 60 * 1000).toISOString();
    locks[lockToken] = {
      scheduleId: body.scheduleId,
      ticketIds: tickets.map((t) => t.ticketId),
      expireAt: Date.now() + 5 * 60 * 1000,
    };

    for (const t of tickets) {
      const ticket = ticketsStore.find((tk) => tk.ticketId === t.ticketId);
      if (ticket) ticket.status = 1; // locked
    }

    const totalPrice = tickets.reduce((sum, t) => sum + t.price, 0);

    return HttpResponse.json({
      resCode: '10000', resMsg: '锁定成功',
      data: { lockToken, tickets, totalPrice, expireAt },
    });
  }),

  // ======================== 下单 ========================
  http.post('/customer/api/orders', async ({ request }) => {
    clearExpiredLocks();
    const body = (await request.json()) as { lockToken: string };

    const lock = locks[body.lockToken];
    if (!lock) {
      return HttpResponse.json({ resCode: '20005', resMsg: '锁座已过期，请重新选择', data: null });
    }

    const lockTickets = lock.ticketIds.map((tid) => ticketsStore.find((t) => t.ticketId === tid))
      .filter(Boolean) as typeof ticketsStore;

    // 校验票状态仍为 locked
    for (const t of lockTickets) {
      if (t.status !== 1) {
        return HttpResponse.json({ resCode: '20005', resMsg: `票 ${t.ticketId} 状态异常，请重新选择`, data: null });
      }
    }

    const s = getScheduleById(lock.scheduleId);
    const play = s ? getPlayById(s.playId) : null;
    const totalPrice = lockTickets.reduce((sum, t) => sum + t.price, 0);

    // 从 localStorage 读取当前登录观众 ID（注册/登录时已存入 customerInfo）
    let customerId = 1;
    try {
      const infoStr = localStorage.getItem('customerInfo');
      if (infoStr) {
        const info = JSON.parse(infoStr);
        customerId = info.id || 1;
      }
    } catch { /* 解析失败用默认值 */ }

    const orderId = orderNextId++;
    const newOrder: CustomerOrder = {
      orderId,
      customerId,
      lockToken: body.lockToken,
      tickets: lockTickets.map((t) => ({
        ticketId: t.ticketId, seatRow: t.seatRow, seatCol: t.seatCol,
        price: t.price, status: 'locked',
      })),
      totalPrice,
      status: 'unpaid',
      playName: play?.name || '未知',
      poster: play?.poster || '',
      studioName: s?.studioName || '未知',
      showTime: s?.showTime || '',
      createdAt: new Date().toISOString().replace('T', ' ').slice(0, 19),
    };
    customerOrders.unshift(newOrder);

    return HttpResponse.json({
      resCode: '10000', resMsg: '下单成功',
      data: { orderId, totalPrice, status: 'unpaid', expireAt: new Date(Date.now() + 15 * 60 * 1000).toISOString() },
    });
  }),

  // ======================== 支付 ========================
  http.post('/customer/api/orders/:id/pay', async ({ request, params }) => {
    const order = customerOrders.find((o) => o.orderId === Number(params.id));
    if (!order) return HttpResponse.json({ resCode: '20004', resMsg: '订单不存在', data: null });
    if (order.status !== 'unpaid') {
      return HttpResponse.json({ resCode: '20005', resMsg: '订单状态不可支付', data: null });
    }

    const body = (await request.json()) as { paymentMethod: string; paymentPassword: string };
    const customer = registeredCustomers.find((c) => c.id === order.customerId);

    // 余额支付校验密码
    if (body.paymentMethod === 'balance') {
      if (body.paymentPassword !== (customer?.paymentPassword || '123456')) {
        return HttpResponse.json({ resCode: '20005', resMsg: '支付密码错误', data: null });
      }
      if (customer && customer.balance < order.totalPrice) {
        return HttpResponse.json({ resCode: '20005', resMsg: '余额不足', data: null });
      }
      if (customer) customer.balance -= order.totalPrice;
    }

    // 更新票状态为 sold
    for (const t of order.tickets) {
      const ticket = ticketsStore.find((tk) => tk.ticketId === t.ticketId);
      if (ticket) ticket.status = 2; // sold
      t.status = 'sold';
    }
    order.status = 'paid';

    // 清除锁
    delete locks[order.lockToken];

    const playName = order.playName;
    const studioName = order.studioName;
    const showTime = order.showTime;

    return HttpResponse.json({
      resCode: '10000', resMsg: '支付成功',
      data: {
        orderId: order.orderId, orderStatus: 'paid',
        tickets: order.tickets.map((t) => ({
          ticketId: t.ticketId, seatRow: t.seatRow, seatCol: t.seatCol,
          playName, studioName, showTime, price: t.price, ticketStatus: 'sold',
        })),
      },
    });
  }),

  // ======================== 查询订单 ========================
  http.get('/customer/api/orders/:id', ({ params }) => {
    const order = customerOrders.find((o) => o.orderId === Number(params.id));
    if (!order) return HttpResponse.json({ resCode: '20004', resMsg: '订单不存在', data: null });
    return HttpResponse.json({
      resCode: '10000', resMsg: '请求成功',
      data: {
        orderId: order.orderId, orderStatus: order.status,
        tickets: order.tickets.map((t) => ({
          ticketId: t.ticketId, seatRow: t.seatRow, seatCol: t.seatCol,
          playName: order.playName, studioName: order.studioName,
          showTime: order.showTime, price: t.price, ticketStatus: t.status,
        })),
      },
    });
  }),

  http.get('/customer/api/orders', ({ request }) => {
    const url = new URL(request.url);
    const status = url.searchParams.get('status') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = customerOrders;
    if (status) filtered = filtered.filter((o) => o.status === status);

    const start = (page - 1) * pageSize;
    const list = filtered.slice(start, start + pageSize).map((o) => ({
      orderId: o.orderId, playName: o.playName, poster: o.poster,
      studioName: o.studioName, showTime: o.showTime,
      ticketCount: o.tickets.length, totalPrice: o.totalPrice,
      status: o.status, createdAt: o.createdAt,
    }));

    return HttpResponse.json({
      resCode: '10000', resMsg: '请求成功',
      data: { list, total: filtered.length, page, pageSize },
    });
  }),

  // ======================== 退票 ========================
  http.post('/customer/api/orders/:id/refund', async ({ request, params }) => {
    const order = customerOrders.find((o) => o.orderId === Number(params.id));
    if (!order) return HttpResponse.json({ resCode: '20004', resMsg: '订单不存在', data: null });
    if (order.status !== 'paid') {
      return HttpResponse.json({ resCode: '20005', resMsg: '仅已支付订单可退票', data: null });
    }

    const body = (await request.json()) as { ticketIds: number[] };
    let refundAmount = 0;
    const refundedIds: number[] = [];

    for (const tid of body.ticketIds) {
      const t = order.tickets.find((ot) => ot.ticketId === tid);
      if (!t) return HttpResponse.json({ resCode: '20004', resMsg: `票 ${tid} 不在该订单中`, data: null });
      if (t.status !== 'sold') {
        return HttpResponse.json({ resCode: '20005', resMsg: `票 ${tid} 状态不可退`, data: null });
      }
      const ticket = ticketsStore.find((tk) => tk.ticketId === tid);
      if (ticket) {
        ticket.status = 5; // refunded → released to available
        // 实际流转 refunded → available，简化处理直接 available
        ticket.status = 0;
      }
      t.status = 'refunded';
      refundAmount += t.price;
      refundedIds.push(tid);
    }

    // 更新订单状态
    const remaining = order.tickets.filter((t) => !body.ticketIds.includes(t.ticketId));
    order.tickets = remaining;
    if (remaining.every((t) => t.status === 'refunded')) {
      order.status = 'refunded';
    }

    // 退款到余额
    const customer = registeredCustomers.find((c) => c.id === order.customerId);
    if (customer) customer.balance += refundAmount;

    return HttpResponse.json({
      resCode: '10000', resMsg: '退票成功',
      data: { orderId: order.orderId, refundedTickets: refundedIds, refundAmount,
        orderStatus: order.status, ticketStatus: 'refunded' },
    });
  }),

  // ======================== 登录 ========================
  http.post('/customer/api/login', async ({ request }) => {
    const body = (await request.json()) as { username: string; password: string };
    const customer = registeredCustomers.find(
      (c) => c.username === body.username && c.password === body.password
    );
    if (!customer) return HttpResponse.json({ resCode: '20001', resMsg: '用户名或密码错误', data: null });
    if (customer.status === 0) return HttpResponse.json({ resCode: '20005', resMsg: '账号已被封禁', data: null });

    return HttpResponse.json({
      resCode: '10000', resMsg: '登录成功',
      data: {
        token: `customer-token-${customer.id}-${Date.now()}`,
        customer: { id: customer.id, name: customer.name, username: customer.username, balance: customer.balance },
      },
    });
  }),

  // ======================== 注册 ========================
  http.post('/customer/api/register', async ({ request }) => {
    const body = (await request.json()) as {
      name: string; gender: number; phone: string; email: string;
      username: string; password: string; paymentPassword: string;
    };
    const exists = registeredCustomers.find((c) => c.username === body.username);
    if (exists) return HttpResponse.json({ resCode: '20005', resMsg: '用户名已存在', data: null });

    const newId = Math.max(0, ...registeredCustomers.map((c) => c.id)) + 1;
    registeredCustomers.push({
      id: newId, name: body.name, gender: body.gender, phone: body.phone,
      email: body.email, username: body.username, password: body.password,
      paymentPassword: body.paymentPassword, balance: 0, status: 1,
    });

    return HttpResponse.json({ resCode: '10000', resMsg: '注册成功', data: { id: newId } });
  }),

  // ======================== 榜单 ========================
  http.get('/customer/api/board', () => {
    const playSales: Record<number, { name: string; poster: string; sales: number }> = {};
    for (const t of ticketsStore) {
      if (t.status === 2 || t.status === 3) {
        const s = getScheduleById(t.scheduleId);
        if (!s) continue;
        const play = getPlayById(s.playId);
        if (!play) continue;
        if (!playSales[play.id]) {
          playSales[play.id] = { name: play.name, poster: play.poster, sales: 0 };
        }
        playSales[play.id].sales += t.price;
      }
    }

    const list = Object.entries(playSales)
      .map(([pid, data]) => ({ playId: Number(pid), ...data }))
      .sort((a, b) => b.sales - a.sales)
      .slice(0, 10)
      .map((item, idx) => ({ rank: idx + 1, ...item, sales: Math.round(item.sales * 100) / 100 }));

    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: { list } });
  }),
];
