// Axios 实例封装：拦截器与统一错误处理

import axios from 'axios';
import { message } from 'antd';

const request = axios.create({
  baseURL: '',           // Mock 期间用 MSW 拦截，不设真实 baseURL
  timeout: 10000,
});

// 请求拦截器：注入 token（管理端 / 观众端分别注入对应 token）
request.interceptors.request.use((config) => {
  const url = config.url || '';
  if (url.startsWith('/customer/')) {
    // 观众端请求 —— 注入 customerToken
    const customerToken = localStorage.getItem('customerToken');
    if (customerToken) {
      config.headers.Authorization = `Bearer ${customerToken}`;
    }
  } else {
    // 管理端请求 —— 注入管理端 token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  (response) => {
    const { resCode, resMsg } = response.data;
    if (resCode !== '10000') {
      message.error(resMsg);
      return Promise.reject(new Error(resMsg));
    }
    return response.data;           // 只返回 { resCode, resMsg, data }
  },
  (error) => {
    const serverMessage =
      error.response?.data?.resMsg ||
      error.response?.data?.message ||
      error.message ||
      '网络请求失败';
    if (error.response?.status === 401) {
      const url = error.config?.url || '';
      if (url.startsWith('/customer/')) {
        localStorage.removeItem('customerToken');
        localStorage.removeItem('customerInfo');
      } else {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    message.error(serverMessage);
    return Promise.reject(new Error(serverMessage));
  }
);

export default request;
