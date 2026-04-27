import { onMounted } from 'vue'

function upsertMeta(name, content) {
  let meta = document.querySelector(`meta[name="${name}"]`)
  if (!meta) {
    meta = document.createElement('meta')
    meta.setAttribute('name', name)
    document.head.appendChild(meta)
  }
  meta.setAttribute('content', content)
}

export function useSeo({ title, description, keywords }) {
  onMounted(() => {
    if (title) document.title = title
    if (description) upsertMeta('description', description)
    if (keywords) upsertMeta('keywords', keywords)
  })
}
