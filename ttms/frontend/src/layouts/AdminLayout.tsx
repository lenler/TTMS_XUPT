// 管理端 SaaS 布局（侧边菜单 + 顶栏用户信息 + 内容区 + 退出）

import { useState, useEffect, useMemo } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button, theme, Spin } from 'antd';
import type { MenuProps } from 'antd';
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

/** 菜单名称 → 图标映射（从接口返回的菜单没有 icon，前端补上） */
const iconMap: Record<string, React.ReactNode> = {
  '工作台': <HomeOutlined />,
  '剧院管理': <BankOutlined />,
  '演出厅管理': <TableOutlined />,
  '剧目管理': <VideoCameraOutlined />,
  '演出计划': <CalendarOutlined />,
  '验票管理': <CheckCircleOutlined />,
  '票务管理': <DollarOutlined />,
  '售票记录': <DollarOutlined />,
  '退票处理': <DollarOutlined />,
  '用户管理': <TeamOutlined />,
  '员工管理': <UserOutlined />,
  '观众管理': <TeamOutlined />,
  '权限管理': <SafetyCertificateOutlined />,
  '角色管理': <SafetyCertificateOutlined />,
  '财务管理': <BarChartOutlined />,
  '财务统计': <BarChartOutlined />,
};

/** 将 authStore 的 MenuItem 转为 antd Menu 的 items 格式 */
function toAntdMenuItems(
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  menus: any[]
): MenuProps['items'] {
  return menus.map((item) => {
    if (item.children && item.children.length > 0) {
      return {
        key: item.name,
        icon: iconMap[item.name] || null,
        label: item.name,
        children: item.children.map((child: { name: string; url?: string }) => ({
          key: child.url || child.name,
          icon: iconMap[child.name] || null,
          label: child.name,
        })),
      };
    }
    return {
      key: item.url || item.name,
      icon: iconMap[item.name] || null,
      label: item.name,
    };
  });
}

function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, menus, logout, fetchMenus } = useAuthStore();
  const { token: themeToken } = theme.useToken();

  /** 首次加载时获取菜单 */
  useEffect(() => {
    if (!menus) {
      fetchMenus();
    }
  }, [menus, fetchMenus]);

  /** 从接口菜单生成 antd menu items */
  const antdMenuItems = useMemo(() => {
    if (!menus || menus.length === 0) return [];
    return toAntdMenuItems(menus) ?? [];
  }, [menus]);

  /** 根据当前路径计算选中的菜单项 */
  const selectedKey = location.pathname === '/admin'
    ? '/admin/dashboard'
    : location.pathname;

  /** 默认展开所有有子菜单的一级菜单 */
  const openKeys = useMemo(() => {
    if (!menus) return [];
    return menus
      .filter((item) => item.children && item.children.length > 0)
      .map((item) => item.name);
  }, [menus]);

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
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
        {antdMenuItems.length > 0 ? (
          <Menu
            theme="dark"
            mode="inline"
            selectedKeys={[selectedKey]}
            defaultOpenKeys={openKeys}
            items={antdMenuItems}
            onClick={handleMenuClick}
          />
        ) : (
          <div style={{ textAlign: 'center', padding: 24 }}>
            <Spin size="small" />
          </div>
        )}
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
            <span>奥斯卡剧院票务管理系统</span>
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
          TTMS &copy; 2026 小麦
        </Footer>
      </Layout>
    </Layout>
  );
}

export default AdminLayout;
