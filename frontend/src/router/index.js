import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/pets',
    name: 'PetList',
    component: () => import('@/views/PetList.vue'),
    meta: { title: '宠物列表' }
  },
  {
    path: '/pet/:id',
    name: 'PetDetail',
    component: () => import('@/views/PetDetail.vue'),
    meta: { title: '宠物详情' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/apply/:petId',
    name: 'Apply',
    component: () => import('@/views/Apply.vue'),
    meta: { title: '申请领养', requiresAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/user/UserCenter.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/user/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: '个人资料', requiresAuth: true }
  },
  {
    path: '/user/password',
    name: 'Password',
    component: () => import('@/views/user/Password.vue'),
    meta: { title: '修改密码', requiresAuth: true }
  },
  {
    path: '/user/applications',
    name: 'Applications',
    component: () => import('@/views/user/Applications.vue'),
    meta: { title: '申请记录', requiresAuth: true }
  },
  {
    path: '/user/application/:id',
    name: 'ApplicationDetail',
    component: () => import('@/views/user/ApplicationDetail.vue'),
    meta: { title: '申请详情', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || '宠遇'} - 宠物领养平台`

  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn

  // 需要登录的页面
  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  // 已登录用户访问登录/注册页面
  if (to.meta.guest && isLoggedIn) {
    next({ name: 'Home' })
    return
  }

  next()
})

export default router
