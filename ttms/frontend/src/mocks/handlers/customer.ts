// 观众 Mock Handler：列表查询 + 封禁/解封
import { http, HttpResponse } from 'msw';
import customersData from '../data/customers.json';

interface Customer {
  id: number;
  name: string;
  gender: number;
  phone: string;
  email: string;
  username: string;
  balance: number;
  status: number;
}

let customers: Customer[] = JSON.parse(JSON.stringify(customersData));

export const customerHandlers = [
  /** 查询观众列表 */
  http.get('/admin/api/customers', ({ request }) => {
    const url = new URL(request.url);
    const keyword = url.searchParams.get('keyword') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = customers;
    if (keyword) {
      filtered = filtered.filter(
        (c) => c.name.includes(keyword) || c.username.includes(keyword) || c.phone.includes(keyword)
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

  /** 查看观众详情 */
  http.get('/admin/api/customers/:id', ({ params }) => {
    const customer = customers.find((c) => c.id === Number(params.id));
    if (!customer) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: customer });
  }),

  /** 封禁/解封观众 */
  http.put('/admin/api/customers/:id/status', async ({ request, params }) => {
    const body = (await request.json()) as { status: number };
    const idx = customers.findIndex((c) => c.id === Number(params.id));
    if (idx === -1) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    customers[idx].status = body.status;
    const msg = body.status === 1 ? '解封成功' : '封禁成功';
    return HttpResponse.json({ resCode: '10000', resMsg: msg, data: null });
  }),
];
