// 观众端路由表

import { lazy } from 'react';
import type { RouteObject } from 'react-router-dom';
import CustomerLayout from '@/layouts/CustomerLayout';
import CustomerGuard from '@/components/common/CustomerGuard';

const HomePage = lazy(() => import('@/pages/customer/Home'));
const SchedulePage = lazy(() => import('@/pages/customer/Schedule'));
const SeatsPage = lazy(() => import('@/pages/customer/Seats'));
const OrderPage = lazy(() => import('@/pages/customer/Order'));
const ResultPage = lazy(() => import('@/pages/customer/Result'));
const OrdersPage = lazy(() => import('@/pages/customer/Orders'));
const WalletPage = lazy(() => import('@/pages/customer/Wallet'));
const BoardPage = lazy(() => import('@/pages/customer/Board'));
const ContactPage = lazy(() => import('@/pages/customer/Contact'));
const CustomerLoginPage = lazy(() => import('@/pages/customer/Login'));
const RegisterPage = lazy(() => import('@/pages/customer/Register'));

/** 观众端完整路由配置 */
const customerRoutes: RouteObject[] = [
  {
    element: <CustomerLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: 'schedule', element: <SchedulePage /> },
      { path: 'board', element: <BoardPage /> },
      { path: 'contact', element: <ContactPage /> },
      { path: 'login', element: <CustomerLoginPage /> },
      { path: 'register', element: <RegisterPage /> },
      // 需要登录才能访问（购票流程 + 我的订单）
      {
        element: <CustomerGuard />,
        children: [
          { path: 'seats/:scheduleId', element: <SeatsPage /> },
          { path: 'order', element: <OrderPage /> },
          { path: 'result/:orderId', element: <ResultPage /> },
          { path: 'orders', element: <OrdersPage /> },
          { path: 'wallet', element: <WalletPage /> },
        ],
      },
    ],
  },
];

export default customerRoutes;
