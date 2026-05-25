// 观众端布局（页头导航 + 页脚 + 内容区）

import { Outlet } from 'react-router-dom';
import { Layout } from 'antd';

const { Header, Content, Footer } = Layout;

function CustomerLayout() {
  return (
    <Layout>
      <Header style={{ background: '#001529', display: 'flex', alignItems: 'center' }}>
        <span style={{ color: '#fff', fontSize: 18, fontWeight: 'bold' }}>
          汉唐剧院
        </span>
        {/* 导航菜单后续添加 */}
      </Header>
      <Content style={{ minHeight: 'calc(100vh - 134px)', padding: 24 }}>
        <Outlet />
      </Content>
      <Footer style={{ textAlign: 'center' }}>
        TTMS &copy; 2026 汉唐传媒有限公司
      </Footer>
    </Layout>
  );
}

export default CustomerLayout;
