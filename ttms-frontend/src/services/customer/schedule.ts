// 观众端排片 API 服务

import request from '../request';
import type { ApiResponse, PageData, PageParams } from '@/types/api';

/** 放映列表项 */
interface ScheduleItem {
  id: number;
  playId: number;
  playName: string;
  playPoster: string;
  playType: string;
  playDuration: number;
  studioId: number;
  studioName: string;
  showTime: string;
  ticketPrice: number;
  availableSeats: number;
}

/** 座位（观众端视角） */
interface ScheduleSeat {
  id: number;
  row: number;
  col: number;
  status: number; // 0=available, 1=locked, 2=sold
}

/** 演出详情 */
interface ScheduleDetail {
  id: number;
  play: {
    id: number;
    name: string;
    poster: string;
    typeName: string;
    langName: string;
    introduction: string;
    duration: number;
  };
  studio: {
    id: number;
    name: string;
    introduction: string;
  };
  showTime: string;
  ticketPrice: number;
  seats: ScheduleSeat[];
  seatLayout: string[];
}

/** 查询放映安排列表 */
export function getSchedules(
  params: PageParams & { playId?: number; date?: string }
): Promise<ApiResponse<PageData<ScheduleItem>>> {
  return request.get('/customer/api/schedules', { params });
}

/** 查询演出详情（含座位） */
export function getScheduleById(id: number): Promise<ApiResponse<ScheduleDetail>> {
  return request.get(`/customer/api/schedules/${id}`);
}
