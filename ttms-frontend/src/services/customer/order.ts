// 观众端订单 API 服务

import axios from 'axios';
import { getCustomerSession } from './auth';

async function unwrap<T>(promise: Promise<{ data: { code: string; message: string; data: T } }>) {
  const res = await promise;
  if (res.data.code !== '10000') {
    throw new Error(res.data.message);
  }
  return res.data.data;
}

export interface CustomerOrder {
  id: number;
  saleTime: string;
  amountPayable: number;
  paidAmount: number;
  changeAmount: number;
  status: string;
  items: { ticketId: number; rowNo: number; colNo: number; price: number }[];
}

export async function createCustomerOrder(ticketIds: number[], paidAmount: number) {
  const customer = getCustomerSession();
  if (!customer) {
    throw new Error('请先登录');
  }
  const order = await unwrap<CustomerOrder>(
    axios.post('/sales/orders', { customerId: customer.id, employeeId: null, ticketIds })
  );
  return unwrap<CustomerOrder>(axios.post(`/sales/${order.id}/payments`, { paidAmount }));
}

export async function getCustomerOrders() {
  const customer = getCustomerSession();
  if (!customer) {
    return [];
  }
  return unwrap<CustomerOrder[]>(axios.get('/sales', { params: { customerId: customer.id } }));
}

export async function refundOrder(id: number) {
  return unwrap<CustomerOrder>(axios.post(`/sales/${id}/refund`));
}
