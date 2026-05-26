// 管理端 SaaS 布局（侧边菜单 + 顶栏用户信息 + 内容区 + 退出）

import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button, theme } from 'antd';
import {
  HomeOutlined,
  BankOutlined,
  TableOutlined,
  VideoCameraOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  DollarOutlined,
  TeamOutlined,
  UserOutlined,
  SafetyCertificateOutlined,
  BarChartOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { useAuthStore } from '@/stores/authStore';

const { Header, Sider, Content, Footer } = Layout;

// 静态菜单配置（Mock 阶段使用，后续从接口加载）
const menuItems = [
  {
    key: '/admin/dashboard',
    icon: <HomeOutlined />,
    label: '工作台',
  },
  {
    key: 'theater',
    icon: <BankOutlined />,
    label: '剧院管理',
    children: [
      { key: '/admin/studio', icon: <TableOutlined />, label: '演出厅管理' },
      { key: '/admin/play', icon: <VideoCameraOutlined />, label: '剧目管理' },
      { key: '/admin/schedule', icon: <CalendarOutlined />, label: '演出计划' },
      { key: '/admin/check', icon: <CheckCircleOutlined />, label: '验票管理' },
    ],
  },
  {
    key: 'ticket',
    icon: <DollarOutlined />,
    label: '票务管理',
    children: [
      { key: '/admin/sale', icon: <DollarOutlined />, label: '售票记录' },
      { key: '/admin/sale/refund', icon: <DollarOutlined />, label: '退票处理' },
    ],
  },
  {
    key: 'user',
    icon: <TeamOutlined />,
    label: '用户管理',
    children: [
      { key: '/admin/employee', icon: <UserOutlined />, label: '员工管理' },
      { key: '/admin/customer', icon: <TeamOutlined />, label: '观众管理' },
    ],
  },
  {
    key: 'perm',
    icon: <SafetyCertificateOutlined />,
    label: '权限管理',
    children: [
      { key: '/admin/role', icon: <SafetyCertificateOutlined />, label: '角色管理' },
    ],
  },
  {
    key: '/admin/finance',
    icon: <BarChartOutlined />,
    label: '财务管理',
  },
];

function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuthStore();
  const { token: themeToken } = theme.useToken();

  // 根据当前路径计算选中的菜单项（/admin 映射到 /admin/dashboard）
  const selectedKey = location.pathname === '/admin'
    ? '/admin/dashboard'
    : location.pathname;
  const openKeys = menuItems
    .filter((item) => 'children' in item)
    .map((item) => item.key);

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  const handleLogout = () => {
    logout();
    navigate('/admin/login');
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        breakpoint="lg"
        theme="dark"
        width={220}
      >
        <div
          style={{
            height: 48,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontSize: collapsed ? 16 : 18,
            fontWeight: 'bold',
            borderBottom: '1px solid rgba(255,255,255,0.1)',
          }}
        >
          {collapsed ? 'TT' : 'TTMS'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          defaultOpenKeys={openKeys}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            background: themeToken.colorBgContainer,
            padding: '0 24px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            borderBottom: `1px solid ${themeToken.colorBorderSecondary}`,
          }}
        >
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
          />
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <span>汉唐剧院票务管理系统</span>
            {user && (
              <>
                <span>
                  {user.name}（{user.positionName}）
                </span>
                <Button
                  type="text"
                  icon={<LogoutOutlined />}
                  onClick={handleLogout}
                >
                  退出
                </Button>
              </>
            )}
          </div>
        </Header>
        <Content style={{ margin: 16 }}>
          <Outlet />
        </Content>
        <Footer style={{ textAlign: 'center', padding: '12px 50px' }}>
          TTMS &copy; 2026 汉唐传媒有限公司
        </Footer>
      </Layout>
    </Layout>
  );
}

export default AdminLayout;
