// 观众端布局 —— 水墨留白 · 东方极简
// 页头导航（宣纸白 + 细线分隔）+ 内容区 + 页脚

import { Link, Outlet, useLocation } from 'react-router-dom';
import { UserOutlined } from '@ant-design/icons';

/** 导航项定义 */
const navItems = [
  { key: '/', label: '首页' },
  { key: '/schedule', label: '放映安排' },
  { key: '/board', label: '榜单' },
  { key: '/contact', label: '联系我们' },
];

/** 获取当前路由用于高亮导航 */
function useCurrentRoute() {
  const { pathname } = useLocation();
  if (pathname === '/') return '/';
  const seg = pathname.split('/')[1];
  return '/' + seg;
}

/** 观众端布局组件 */
function CustomerLayout() {
  const current = useCurrentRoute();
  const customerToken = localStorage.getItem('customerToken');

  return (
    <div className="min-h-screen flex flex-col bg-rice">
      {/* ===== Header ===== */}
      <header className="sticky top-0 z-50 bg-rice/95 backdrop-blur-sm border-b border-warm">
        <div className="max-w-6xl mx-auto flex items-center justify-between px-6 h-16">
          {/* 左侧：品牌名 + 导航 */}
          <div className="flex items-center gap-10">
            {/* 品牌名 — 衬线字体 + 宽字间距 */}
            <Link
              to="/"
              className="font-serif text-xl tracking-widest text-ink hover:text-gold transition-soft whitespace-nowrap"
            >
              汉唐剧院
            </Link>

            {/* 导航链接 — 当前页暗金下划线 */}
            <nav className="flex items-center gap-1">
              {navItems.map((item) => {
                const active = current === item.key;
                return (
                  <Link
                    key={item.key}
                    to={item.key}
                    className={`
                      relative px-4 py-2 text-sm transition-soft
                      ${active ? 'text-ink font-medium' : 'text-stone hover:text-ink'}
                    `}
                  >
                    {item.label}
                    {/* 当前页底部 2px 暗金下划线 */}
                    {active && (
                      <span className="absolute bottom-0 left-1/2 -translate-x-1/2 w-5 h-[2px] bg-gold rounded-full" />
                    )}
                  </Link>
                );
              })}
            </nav>
          </div>

          {/* 右侧：用户操作 */}
          <div className="flex items-center gap-4 text-sm">
            {customerToken ? (
              <>
                <Link
                  to="/profile"
                  className="text-stone hover:text-ink transition-soft flex items-center gap-1"
                >
                  <UserOutlined /> 我的
                </Link>
                <Link
                  to="/login"
                  onClick={() => localStorage.removeItem('customerToken')}
                  className="border border-warm text-stone hover:text-ink hover:border-ink px-4 py-1.5 rounded-sm transition-soft"
                >
                  退出
                </Link>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className="border border-ink text-ink hover:bg-ink hover:text-white px-4 py-1.5 rounded-sm transition-soft"
                >
                  登录
                </Link>
                <Link
                  to="/register"
                  className="bg-ink text-white hover:bg-gold px-4 py-1.5 rounded-sm transition-soft"
                >
                  注册
                </Link>
              </>
            )}
          </div>
        </div>
      </header>

      {/* ===== Content ===== */}
      <main className="flex-1 max-w-6xl mx-auto w-full px-6 py-10">
        <Outlet />
      </main>

      {/* ===== Footer ===== */}
      <footer className="border-t border-warm py-8 text-center">
        <p className="text-light-ink text-sm">
          TTMS &copy; 2026 汉唐传媒有限公司 · 西安邮电大学软件工程系
        </p>
      </footer>
    </div>
  );
}

export default CustomerLayout;
