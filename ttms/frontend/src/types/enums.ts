// 类型定义：核心状态枚举

/** 票状态 */
export enum TicketStatus {
  Available = 0,   // 可售
  Locked = 1,      // 已锁定
  Sold = 2,        // 已售
  Checked = 3,     // 已验票
  Refunding = 4,   // 退票中
  Refunded = 5,    // 已退票
}

/** 订单状态 */
export enum SaleStatus {
  Unpaid = 0,      // 待支付
  Paid = 1,        // 已支付
  Refunding = 2,   // 退票中
  Refunded = 3,    // 已退票
  Cancelled = 4,   // 已取消
}

/** 销售类型 */
export enum SaleType {
  Online = 1,      // 网络购票
  Offline = 2,     // 线下售票
}
