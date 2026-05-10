<template>
  <div class="flex flex-col items-center justify-center py-16 px-4">
    <div class="w-24 h-24 bg-gray-100 border-4 border-black rounded-3xl flex items-center justify-center text-5xl mb-6 shadow-playful">
      {{ icon }}
    </div>
    <h3 class="text-xl font-black mb-2">{{ title }}</h3>
    <p class="text-sm font-bold opacity-60 text-center max-w-xs">{{ description }}</p>
    <slot name="action"></slot>
  </div>
</template>

<script setup>
defineProps({
  icon: {
    type: String,
    default: '🔍'
  },
  title: {
    type: String,
    default: '暂无数据'
  },
  description: {
    type: String,
    default: '这里空空如也~'
  }
})
</script>
