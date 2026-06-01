import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import StudioView from '../views/admin/StudioView.vue'
import PlayScheduleView from '../views/admin/PlayScheduleView.vue'
import SaleView from '../views/sales/SaleView.vue'
import CheckView from '../views/check/CheckView.vue'
import FinanceView from '../views/finance/FinanceView.vue'
import CustomerPortalView from '../views/customer/CustomerPortalView.vue'

const routes = [
  { path: '/', component: DashboardView, meta: { title: '运营总览', caption: '关键业务流程、待办和今日售票概览' } },
  { path: '/admin/studios', component: StudioView, meta: { title: '演出厅与座位管理', caption: '维护演出厅、行列布局和座位状态' } },
  { path: '/admin/plays', component: PlayScheduleView, meta: { title: '剧目与演出计划', caption: '剧目录入、排期、冲突检测和演出票生成' } },
  { path: '/sales', component: SaleView, meta: { title: '售票与退票', caption: '线下售票、选座锁票、收款出票和退票处理' } },
  { path: '/check', component: CheckView, meta: { title: '入场验票', caption: '按票 ID 校验当前场次入场资格' } },
  { path: '/finance', component: FinanceView, meta: { title: '财务统计', caption: '个人日销售额、票款核对、票房与上座率' } },
  { path: '/customer', component: CustomerPortalView, meta: { title: '观众端', caption: '放映安排、选座订票、订单结果与榜单' } }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
