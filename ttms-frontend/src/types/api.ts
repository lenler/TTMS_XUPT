// 类型定义：API 响应类型

/** 统一响应包裹 */
export interface ApiResponse<T = unknown> {
  resCode: string;
  resMsg: string;
  data: T;
}

/** 分页请求参数 */
export interface PageParams {
  page: number;
  pageSize: number;
  keyword?: string;
}

/** 分页响应 data */
export interface PageData<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}
