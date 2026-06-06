// 通用数据格式化工具函数

/**
 * 金额格式化：保留两位小数，加 ¥ 前缀
 */
export function formatMoney(value: number | undefined | null): string {
  if (value === undefined || value === null) return '¥0.00';
  return `¥${value.toFixed(2)}`;
}

/**
 * 日期格式化：YYYY-MM-DD
 */
export function formatDate(dateStr: string | undefined | null): string {
  if (!dateStr) return '-';
  const d = new Date(dateStr);
  const pad = (n: number) => n.toString().padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

/**
 * 日期时间格式化：YYYY-MM-DD HH:mm
 */
export function formatDateTime(dateStr: string | undefined | null): string {
  if (!dateStr) return '-';
  const d = new Date(dateStr);
  const pad = (n: number) => n.toString().padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

/**
 * 时间格式化：HH:mm
 */
export function formatTime(dateStr: string | undefined | null): string {
  if (!dateStr) return '-';
  const d = new Date(dateStr);
  const pad = (n: number) => n.toString().padStart(2, '0');
  return `${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

/**
 * 手机号脱敏：138****1234
 */
export function maskPhone(phone: string | undefined | null): string {
  if (!phone || phone.length < 7) return phone || '-';
  return phone.substring(0, 3) + '****' + phone.substring(7);
}

/**
 * 上座率百分比格式化
 */
export function formatOccupancy(value: number | undefined | null): string {
  if (value === undefined || value === null) return '-';
  return `${(value * 100).toFixed(1)}%`;
}

/**
 * 数字千分位格式化
 */
export function formatNumber(value: number | undefined | null): string {
  if (value === undefined || value === null) return '0';
  return value.toLocaleString('zh-CN');
}
