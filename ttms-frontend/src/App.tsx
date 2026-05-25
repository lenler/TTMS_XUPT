// 根组件：路由挂载点

import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
          {/* 默认跳转管理端（后续添加完整路由） */}
          <Route path="/" element={<Navigate to="/admin" replace />} />
          <Route path="/admin" element={<div style={{ padding: 24 }}>管理端首页（待实现）</div>} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;
