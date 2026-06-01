// 认证状态管理：token、用户信息、菜单

import { create } from 'zustand';
import { getCurrentUserMenus } from '@/services/admin/auth';

interface User {
  id: number;
  name: string;
  positionName: string;
  roles: string[];
}

interface MenuItem {
  name: string;
  icon?: string;
  url?: string;
  children?: MenuItem[];
}

interface AuthState {
  token: string | null;
  user: User | null;
  menus: MenuItem[] | null;
  login: (token: string, user: User) => void;
  logout: () => void;
  fetchMenus: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('token'),
  user: JSON.parse(localStorage.getItem('user') || 'null'),
  menus: null,

  login: (token, user) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    set({ token, user });
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    set({ token: null, user: null, menus: null });
  },

  fetchMenus: async () => {
    try {
      const res = await getCurrentUserMenus();
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      set({ menus: (res.data as any).menus || [] });
    } catch {
      set({ menus: [] });
    }
  },
}));
