<template>
  <div class="flex items-center justify-center gap-2 flex-wrap">
    <!-- 上一页 -->
    <button
      @click="goToPage(currentPage - 1)"
      :disabled="currentPage === 1"
      class="w-10 h-10 bg-white border-3 border-black rounded-xl flex items-center justify-center font-bold shadow-playful-sm disabled:opacity-50 disabled:cursor-not-allowed hover:shadow-playful transition-all"
    >
      <i class="fas fa-chevron-left"></i>
    </button>

    <!-- 页码 -->
    <template v-for="page in displayedPages" :key="page">
      <span v-if="page === '...'" class="w-10 h-10 flex items-center justify-center font-bold">...</span>
      <button
        v-else
        @click="goToPage(page)"
        class="w-10 h-10 border-3 border-black rounded-xl flex items-center justify-center font-bold shadow-playful-sm hover:shadow-playful transition-all"
        :class="page === currentPage ? 'bg-pink-400 text-white' : 'bg-white'"
      >
        {{ page }}
      </button>
    </template>

    <!-- 下一页 -->
    <button
      @click="goToPage(currentPage + 1)"
      :disabled="currentPage === totalPages"
      class="w-10 h-10 bg-white border-3 border-black rounded-xl flex items-center justify-center font-bold shadow-playful-sm disabled:opacity-50 disabled:cursor-not-allowed hover:shadow-playful transition-all"
    >
      <i class="fas fa-chevron-right"></i>
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    default: 1
  },
  totalPages: {
    type: Number,
    default: 1
  },
  maxVisible: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['update:currentPage', 'change'])

const displayedPages = computed(() => {
  const pages = []
  const total = props.totalPages
  const current = props.currentPage
  const max = props.maxVisible

  if (total <= max) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 3) {
      for (let i = 1; i <= 4; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 2) {
      pages.push(1)
      pages.push('...')
      for (let i = total - 3; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      pages.push(current - 1)
      pages.push(current)
      pages.push(current + 1)
      pages.push('...')
      pages.push(total)
    }
  }

  return pages
})

const goToPage = (page) => {
  if (page < 1 || page > props.totalPages || page === props.currentPage) return
  emit('update:currentPage', page)
  emit('change', page)
}
</script>
