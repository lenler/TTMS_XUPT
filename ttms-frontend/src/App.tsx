// 根组件：路由挂载点——管理端 + 观众端

import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider, Spin } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import AdminLayout from '@/layouts/AdminLayout';
import CustomerLayout from '@/layouts/CustomerLayout';
import AdminGuard from '@/components/common/AdminGuard';
import CustomerGuard from '@/components/common/CustomerGuard';

// 页面懒加载
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

// 全局加载中组件
function PageLoading() {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
      <Spin size="large" />
    </div>
  );
}

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Suspense fallback={<PageLoading />}>
          <Routes>
            {/* ========== 管理端路由 ========== */}
            {/* 登录页——独立布局，不走 AdminLayout */}
            <Route path="/admin/login" element={<AdminLoginPage />} />

            {/* 受保护的管理端页面 */}
            <Route path="/admin" element={<AdminGuard />}>
              <Route element={<AdminLayout />}>
                <Route index element={<Navigate to="/admin/dashboard" replace />} />
                <Route path="dashboard" element={<DashboardPage />} />
                <Route path="studio" element={<StudioListPage />} />
                <Route path="studio/:id" element={<StudioDetailPage />} />
                <Route path="play" element={<PlayListPage />} />
                <Route path="play/:id" element={<PlayDetailPage />} />
                <Route path="schedule" element={<ScheduleListPage />} />
                <Route path="schedule/:id" element={<ScheduleDetailPage />} />
                <Route path="check" element={<CheckListPage />} />
                <Route path="sale" element={<SaleListPage />} />
                <Route path="sale/refund" element={<RefundPage />} />
                <Route path="employee" element={<EmployeeListPage />} />
                <Route path="employee/:id" element={<EmployeeDetailPage />} />
                <Route path="customer" element={<CustomerListPage />} />
                <Route path="role" element={<RoleListPage />} />
                <Route path="role/:id" element={<RoleDetailPage />} />
                <Route path="finance" element={<FinancePage />} />
              </Route>
            </Route>

            {/* ========== 观众端路由 ========== */}
            <Route element={<CustomerLayout />}>
              <Route index element={<HomePage />} />
              <Route path="schedule" element={<SchedulePage />} />
              <Route path="seats/:scheduleId" element={<SeatsPage />} />
              <Route path="order" element={<OrderPage />} />
              <Route path="result/:orderId" element={<ResultPage />} />
              <Route path="board" element={<BoardPage />} />
              <Route path="contact" element={<ContactPage />} />
              <Route path="login" element={<CustomerLoginPage />} />
              <Route path="register" element={<RegisterPage />} />

              {/* 需要登录的观众端页面 */}
              <Route element={<CustomerGuard />}>
                <Route path="orders" element={<OrdersPage />} />
              </Route>
            </Route>

            {/* 404 跳转首页 */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Suspense>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;
