import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  // ==================== 用户端 /user/* ====================
  { path: '/', redirect: '/user/' },
  {
    path: '/user/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/user/flights',
    name: 'Flights',
    component: () => import('@/views/Flights.vue'),
    meta: { title: '航班信息' }
  },
  {
    path: '/user/airport-location',
    name: 'AirportLocation',
    component: () => import('@/views/AirportLocation.vue'),
    meta: { title: '机场位置' }
  },
  {
    path: '/user/airport-overview',
    name: 'AirportOverview',
    component: () => import('@/views/AirportOverview.vue'),
    meta: { title: '机场概况' }
  },
  {
    path: '/user/airport-routes',
    name: 'AirportRoutes',
    component: () => import('@/views/AirportRoutes.vue'),
    meta: { title: '机场通航' }
  },
  {
    path: '/user/customs',
    name: 'Customs',
    component: () => import('@/views/Customs.vue'),
    meta: { title: '海关须知' }
  },
  {
    path: '/user/luggage',
    name: 'Luggage',
    component: () => import('@/views/Baggage.vue'),
    meta: { title: '行李须知' }
  },
  {
    path: '/user/immigration',
    name: 'Immigration',
    component: () => import('@/views/Immigration.vue'),
    meta: { title: '出入境流程' }
  },
  {
    path: '/user/tickets',
    name: 'Tickets',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '超值购票' }
  },

  // ==================== 管理端 /admin/* ====================
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/AdminLogin.vue'),
    meta: { title: '管理员登录', noAuth: true }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/views/AdminDashboard.vue'),
    meta: { title: '管理控制台', requiresAdmin: true }
  },

  // ==================== 兜底 ====================
  { path: '/:pathMatch(.*)*', redirect: '/user/' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
