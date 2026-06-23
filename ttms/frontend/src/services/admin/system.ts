// 系统信息 API 服务
import request from '../request';
import type { ApiResponse } from '@/types/api';

/** 系统信息类型 */
export interface SystemInfo {
  name: string;
  nameEn: string;
  abbr: string;
  version: string;
  description: string;
  team: string;
  developer: string;
  javaVersion: string;
  osInfo: string;
  startupTime: string;
  timestamp: string;
}

/** 获取系统信息 */
export function getSystemInfo(): Promise<ApiResponse<SystemInfo>> {
  return request.get('/api/system/info');
}

/** 健康检查 */
export function getHealth(): Promise<ApiResponse<{ status: string; timestamp: string }>> {
  return request.get('/api/system/health');
}
