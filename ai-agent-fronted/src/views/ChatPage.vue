<script setup>
import { computed, nextTick, onBeforeUnmount, ref } from 'vue'
import { useRouter } from 'vue-router'
import { API_BASE_URL } from '../api/http'

const props = defineProps({
  title: { type: String, required: true },
  path: { type: String, required: true },
  needChatId: { type: Boolean, default: false },
  aiChunkAsStep: { type: Boolean, default: false },
  aiAvatar: { type: String, default: 'AI' },
})

const router = useRouter()
const messages = ref([])
const inputMessage = ref('')
const chatId = ref(props.needChatId ? createChatId() : '')
const loading = ref(false)
const chatBodyRef = ref(null)
let eventSource = null

const canSend = computed(() => inputMessage.value.trim() && !loading.value)

function createChatId() {
  if (window.crypto && window.crypto.randomUUID) {
    return window.crypto.randomUUID()
  }
  return `${Date.now()}-${Math.random().toString(16).slice(2)}`
}

function buildSseUrl(message) {
  const url = new URL(`${API_BASE_URL}${props.path}`)
  url.searchParams.set('message', message)
  if (props.needChatId) {
    url.searchParams.set('chatId', chatId.value)
  }
  return url.toString()
}

async function scrollToBottom() {
  await nextTick()
  if (chatBodyRef.value) {
    chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
  }
}

function closeCurrentSse() {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}

function appendAiText(delta) {
  if (!messages.value.length || messages.value[messages.value.length - 1].role !== 'ai') {
    messages.value.push({ role: 'ai', content: '' })
  }
  messages.value[messages.value.length - 1].content += delta
  scrollToBottom()
}

function appendAiStep(stepText) {
  messages.value.push({ role: 'ai', content: stepText })
  scrollToBottom()
}

function finishCurrentSse() {
  loading.value = false
  closeCurrentSse()
}

function startSse(message) {
  loading.value = true
  const sseUrl = buildSseUrl(message)
  eventSource = new EventSource(sseUrl)

  eventSource.onmessage = (event) => {
    const chunk = event.data ?? ''
    if (!chunk) return
    if (chunk === '[DONE]') {
      finishCurrentSse()
      return
    }
    if (props.aiChunkAsStep) {
      appendAiStep(chunk)
      return
    }
    appendAiText(chunk)
  }

  eventSource.onerror = () => {
    if (!messages.value.length || messages.value[messages.value.length - 1].role !== 'ai') {
      messages.value.push({ role: 'ai', content: '对话流已中断，请重试。' })
    } else if (!messages.value[messages.value.length - 1].content.trim()) {
      messages.value[messages.value.length - 1].content = '对话流已中断，请重试。'
    }
    finishCurrentSse()
  }
}

function sendMessage() {
  const content = inputMessage.value.trim()
  if (!content || loading.value) return
  inputMessage.value = ''
  closeCurrentSse()

  messages.value.push({ role: 'user', content })
  if (!props.aiChunkAsStep) {
    messages.value.push({ role: 'ai', content: '' })
  }
  scrollToBottom()
  startSse(content)
}

function backHome() {
  closeCurrentSse()
  router.push({ name: 'home' })
}

onBeforeUnmount(() => {
  closeCurrentSse()
})
</script>

<template>
  <div class="chat-page">
    <header class="chat-header">
      <div>
        <h1>{{ title }}</h1>
        <p v-if="needChatId" class="chat-id">聊天室 ID：{{ chatId }}</p>
      </div>
      <button class="ghost-btn" @click="backHome">返回首页</button>
    </header>

    <main ref="chatBodyRef" class="chat-body">
      <div
        v-for="(message, index) in messages"
        :key="`${message.role}-${index}`"
        class="message-row"
        :class="message.role === 'user' ? 'message-row-user' : 'message-row-ai'"
      >
        <template v-if="message.role === 'ai'">
          <div class="ai-avatar">{{ aiAvatar }}</div>
          <div class="message-bubble">{{ message.content || '...' }}</div>
        </template>
        <template v-else>
          <div class="message-bubble">{{ message.content || '...' }}</div>
        </template>
      </div>
    </main>

    <footer class="chat-footer">
      <input
        v-model="inputMessage"
        class="chat-input"
        placeholder="请输入消息..."
        @keydown.enter.prevent="sendMessage"
      />
      <button class="send-btn" :disabled="!canSend" @click="sendMessage">
        {{ loading ? '生成中...' : '发送' }}
      </button>
    </footer>
  </div>
</template>
