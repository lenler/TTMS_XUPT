// 观众端路由守卫：需要登录的页面（我的订单）未登录跳转登录页

import { Navigate, Outlet } from 'react-router-dom';

function CustomerGuard() {
  const customerToken = localStorage.getItem('customerToken');

  if (!customerToken) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

export default CustomerGuard;
