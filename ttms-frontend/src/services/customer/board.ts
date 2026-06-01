// 观众端榜单 API 服务

import request from '../request';
import type { ApiResponse } from '@/types/api';

/** 榜单项 */
interface BoardItem {
  rank: number;
  playId: number;
  playName: string;
  poster: string;
  sales: number;
}

/** 榜单数据 */
interface BoardData {
  list: BoardItem[];
}

/** 获取票房榜单 */
export function getBoard(params?: { type?: string; limit?: number }): Promise<ApiResponse<BoardData>> {
  return request.get('/customer/api/board', { params });
}
