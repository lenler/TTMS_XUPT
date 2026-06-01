// 管理端路由守卫：未登录跳转登录页

import { Navigate, Outlet } from 'react-router-dom';

function AdminGuard() {
  const token = localStorage.getItem('token');

  if (!token) {
    return <Navigate to="/admin/login" replace />;
  }

  return <Outlet />;
}

export default AdminGuard;
