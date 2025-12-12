import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'  // 使用相对路径

const routes = [
    {
        path: '/',
        name: 'home',
        component: HomeView
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
