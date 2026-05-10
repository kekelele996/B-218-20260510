<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="visible" class="fixed inset-0 bg-black/50 flex items-center justify-center z-[150] p-4" @click.self="onClose">
        <div class="bg-white border-4 border-black rounded-3xl w-full max-w-md shadow-playful-lg animate-slideUp overflow-hidden">
          <!-- 头部 -->
          <div v-if="title" class="flex items-center justify-between px-6 py-4 border-b-4 border-black">
            <h3 class="text-xl font-black">{{ title }}</h3>
            <button @click="onClose" class="w-8 h-8 flex items-center justify-center rounded-xl hover:bg-gray-100 transition-colors">
              <i class="fas fa-times text-gray-400"></i>
            </button>
          </div>

          <!-- 内容 -->
          <div class="p-6">
            <slot></slot>
          </div>

          <!-- 底部按钮 -->
          <div v-if="showFooter" class="flex gap-3 px-6 pb-6">
            <button
              v-if="showCancel"
              @click="onClose"
              class="flex-1 bg-white border-3 border-black rounded-2xl py-3 font-bold shadow-playful-sm active:shadow-none active:translate-x-1 active:translate-y-1 transition-all"
            >
              {{ cancelText }}
            </button>
            <button
              @click="onConfirm"
              class="flex-1 btn-main"
              :class="confirmClass"
              :disabled="confirmDisabled"
            >
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  showFooter: {
    type: Boolean,
    default: true
  },
  showCancel: {
    type: Boolean,
    default: true
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  confirmClass: {
    type: String,
    default: ''
  },
  confirmDisabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'confirm', 'close'])

const onClose = () => {
  emit('update:visible', false)
  emit('close')
}

const onConfirm = () => {
  emit('confirm')
}
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
