<script setup>
import ChatPage from './ChatPage.vue'
import { useSeo } from '../composables/useSeo'

useSeo({
  title: 'AI 恋爱大师 | 极客智能助手',
  description: 'AI 恋爱大师，支持聊天室会话 ID 与 SSE 实时流式回复，助你高效沟通。',
  keywords: 'AI 恋爱大师,情感建议,SSE 聊天',
})
</script>

<template>
  <ChatPage title="AI 恋爱大师" path="/ai/loveApp/chat/sse" :need-chat-id="true" ai-avatar="❤️" />
</template>
