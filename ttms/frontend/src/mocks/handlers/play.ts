// 剧目 Mock Handler：完整 CRUD + 数据字典

import { http, HttpResponse } from 'msw';
import playsData from '../data/plays.json';

let plays = JSON.parse(JSON.stringify(playsData)) as Array<{
  id: number;
  typeId: number;
  typeName: string;
  langId: number;
  langName: string;
  name: string;
  introduction: string;
  poster: string;
  video: string;
  duration: number;
  basePrice: number;
  status: number;
}>;

let nextId = Math.max(0, ...plays.map((p) => p.id)) + 1;

export const playHandlers = [
  /** 查询剧目列表 */
  http.get('/admin/api/plays', ({ request }) => {
    const url = new URL(request.url);
    const keyword = url.searchParams.get('keyword') || '';
    const type = url.searchParams.get('type') || '';
    const lang = url.searchParams.get('lang') || '';
    const page = Number(url.searchParams.get('page')) || 1;
    const pageSize = Number(url.searchParams.get('pageSize')) || 10;

    let filtered = plays;
    if (keyword) {
      filtered = filtered.filter(
        (p) => p.name.includes(keyword) || p.introduction.includes(keyword)
      );
    }
    if (type) {
      filtered = filtered.filter((p) => p.typeId === Number(type));
    }
    if (lang) {
      filtered = filtered.filter((p) => p.langId === Number(lang));
    }

    const start = (page - 1) * pageSize;
    const list = filtered.slice(start, start + pageSize);

    return HttpResponse.json({
      resCode: '10000',
      resMsg: '请求成功',
      data: { list, total: filtered.length, page, pageSize },
    });
  }),

  /** 查询单个剧目 */
  http.get('/admin/api/plays/:id', ({ params }) => {
    const play = plays.find((p) => p.id === Number(params.id));
    if (!play) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: play });
  }),

  /** 新增剧目 */
  http.post('/admin/api/plays', async ({ request }) => {
    const body = (await request.json()) as {
      typeId: number;
      langId: number;
      name: string;
      introduction: string;
      poster: string;
      video: string;
      duration: number;
      basePrice: number;
    };
    const typeNames: Record<number, string> = { 1: '话剧', 2: '音乐剧', 3: '舞剧', 4: '戏曲' };
    const langNames: Record<number, string> = { 1: '中文', 2: '英文', 3: '法语', 4: '其他' };
    const newPlay = {
      ...body,
      id: nextId++,
      typeName: typeNames[body.typeId] || '未知',
      langName: langNames[body.langId] || '未知',
      status: 1,
    };
    plays.push(newPlay);
    return HttpResponse.json({ resCode: '10000', resMsg: '添加成功', data: { id: newPlay.id } });
  }),

  /** 修改剧目 */
  http.put('/admin/api/plays/:id', async ({ request, params }) => {
    const body = (await request.json()) as {
      typeId: number;
      langId: number;
      name: string;
      introduction: string;
      poster: string;
      video: string;
      duration: number;
      basePrice: number;
    };
    const idx = plays.findIndex((p) => p.id === Number(params.id));
    if (idx === -1) {
      return HttpResponse.json({ resCode: '20004', resMsg: '数据不存在', data: null });
    }
    const typeNames: Record<number, string> = { 1: '话剧', 2: '音乐剧', 3: '舞剧', 4: '戏曲' };
    const langNames: Record<number, string> = { 1: '中文', 2: '英文', 3: '法语', 4: '其他' };
    plays[idx] = {
      ...plays[idx],
      ...body,
      typeName: typeNames[body.typeId] || plays[idx].typeName,
      langName: langNames[body.langId] || plays[idx].langName,
    };
    return HttpResponse.json({ resCode: '10000', resMsg: '修改成功', data: null });
  }),

  /** 删除剧目 */
  http.delete('/admin/api/plays/:id', ({ params }) => {
    plays = plays.filter((p) => p.id !== Number(params.id));
    return HttpResponse.json({ resCode: '10000', resMsg: '删除成功', data: null });
  }),

  /** 数据字典：剧目类型 + 语种 */
  http.get('/admin/api/dicts', ({ request }) => {
    const url = new URL(request.url);
    const parentId = url.searchParams.get('parentId') || '';
    // 剧目类型
    if (parentId === 'type' || parentId === '') {
      return HttpResponse.json({
        resCode: '10000',
        resMsg: '请求成功',
        data: {
          list: [
            { id: 1, name: '话剧', value: 'drama' },
            { id: 2, name: '音乐剧', value: 'musical' },
            { id: 3, name: '舞剧', value: 'dance' },
            { id: 4, name: '戏曲', value: 'opera' },
          ],
        },
      });
    }
    // 语种
    if (parentId === 'lang' || parentId === '') {
      return HttpResponse.json({
        resCode: '10000',
        resMsg: '请求成功',
        data: {
          list: [
            { id: 1, name: '中文', value: 'chinese' },
            { id: 2, name: '英文', value: 'english' },
            { id: 3, name: '法语', value: 'french' },
            { id: 4, name: '其他', value: 'other' },
          ],
        },
      });
    }
    return HttpResponse.json({ resCode: '10000', resMsg: '请求成功', data: { list: [] } });
  }),
];
