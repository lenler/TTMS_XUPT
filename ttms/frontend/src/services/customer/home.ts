// 观众端首页 API 服务

import request from '../request';
import type { ApiResponse } from '@/types/api';

/** 热卖剧目 */
interface HotPlay {
  id: number;
  name: string;
  poster: string;
  typeName: string;
  duration: number;
  basePrice: number;
  soldCount: number;
}

/** 近期演出 */
interface UpcomingSchedule {
  id: number;
  playId: number;
  playName: string;
  studioName: string;
  showTime: string;
  ticketPrice: number;
  availableSeats: number;
}

/** 首页数据 */
interface HomeData {
  hotPlays: HotPlay[];
  upcomingSchedules: UpcomingSchedule[];
}

/** 获取首页数据 */
export function getHome(): Promise<ApiResponse<HomeData>> {
  return request.get('/customer/api/home');
}
