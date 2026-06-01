// 售票 Mock Handler：售票 + 销售记录查询 + 退票

import { http, HttpResponse } from 'msw';
import { ticketsStore } from './schedule';
import type { TicketStore } from './schedule';

interface SaleItem {
  id: number;
  ticketId: number;
  seatRow: number;
  seatCol: number;
  price: number;
}

interface Sale {
  id: number;
  employeeName: string;
  customerId: number;
  customerName: string;
  saleTime: string;
  paymentAmount: number;
  change: number;
  type: number;    // 1=销售, 2=退票
  saleType: number; // 1=网络, 2=线下
  status: number;   // 0=待支付, 1=已支付, 2=退票中, 3=已退票, 4=已取消
  items: SaleItem[];
}

let sales: Sale[] = [];
let saleNextId = 20260001;
let saleItemNextId = 1;

export const saleHandlers = [
  /** 查询销售记录 */
  http.get('/admin/api/sales', ({ request }) => {
    const url = new URL(request.url);
    const keyword = url.searchParams.get('keyword') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = sales;
    if (keyword) {
      filtered = filtered.filter(
        (s) =>
          String(s.id).includes(keyword) ||
          s.customerName.includes(keyword) ||
          s.employeeName.includes(keyword)
      );
    }

    const start = (page - 1) * pageSize;
    const list = filtered.slice(start, start + pageSize);

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { list, total: filtered.length, page, pageSize },
    });
  }),

  /** 查询单个销售单 */
  http.get('/admin/api/sales/:id', ({ params }) => {
    const sale = sales.find((s) => s.id === Number(params.id));
    if (!sale) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: sale });
  }),

  /** 线下售票 */
  http.post('/admin/api/sales', async ({ request }) => {
    const body = (await request.json()) as {
      scheduleId: number;
      ticketIds: number[];
      customerId: number;
      paymentAmount: number;
    };

    // 校验：所有票必须为 available 且属于同一场次
    const targetTickets: TicketStore[] = [];
    for (const tid of body.ticketIds) {
      const ticket = ticketsStore.find((t) => t.ticketId === tid);
      if (!ticket) {
        return HttpResponse.json({
          resCode: '20004',
          resMsg: `票 ${tid} 不存在`,
          data: null,
        });
      }
      if (ticket.status !== 0) {
        return HttpResponse.json({
          resCode: '20005',
          resMsg: `票 ${tid} 不可售（当前状态: ${ticket.status}）`,
          data: null,
        });
      }
      if (ticket.scheduleId !== body.scheduleId) {
        return HttpResponse.json({
          resCode: '20005',
          resMsg: `票 ${tid} 不属于所选场次`,
          data: null,
        });
      }
      targetTickets.push(ticket);
    }

    // 计算总价
    const totalPrice = targetTickets.reduce((sum, t) => sum + t.price, 0);
    const change = body.paymentAmount - totalPrice;
    if (change < 0) {
      return HttpResponse.json({
        resCode: '20005',
        resMsg: `收款不足，还需 ¥${Math.abs(change).toFixed(2)}`,
        data: null,
      });
    }

    // 更新票状态为 sold
    for (const t of targetTickets) {
      t.status = 2; // sold
    }

    // 创建 SaleItem 列表
    const items: SaleItem[] = targetTickets.map((t) => ({
      id: saleItemNextId++,
      ticketId: t.ticketId,
      seatRow: t.seatRow,
      seatCol: t.seatCol,
      price: t.price,
    }));

    // 创建 Sale
    const newSale: Sale = {
      id: saleNextId++,
      employeeName: '当前售票员',
      customerId: body.customerId,
      customerName: `顾客${body.customerId}`,
      saleTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
      paymentAmount: body.paymentAmount,
      change,
      type: 1,
      saleType: 2, // 线下
      status: 1,  // 已支付
      items,
    };
    sales.unshift(newSale);

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '售票成功',
      data: { saleId: newSale.id, change },
    });
  }),

  /** 退票 */
  http.post('/admin/api/sales/:id/refund', async ({ request, params }) => {
    const body = (await request.json()) as { ticketIds: number[] };
    const sale = sales.find((s) => s.id === Number(params.id));
    if (!sale) {
      return HttpResponse.json({ resCode: '20004', resMsg: '订单不存在', data: null });
    }
    if (sale.status !== 1) {
      return HttpResponse.json({
        resCode: '20005',
        resMsg: '仅已支付订单可退票',
        data: null,
      });
    }

    // 校验要退的票
    let refundAmount = 0;
    const refundedTicketIds: number[] = [];
    for (const tid of body.ticketIds) {
      const item = sale.items.find((it) => it.ticketId === tid);
      if (!item) {
        return HttpResponse.json({
          resCode: '20004',
          resMsg: `票 ${tid} 不在该订单中`,
          data: null,
        });
      }
      const ticket = ticketsStore.find((t) => t.ticketId === tid);
      if (!ticket || ticket.status !== 2) {
        return HttpResponse.json({
          resCode: '20005',
          resMsg: `票 ${tid} 状态不可退`,
          data: null,
        });
      }
      // 更新票状态：sold → refunded → available
      ticket.status = 5; // refunded
      refundAmount += item.price;
      refundedTicketIds.push(tid);
    }

    // 更新订单状态
    const remainingItems = sale.items.filter((it) => !body.ticketIds.includes(it.ticketId));
    if (remainingItems.length === 0) {
      sale.status = 3; // 全额退票
    } else {
      sale.status = 1; // 部分退票，仍为已支付
    }
    sale.items = remainingItems;

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '退票成功',
      data: {
        saleId: sale.id,
        refundedTickets: refundedTicketIds,
        refundAmount,
        orderStatus: sale.status === 3 ? 'refunded' : 'paid',
        ticketStatus: 'refunded',
      },
    });
  }),
];
