import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../layout/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/Dashboard.vue')
        },
        {
          path: 'products',
          name: 'products',
          component: () => import('../views/Product.vue')
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/Order.vue')
        },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('../views/Category.vue')
        },
        {
          path: 'customers',
          name: 'customers',
          component: () => import('../views/Customer.vue')
        }
      ]
    }
  ]
})

// Simple Route Guard
router.beforeEach((to, from, next) => {
  const user = sessionStorage.getItem('user')
  if (to.name !== 'login' && !user) {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router
