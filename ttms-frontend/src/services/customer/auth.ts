// 观众端认证 API 服务

import axios from 'axios';

export interface CustomerSession {
  id: number;
  username: string;
  name: string;
}

async function unwrap<T>(promise: Promise<{ data: { code: string; message: string; data: T } }>) {
  const res = await promise;
  if (res.data.code !== '10000') {
    throw new Error(res.data.message);
  }
  return res.data.data;
}

export async function customerLogin(data: { username: string; password: string }) {
  const auth = await unwrap<{ id: number; username: string; name: string }>(
    axios.post('/auth/login', { ...data, userType: 'customer' })
  );
  const session: CustomerSession = { id: auth.id, username: auth.username, name: auth.name };
  localStorage.setItem('customerToken', `customer-${auth.id}`);
  localStorage.setItem('customer', JSON.stringify(session));
  return session;
}

export async function customerRegister(data: { username: string; password: string; name: string; phone?: string; email?: string }) {
  return unwrap<CustomerSession>(axios.post('/auth/customers/register', data));
}

export function getCustomerSession(): CustomerSession | null {
  return JSON.parse(localStorage.getItem('customer') || 'null');
}
