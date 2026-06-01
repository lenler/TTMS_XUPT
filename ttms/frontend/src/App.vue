<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">TT</span>
        <div>
          <strong>TTMS</strong>
          <small>汉唐剧院票务管理系统</small>
        </div>
      </div>
      <nav>
        <RouterLink v-for="item in navItems" :key="item.to" :to="item.to">
          <component :is="item.icon" :size="18" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>
    </aside>
    <main>
      <header class="topbar">
        <div>
          <h1>{{ route.meta.title || '工作台' }}</h1>
          <p>{{ route.meta.caption || '剧院运营、票务与财务的一体化管理界面' }}</p>
        </div>
        <div class="user-chip">系统管理员</div>
      </header>
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { BarChart3, Building2, CalendarDays, ClipboardCheck, LayoutDashboard, Ticket } from 'lucide-vue-next'

const route = useRoute()
const navItems = [
  { to: '/', label: '运营总览', icon: LayoutDashboard },
  { to: '/admin/studios', label: '剧院管理', icon: Building2 },
  { to: '/admin/plays', label: '剧目计划', icon: CalendarDays },
  { to: '/sales', label: '售票退票', icon: Ticket },
  { to: '/check', label: '入场验票', icon: ClipboardCheck },
  { to: '/finance', label: '财务统计', icon: BarChart3 }
]
</script>
