// 演出厅 Mock Handler：完整 CRUD + 座位设置

import { http, HttpResponse } from 'msw';
import studiosData from '../data/studios.json';

let studios = JSON.parse(JSON.stringify(studiosData)) as Array<{
  id: number;
  name: string;
  rowCount: number;
  colCount: number;
  introduction: string;
  status: number;
}>;

export const studioHandlers = [
  /** 查询演出厅列表 */
  http.get('/admin/api/studios', ({ request }) => {
    const url = new URL(request.url);
    const keyword = url.searchParams.get('keyword') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = studios;
    if (keyword) {
      filtered = studios.filter(
        (s) => s.name.includes(keyword) || s.introduction.includes(keyword)
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

  /** 查询单个演出厅 */
  http.get('/admin/api/studios/:id', ({ params }) => {
    const studio = studios.find((s) => s.id === Number(params.id));
    if (!studio) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: studio });
  }),

  /** 新增演出厅 */
  http.post('/admin/api/studios', async ({ request }) => {
    const body = (await request.json()) as {
      name: string;
      rowCount: number;
      colCount: number;
      introduction: string;
    };
    const newId = Math.max(0, ...studios.map((s) => s.id)) + 1;
    const newStudio = { ...body, id: newId, status: 1 };
    studios.push(newStudio);
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '添加成功',
      data: { id: newId },
    });
  }),

  /** 修改演出厅 */
  http.put('/admin/api/studios/:id', async ({ request, params }) => {
    const body = (await request.json()) as {
      name: string;
      rowCount: number;
      colCount: number;
      introduction: string;
    };
    const idx = studios.findIndex((s) => s.id === Number(params.id));
    if (idx === -1) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    studios[idx] = { ...studios[idx], ...body };
    return HttpResponse.json({ resCode: '10000', resMsg: '修改成功', data: null });
  }),

  /** 删除演出厅 */
  http.delete('/admin/api/studios/:id', ({ params }) => {
    studios = studios.filter((s) => s.id !== Number(params.id));
    return HttpResponse.json({ resCode: '10000', resMsg: '删除成功', data: null });
  }),

  /** 设置座位 */
  http.put('/admin/api/studios/:id/seats', async ({ request, params }) => {
    const body = (await request.json()) as { seatRows: string[] };
    const studio = studios.find((s) => s.id === Number(params.id));
    if (!studio) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    // 同步更新演出厅的行列数
    studio.rowCount = body.seatRows.length;
    studio.colCount = body.seatRows[0]?.length ?? 0;
    return HttpResponse.json({
      resCode: '10000',
      resMsg: '座位设置成功',
      data: {
        studioId: studio.id,
        rowCount: studio.rowCount,
        colCount: studio.colCount,
        layout: body.seatRows,
      },
    });
  }),
];
