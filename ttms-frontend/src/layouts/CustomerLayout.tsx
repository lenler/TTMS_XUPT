// 观众端布局（页头导航 + 页脚 + 内容区）

import { Link, Outlet, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import { HomeOutlined, CalendarOutlined, TrophyOutlined, ContactsOutlined, UserOutlined } from '@ant-design/icons';

const { Header, Content, Footer } = Layout;

const navItems = [
  { key: '/', icon: <HomeOutlined />, label: <Link to="/">首页</Link> },
  { key: '/schedule', icon: <CalendarOutlined />, label: <Link to="/schedule">放映安排</Link> },
  { key: '/board', icon: <TrophyOutlined />, label: <Link to="/board">榜单</Link> },
  { key: '/contact', icon: <ContactsOutlined />, label: <Link to="/contact">联系我们</Link> },
];

function CustomerLayout() {
  const location = useLocation();
  // 观众端使用独立的 token 存储
  const customerToken = localStorage.getItem('customerToken');

  return (
    <Layout>
      <Header
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          background: '#001529',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
          <Link
            to="/"
            style={{ color: '#fff', fontSize: 18, fontWeight: 'bold', textDecoration: 'none' }}
          >
            汉唐剧院
          </Link>
          <Menu
            theme="dark"
            mode="horizontal"
            selectedKeys={[location.pathname === '/' ? '/' : '/' + location.pathname.split('/')[1]]}
            items={navItems}
            style={{ flex: 1, minWidth: 400 }}
          />
        </div>
        <div>
          {customerToken ? (
            <>
              <Link to="/orders" style={{ color: '#fff', marginRight: 16 }}>
                <UserOutlined /> 我的订单
              </Link>
              <Link
                to="/login"
                style={{ color: '#fff' }}
                onClick={() => localStorage.removeItem('customerToken')}
              >
                退出
              </Link>
            </>
          ) : (
            <>
              <Link to="/login" style={{ color: '#fff', marginRight: 16 }}>
                登录
              </Link>
              <Link to="/register" style={{ color: '#fff' }}>
                注册
              </Link>
            </>
          )}
        </div>
      </Header>
      <Content style={{ minHeight: 'calc(100vh - 134px)', padding: 24, background: '#f5f5f5' }}>
        <Outlet />
      </Content>
      <Footer style={{ textAlign: 'center' }}>
        TTMS &copy; 2026 汉唐传媒有限公司
      </Footer>
    </Layout>
  );
}

export default CustomerLayout;
