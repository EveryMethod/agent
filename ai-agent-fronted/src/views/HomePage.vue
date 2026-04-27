<script setup>
import { useRouter } from 'vue-router'
import { useSeo } from '../composables/useSeo'

const router = useRouter()

const apps = [
  {
    title: 'AI 恋爱大师',
    routeName: 'love-master',
    desc: '与 AI 恋爱大师进行甜蜜对话，获取恋爱建议',
    avatar: '❤️',
    tag: '情感咨询',
    features: ['智能情感分析', '个性化恋爱建议', '实时对话响应'],
  },
  {
    title: 'AI 超级智能体',
    routeName: 'super-agent',
    desc: '与超级智能 AI 进行对话，解决各种问题',
    avatar: '🤖',
    tag: '智能体',
    features: ['多步骤推理能力', '工具调用与分析', '高级问题解决'],
  },
]

function goToApp(routeName) {
  router.push({ name: routeName })
}

useSeo({
  title: 'AI 应用中心 | 极客智能助手',
  description: 'AI 恋爱大师与 AI 超级智能体，一站式实时对话平台，支持 PC、平板和手机。',
  keywords: 'AI 聊天,恋爱大师,超级智能体,SSE,Vue3',
})
</script>

<template>
  <div class="home-page">
    <div class="stars-layer" aria-hidden="true"></div>
    <section class="hero-panel">
      <p class="hero-tag">NEXT-GEN AI HUB</p>
      <h1>AI 智能助手应用平台</h1>
      <p class="home-subtitle">探索 AI 的无限可能，体验智能对话的魅力</p>
    </section>

    <section class="terminal-panel">
      <div class="terminal-top">
        <span class="dot dot-red"></span>
        <span class="dot dot-yellow"></span>
        <span class="dot dot-green"></span>
        <span class="terminal-title">ai-terminal</span>
      </div>
      <div class="terminal-content">
        <p><span class="line-blue">root@yuai:~$</span> ./launch-ai-platform.sh</p>
        <p>正在初始化 AI 引擎...</p>
        <p>加载智能助手模型...</p>
        <p>连接服务器...</p>
        <p class="line-green">AI 平台启动成功！选择下方应用开始体验</p>
      </div>
    </section>

    <div class="app-list">
      <article
        v-for="app in apps"
        :key="app.routeName"
        class="app-card"
        role="button"
        tabindex="0"
        @click="goToApp(app.routeName)"
        @keydown.enter="goToApp(app.routeName)"
        @keydown.space.prevent="goToApp(app.routeName)"
      >
        <div class="app-card-top">
          <span class="app-avatar">{{ app.avatar }}</span>
          <span class="app-tag">{{ app.tag }}</span>
        </div>
        <h2>{{ app.title }}</h2>
        <p>{{ app.desc }}</p>
        <ul class="app-feature-list">
          <li v-for="feature in app.features" :key="feature">{{ feature }}</li>
        </ul>
        <span class="app-enter">开始聊天</span>
      </article>
    </div>
  </div>
</template>
