import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/flights',
    name: 'Flights',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '航班信息' }
  },
  {
    path: '/airport-location',
    name: 'AirportLocation',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '机场位置' }
  },
  {
    path: '/airport-overview',
    name: 'AirportOverview',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '机场概况' }
  },
  {
    path: '/airport-facilities',
    name: 'AirportFacilities',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '机场设施' }
  },
  {
    path: '/airport-transport',
    name: 'AirportTransport',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '交通指南' }
  },
  {
    path: '/customs',
    name: 'Customs',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '海关须知' }
  },
  {
    path: '/luggage',
    name: 'Luggage',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '行李规定' }
  },
  {
    path: '/immigration',
    name: 'Immigration',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '出入境流程' }
  },
  {
    path: '/tickets',
    name: 'Tickets',
    component: () => import('@/views/Placeholder.vue'),
    meta: { title: '超值购票' }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
