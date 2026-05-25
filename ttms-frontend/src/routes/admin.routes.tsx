// 管理端路由表
/* eslint-disable react-refresh/only-export-components */

import { lazy } from 'react';
import type { RouteObject } from 'react-router-dom';

// 懒加载页面组件
const AdminLoginPage = lazy(() => import('@/pages/admin/Login'));
const DashboardPage = lazy(() => import('@/pages/admin/Dashboard'));
const StudioListPage = lazy(() => import('@/pages/admin/Studio/index'));
const StudioDetailPage = lazy(() => import('@/pages/admin/Studio/Detail'));
const PlayListPage = lazy(() => import('@/pages/admin/Play/index'));
const PlayDetailPage = lazy(() => import('@/pages/admin/Play/Detail'));
const ScheduleListPage = lazy(() => import('@/pages/admin/Schedule/index'));
const ScheduleDetailPage = lazy(() => import('@/pages/admin/Schedule/Detail'));
const CheckListPage = lazy(() => import('@/pages/admin/Check/index'));
const SaleListPage = lazy(() => import('@/pages/admin/Sale/index'));
const RefundPage = lazy(() => import('@/pages/admin/Sale/Refund'));
const EmployeeListPage = lazy(() => import('@/pages/admin/Employee/index'));
const EmployeeDetailPage = lazy(() => import('@/pages/admin/Employee/Detail'));
const CustomerListPage = lazy(() => import('@/pages/admin/Customer/index'));
const RoleListPage = lazy(() => import('@/pages/admin/Role/index'));
const RoleDetailPage = lazy(() => import('@/pages/admin/Role/Detail'));
const FinancePage = lazy(() => import('@/pages/admin/Finance/index'));

const adminRoutes: RouteObject[] = [
  { path: 'login', element: <AdminLoginPage /> },
  { path: 'dashboard', element: <DashboardPage /> },
  { path: 'studio', element: <StudioListPage /> },
  { path: 'studio/:id', element: <StudioDetailPage /> },
  { path: 'play', element: <PlayListPage /> },
  { path: 'play/:id', element: <PlayDetailPage /> },
  { path: 'schedule', element: <ScheduleListPage /> },
  { path: 'schedule/:id', element: <ScheduleDetailPage /> },
  { path: 'check', element: <CheckListPage /> },
  { path: 'sale', element: <SaleListPage /> },
  { path: 'sale/refund', element: <RefundPage /> },
  { path: 'employee', element: <EmployeeListPage /> },
  { path: 'employee/:id', element: <EmployeeDetailPage /> },
  { path: 'customer', element: <CustomerListPage /> },
  { path: 'role', element: <RoleListPage /> },
  { path: 'role/:id', element: <RoleDetailPage /> },
  { path: 'finance', element: <FinancePage /> },
];

export default adminRoutes;
