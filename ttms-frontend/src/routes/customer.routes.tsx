// 观众端路由表
/* eslint-disable react-refresh/only-export-components */

import { lazy } from 'react';
import type { RouteObject } from 'react-router-dom';

// 懒加载页面组件
const HomePage = lazy(() => import('@/pages/customer/Home'));
const SchedulePage = lazy(() => import('@/pages/customer/Schedule'));
const SeatsPage = lazy(() => import('@/pages/customer/Seats'));
const OrderPage = lazy(() => import('@/pages/customer/Order'));
const ResultPage = lazy(() => import('@/pages/customer/Result'));
const OrdersPage = lazy(() => import('@/pages/customer/Orders'));
const BoardPage = lazy(() => import('@/pages/customer/Board'));
const ContactPage = lazy(() => import('@/pages/customer/Contact'));
const CustomerLoginPage = lazy(() => import('@/pages/customer/Login'));
const RegisterPage = lazy(() => import('@/pages/customer/Register'));

const customerRoutes: RouteObject[] = [
  { index: true, element: <HomePage /> },
  { path: 'schedule', element: <SchedulePage /> },
  { path: 'seats/:scheduleId', element: <SeatsPage /> },
  { path: 'order', element: <OrderPage /> },
  { path: 'result/:orderId', element: <ResultPage /> },
  { path: 'orders', element: <OrdersPage /> },
  { path: 'board', element: <BoardPage /> },
  { path: 'contact', element: <ContactPage /> },
  { path: 'login', element: <CustomerLoginPage /> },
  { path: 'register', element: <RegisterPage /> },
];

export default customerRoutes;
