// 观众端榜单 API 服务

import { getPublicSchedules } from './schedule';

export async function getBoxOfficeBoard() {
  const schedules = await getPublicSchedules();
  return schedules
    .map((schedule) => ({
      id: schedule.id,
      playName: schedule.playName,
      studioName: schedule.studioName,
      showTime: schedule.showTime,
      ticketPrice: schedule.ticketPrice,
    }))
    .sort((a, b) => a.playName.localeCompare(b.playName));
}
