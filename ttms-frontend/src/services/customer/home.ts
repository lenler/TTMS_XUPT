// 观众端首页 API 服务

import { getPublicPlays, getPublicSchedules } from './schedule';

export async function getHomeData() {
  const [plays, schedules] = await Promise.all([
    getPublicPlays(),
    getPublicSchedules(),
  ]);
  return { plays: plays.slice(0, 6), schedules: schedules.slice(0, 6) };
}
