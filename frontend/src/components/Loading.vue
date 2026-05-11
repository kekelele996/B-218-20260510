<template>
  <div v-if="visible" class="fixed inset-0 bg-black/30 flex items-center justify-center z-[100]">
    <div class="flex flex-col items-center gap-4">
      <div class="w-16 h-16 bg-white border-4 border-black rounded-2xl flex items-center justify-center shadow-playful animate-pulse">
        <i class="fas fa-spinner text-3xl text-pink-500 animate-spin"></i>
      </div>
      <p class="text-white font-bold text-lg">{{ text }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  text: {
    type: String,
    default: '加载中...'
  }
})
</script>
