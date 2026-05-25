// 演出厅 Mock Handler

import { http, HttpResponse } from 'msw';
import type { Studio } from '@/types/models';

let studios: Studio[] = [
  { id: 1, name: '一号激光厅', rowCount: 10, colCount: 12, introduction: '适合首映礼和大型商业演出。', status: 1 },
  { id: 2, name: '二号杜比厅', rowCount: 8, colCount: 10, introduction: '主打沉浸式音效体验。', status: 1 },
  { id: 3, name: '三号小剧场', rowCount: 6, colCount: 8, introduction: '适合小型话剧和校园放映。', status: 1 },
];

/** 生成统一成功响应 */
function ok<T>(data: T, resMsg = '请求成功') {
  return HttpResponse.json({ resCode: '10000', resMsg, data });
}

/** 生成统一失败响应 */
function fail(resMsg: string, status = 400) {
  return HttpResponse.json({ resCode: '40000', resMsg, data: null }, { status });
}

/** 从请求地址中解析分页参数 */
function getPageParams(request: Request) {
  const url = new URL(request.url);
  return {
    page: Number(url.searchParams.get('page') || 1),
    pageSize: Number(url.searchParams.get('pageSize') || 10),
    keyword: url.searchParams.get('keyword') || '',
  };
}

export const studioHandlers = [
  http.get('/admin/api/studios', ({ request }) => {
    const { page, pageSize, keyword } = getPageParams(request);
    const matched = studios.filter((studio) => {
      const text = `${studio.name}${studio.introduction}`;
      return !keyword || text.includes(keyword);
    });
    const start = (page - 1) * pageSize;

    return ok({
      list: matched.slice(start, start + pageSize),
      total: matched.length,
      page,
      pageSize,
    });
  }),

  http.get('/admin/api/studios/:id', ({ params }) => {
    const id = Number(params.id);
    const studio = studios.find((item) => item.id === id);
    return studio ? ok(studio) : fail('演出厅不存在', 404);
  }),

  http.post('/admin/api/studios', async ({ request }) => {
    const body = (await request.json()) as Omit<Studio, 'id' | 'status'>;
    const id = Math.max(0, ...studios.map((item) => item.id)) + 1;
    studios = [{ ...body, id, status: 1 }, ...studios];
    return ok({ id }, '新增成功');
  }),

  http.put('/admin/api/studios/:id', async ({ request, params }) => {
    const id = Number(params.id);
    const body = (await request.json()) as Omit<Studio, 'id' | 'status'>;
    const index = studios.findIndex((item) => item.id === id);
    if (index < 0) {
      return fail('演出厅不存在', 404);
    }
    studios[index] = { ...studios[index], ...body };
    return ok(null, '修改成功');
  }),

  http.delete('/admin/api/studios/:id', ({ params }) => {
    const id = Number(params.id);
    const exists = studios.some((item) => item.id === id);
    if (!exists) {
      return fail('演出厅不存在', 404);
    }
    studios = studios.filter((item) => item.id !== id);
    return ok(null, '删除成功');
  }),
];
