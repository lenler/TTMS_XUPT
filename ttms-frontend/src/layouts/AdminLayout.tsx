// 管理端 SaaS 布局（侧边菜单 + 顶栏 + 内容区）

import { Outlet } from 'react-router-dom';
import { Layout } from 'antd';

const { Header, Sider, Content, Footer } = Layout;

function AdminLayout() {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible breakpoint="lg" theme="dark">
        {/* 菜单项后续动态加载 */}
        <div style={{ color: '#fff', textAlign: 'center', padding: 16 }}>
          TTMS
        </div>
      </Sider>
      <Layout>
        <Header style={{ background: '#fff', padding: '0 24px' }}>
          <span>汉唐剧院票务管理系统</span>
        </Header>
        <Content style={{ margin: 16 }}>
          <Outlet />
        </Content>
        <Footer style={{ textAlign: 'center' }}>
          TTMS &copy; 2026
        </Footer>
      </Layout>
    </Layout>
  );
}

export default AdminLayout;
