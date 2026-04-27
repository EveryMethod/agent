import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import LoveMasterPage from '../views/LoveMasterPage.vue'
import SuperAgentPage from '../views/SuperAgentPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage },
    { path: '/love-master', name: 'love-master', component: LoveMasterPage },
    { path: '/super-agent', name: 'super-agent', component: SuperAgentPage },
  ],
})

export default router
