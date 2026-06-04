// 管理端路由表

import { lazy } from 'react';
import type { RouteObject } from 'react-router-dom';
import AdminLayout from '@/layouts/AdminLayout';
import AdminGuard from '@/components/common/AdminGuard';

const AdminLoginPage = lazy(() => import('@/pages/admin/Login'));
const DashboardPage = lazy(() => import('@/pages/admin/Dashboard'));
const StudioListPage = lazy(() => import('@/pages/admin/Studio/index'));
// StudioDetail 改为 Modal，不在路由中独立使用
const PlayListPage = lazy(() => import('@/pages/admin/Play/index'));
const ScheduleListPage = lazy(() => import('@/pages/admin/Schedule/index'));
const CheckListPage = lazy(() => import('@/pages/admin/Check/index'));
const SaleListPage = lazy(() => import('@/pages/admin/Sale/index'));
const RefundPage = lazy(() => import('@/pages/admin/Sale/Refund'));
const EmployeeListPage = lazy(() => import('@/pages/admin/Employee/index'));
// EmployeeDetail 改为 Modal，不在路由中独立使用
const CustomerListPage = lazy(() => import('@/pages/admin/Customer/index'));
const RoleListPage = lazy(() => import('@/pages/admin/Role/index'));
// RoleDetail 改为 Modal，不在路由中独立使用
const FinancePage = lazy(() => import('@/pages/admin/Finance/index'));
const AboutPage = lazy(() => import('@/pages/admin/About/index'));

/** 管理端完整路由配置 */
const adminRoutes: RouteObject[] = [
  {
    path: '/admin/login',
    element: <AdminLoginPage />,
  },
  {
    path: '/admin',
    element: <AdminGuard />,
    children: [
      {
        element: <AdminLayout />,
        children: [
          { index: true, element: <DashboardPage /> },
          { path: 'dashboard', element: <DashboardPage /> },
          { path: 'studio', element: <StudioListPage /> },
          { path: 'play', element: <PlayListPage /> },
          { path: 'schedule', element: <ScheduleListPage /> },
          { path: 'check', element: <CheckListPage /> },
          { path: 'sale', element: <SaleListPage /> },
          { path: 'sale/refund', element: <RefundPage /> },
          { path: 'employee', element: <EmployeeListPage /> },
          { path: 'customer', element: <CustomerListPage /> },
          { path: 'role', element: <RoleListPage /> },
          { path: 'finance', element: <FinancePage /> },
          { path: 'about', element: <AboutPage /> },
        ],
      },
    ],
  },
];

export default adminRoutes;
