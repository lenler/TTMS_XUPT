// 前端应用常量定义

/** 系统名称 */
export const APP_NAME = '票枢Core · 剧目票务管理系统';

/** 系统简称 */
export const APP_ABBR = 'TTMS';

/** 版本号 */
export const APP_VERSION = '0.1.0';

/** 开发团队 */
export const APP_TEAM = 'HanTang Studio';

/** 本地存储 key */
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER: 'user',
  CUSTOMER_TOKEN: 'customerToken',
  CUSTOMER_INFO: 'customerInfo',
} as const;

/** 分页默认值 */
export const DEFAULT_PAGE_SIZE = 10;

/** 演出类型映射 */
export const PLAY_TYPES: Record<number, string> = {
  1: '话剧',
  2: '音乐剧',
  3: '戏曲',
  4: '儿童剧',
};

/** 票据状态 */
export const TICKET_STATUS = {
  AVAILABLE: '待售',
  LOCKED: '已锁定',
  SOLD: '已售',
  CHECKED: '已验票',
  REFUNDED: '已退票',
} as const;
