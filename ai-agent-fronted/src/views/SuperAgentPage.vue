<script setup>
import ChatPage from './ChatPage.vue'
import { useSeo } from '../composables/useSeo'

useSeo({
  title: 'AI 超级智能体 | 极客智能助手',
  description: 'AI 超级智能体支持多步骤 SSE 实时输出，每个步骤独立展示，推理过程更清晰。',
  keywords: 'AI 超级智能体,流式响应,多步骤推理',
})
</script>

<template>
  <ChatPage
    title="AI 超级智能体"
    path="/ai/loveApp/manus/chat"
    :ai-chunk-as-step="true"
    ai-avatar="🤖"
  />
</template>
