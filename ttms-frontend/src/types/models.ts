// 类型定义：业务实体类型

import type { TicketStatus, SaleStatus, SaleType } from './enums';

/** 演出厅 */
export interface Studio {
  id: number;
  name: string;
  rowCount: number;
  colCount: number;
  introduction: string;
  status: number;
}

/** 剧目 */
export interface Play {
  id: number;
  typeId: number;
  typeName: string;
  langId: number;
  langName: string;
  name: string;
  introduction: string;
  poster: string;
  video: string;
  duration: number;
  basePrice: number;
  status: number;
}

/** 演出计划 */
export interface Schedule {
  id: number;
  playId: number;
  playName: string;
  studioId: number;
  studioName: string;
  showTime: string;
  ticketPrice: number;
  status: number;
}

/** 演出票 */
export interface Ticket {
  ticketId: number;
  seatId: number;
  seatRow: number;
  seatCol: number;
  price: number;
  status: TicketStatus;
  lockTime: string | null;
}

/** 座位 */
export interface Seat {
  id: number;
  row: number;
  col: number;
  status: number;  // 0 可用，-1 不可用
}

/** 员工 */
export interface Employee {
  id: number;
  employeeNo: string;
  name: string;
  gender: number;
  phone: string;
  email: string;
  positionId: number;
  positionName: string;
  status: number;
}

/** 销售明细 */
export interface SaleItem {
  id: number;
  ticketId: number;
  seatRow: number;
  seatCol: number;
  price: number;
}

/** 销售单 */
export interface Sale {
  id: number;
  employeeName: string;
  customerName: string;
  saleTime: string;
  paymentAmount: number;
  change: number;
  type: number;
  saleType: SaleType;
  status: SaleStatus;
  items: SaleItem[];
}

/** 菜单项 */
export interface MenuItem {
  name: string;
  icon?: string;
  url?: string;
  children?: MenuItem[];
}
