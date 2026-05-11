<template>
  <Teleport to="body">
    <div class="fixed top-4 right-4 z-[200] flex flex-col gap-2">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="flex items-center gap-3 bg-white border-3 border-black rounded-2xl px-4 py-3 shadow-playful-sm min-w-[280px] max-w-[360px]"
          :class="toastClasses(toast.type)"
        >
          <div class="w-8 h-8 rounded-xl flex items-center justify-center border-2 border-black" :class="iconBg(toast.type)">
            <i :class="iconClass(toast.type)"></i>
          </div>
          <p class="flex-1 font-bold text-sm">{{ toast.message }}</p>
          <button @click="removeToast(toast.id)" class="text-gray-400 hover:text-gray-600">
            <i class="fas fa-times"></i>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'

const toasts = ref([])
let toastId = 0

const addToast = (message, type = 'info', duration = 3000) => {
  const id = ++toastId
  toasts.value.push({ id, message, type })

  if (duration > 0) {
    setTimeout(() => {
      removeToast(id)
    }, duration)
  }
}

const removeToast = (id) => {
  const index = toasts.value.findIndex(t => t.id === id)
  if (index > -1) {
    toasts.value.splice(index, 1)
  }
}

const toastClasses = (type) => {
  const classes = {
    success: 'border-green-500',
    error: 'border-red-500',
    warning: 'border-yellow-500',
    info: 'border-blue-500'
  }
  return classes[type] || classes.info
}

const iconBg = (type) => {
  const bgs = {
    success: 'bg-green-100',
    error: 'bg-red-100',
    warning: 'bg-yellow-100',
    info: 'bg-blue-100'
  }
  return bgs[type] || bgs.info
}

const iconClass = (type) => {
  const icons = {
    success: 'fas fa-check text-green-500',
    error: 'fas fa-times text-red-500',
    warning: 'fas fa-exclamation text-yellow-500',
    info: 'fas fa-info text-blue-500'
  }
  return icons[type] || icons.info
}

defineExpose({
  success: (msg) => addToast(msg, 'success'),
  error: (msg) => addToast(msg, 'error'),
  warning: (msg) => addToast(msg, 'warning'),
  info: (msg) => addToast(msg, 'info')
})
</script>

<style scoped>
.toast-enter-active {
  animation: toastIn 0.3s ease-out;
}

.toast-leave-active {
  animation: toastOut 0.3s ease-in;
}

@keyframes toastIn {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes toastOut {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(100%);
  }
}
</style>
