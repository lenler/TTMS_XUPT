// Axios 实例封装：拦截器与统一错误处理

import axios from 'axios';
import { message } from 'antd';

const request = axios.create({
  baseURL: '',           // Mock 期间用 MSW 拦截，不设真实 baseURL
  timeout: 10000,
});

// 请求拦截器：注入 token
request.interceptors.request.use((config) => {
  // 从 localStorage 读取 token（避免循环依赖 stores）
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
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
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
    message.error('网络请求失败');
    return Promise.reject(error);
  }
);

export default request;
