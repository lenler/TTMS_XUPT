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

/** 水墨留白主题 token */
const themeToken = {
  // 主色：暗金点缀
  colorPrimary: '#C9A96E',
  colorPrimaryHover: '#B8944F',
  colorPrimaryActive: '#A8813A',
  // 成功/警告/错误保留默认，只微调文字色
  colorSuccess: '#52c41a',
  colorWarning: '#fa8c16',
  colorError: '#ff4d4f',
  // 文字
  colorText: '#2c2c2c',
  colorTextSecondary: '#666666',
  colorTextTertiary: '#999999',
  colorTextQuaternary: '#bfbfbf',
  // 背景
  colorBgLayout: '#FAFAF7',
  colorBgContainer: '#F5F0EB',
  colorBgElevated: '#F5F0EB',
  // 边框
  colorBorder: '#E8E0D5',
  colorBorderSecondary: '#E8E0D5',
  // 圆角
  borderRadius: 4,
  borderRadiusSM: 2,
  borderRadiusLG: 8,
  // 字号
  fontSize: 14,
  fontSizeHeading1: 28,
  fontSizeHeading2: 24,
  fontSizeHeading3: 20,
  fontSizeHeading4: 18,
  fontSizeHeading5: 16,
  // 字体
  fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
  // 链接
  colorLink: '#C9A96E',
  colorLinkHover: '#B8944F',
  // 输入框
  controlHeight: 36,
  lineWidth: 1,
  lineType: 'solid',
};

function App() {
  return (
    <ConfigProvider locale={zhCN} theme={{ token: themeToken }}>
      <Suspense fallback={<PageLoading />}>
        <RouterProvider router={router} />
      </Suspense>
    </ConfigProvider>
  );
}

export default App;
