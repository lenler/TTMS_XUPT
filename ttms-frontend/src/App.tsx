// 根组件：createBrowserRouter 路由挂载

import { Suspense } from 'react';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { ConfigProvider, Spin } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import adminRoutes from '@/routes/admin.routes';
import customerRoutes from '@/routes/customer.routes';

/** 全局加载中组件 */
function PageLoading() {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
      <Spin size="large" />
    </div>
  );
}

/** 创建路由 */
const router = createBrowserRouter(
  [
    ...adminRoutes,
    ...customerRoutes,
    // 404 兜底
    { path: '*', element: <Navigate to="/" replace /> },
  ],
  {
    future: {
      v7_startTransition: true,
    },
  }
);

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <Suspense fallback={<PageLoading />}>
        <RouterProvider router={router} />
      </Suspense>
    </ConfigProvider>
  );
}

export default App;
