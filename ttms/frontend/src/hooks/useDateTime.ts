// 日期时间工具 Hook —— 提供常用的日期格式化函数

import { useMemo } from 'react';

/**
 * 日期时间格式化 Hook
 * 封装常用日期格式化操作，避免重复编写格式化逻辑
 */
export function useDateTime() {
  const helpers = useMemo(() => ({
    /** 格式化为日期 YYYY-MM-DD */
    formatDate(dateStr?: string | null): string {
      if (!dateStr) return '-';
      const d = new Date(dateStr);
      const pad = (n: number) => n.toString().padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
    },

    /** 格式化为日期时间 YYYY-MM-DD HH:mm */
    formatDateTime(dateStr?: string | null): string {
      if (!dateStr) return '-';
      const d = new Date(dateStr);
      const pad = (n: number) => n.toString().padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    },

    /** 格式化为相对时间 */
    fromNow(dateStr?: string | null): string {
      if (!dateStr) return '-';
      const now = Date.now();
      const diff = now - new Date(dateStr).getTime();
      const minutes = Math.floor(diff / 60000);
      if (minutes < 1) return '刚刚';
      if (minutes < 60) return `${minutes}分钟前`;
      const hours = Math.floor(minutes / 60);
      if (hours < 24) return `${hours}小时前`;
      const days = Math.floor(hours / 24);
      if (days < 30) return `${days}天前`;
      return this.formatDate(dateStr);
    },
  }), []);

  return helpers;
}
