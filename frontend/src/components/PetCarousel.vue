<template>
  <div class="relative">
    <!-- 主图 -->
    <div class="relative overflow-hidden" :class="containerClass">
      <img
        :src="currentImage"
        :alt="alt"
        class="w-full h-full object-cover transition-transform duration-300"
        @click="openPreview"
      />

      <!-- 导航按钮 -->
      <template v-if="images.length > 1">
        <button
          @click.stop="prev"
          class="absolute left-4 top-1/2 -translate-y-1/2 w-10 h-10 bg-white/90 border-2 border-black rounded-xl flex items-center justify-center shadow-playful-sm hover:bg-white transition-colors"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        <button
          @click.stop="next"
          class="absolute right-4 top-1/2 -translate-y-1/2 w-10 h-10 bg-white/90 border-2 border-black rounded-xl flex items-center justify-center shadow-playful-sm hover:bg-white transition-colors"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
      </template>

      <!-- 指示器 -->
      <div v-if="images.length > 1" class="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2">
        <div
          v-for="(_, index) in images"
          :key="index"
          class="w-2 h-2 rounded-full border border-black transition-all"
          :class="index === currentIndex ? 'bg-white w-4' : 'bg-white/50'"
        ></div>
      </div>
    </div>

    <!-- 缩略图 -->
    <div v-if="showThumbnails && images.length > 1" class="flex gap-2 mt-4 overflow-x-auto no-scrollbar">
      <div
        v-for="(image, index) in images"
        :key="index"
        @click="goTo(index)"
        class="flex-shrink-0 w-16 h-16 rounded-xl border-3 overflow-hidden cursor-pointer transition-all"
        :class="index === currentIndex ? 'border-pink-500 scale-110' : 'border-black'"
      >
        <img :src="image" class="w-full h-full object-cover" />
      </div>
    </div>

    <!-- 图片预览模态框 -->
    <Teleport to="body">
      <Transition name="fade">
        <div
          v-if="showPreview"
          class="fixed inset-0 bg-black/90 z-[200] flex items-center justify-center"
          @click="closePreview"
        >
          <button
            @click="closePreview"
            class="absolute top-6 right-6 w-12 h-12 bg-white border-3 border-black rounded-2xl flex items-center justify-center shadow-playful"
          >
            <i class="fas fa-times text-xl"></i>
          </button>

          <img
            :src="currentImage"
            class="max-w-[90vw] max-h-[80vh] object-contain rounded-2xl border-4 border-white"
            @click.stop
          />

          <template v-if="images.length > 1">
            <button
              @click.stop="prev"
              class="absolute left-6 top-1/2 -translate-y-1/2 w-12 h-12 bg-white border-3 border-black rounded-2xl flex items-center justify-center shadow-playful"
            >
              <i class="fas fa-chevron-left text-xl"></i>
            </button>
            <button
              @click.stop="next"
              class="absolute right-6 top-1/2 -translate-y-1/2 w-12 h-12 bg-white border-3 border-black rounded-2xl flex items-center justify-center shadow-playful"
            >
              <i class="fas fa-chevron-right text-xl"></i>
            </button>
          </template>

          <div class="absolute bottom-8 left-1/2 -translate-x-1/2 flex gap-2">
            <div
              v-for="(_, index) in images"
              :key="index"
              @click.stop="goTo(index)"
              class="w-3 h-3 rounded-full border-2 border-white cursor-pointer transition-all"
              :class="index === currentIndex ? 'bg-white' : 'bg-white/30'"
            ></div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  images: {
    type: Array,
    default: () => []
  },
  alt: {
    type: String,
    default: ''
  },
  showThumbnails: {
    type: Boolean,
    default: false
  },
  containerClass: {
    type: String,
    default: 'h-[300px] md:h-[400px] lg:h-[500px]'
  }
})

const currentIndex = ref(0)
const showPreview = ref(false)

const currentImage = computed(() => {
  return props.images[currentIndex.value] || '/placeholder-pet.jpg'
})

const prev = () => {
  currentIndex.value = (currentIndex.value - 1 + props.images.length) % props.images.length
}

const next = () => {
  currentIndex.value = (currentIndex.value + 1) % props.images.length
}

const goTo = (index) => {
  currentIndex.value = index
}

const openPreview = () => {
  showPreview.value = true
  document.body.style.overflow = 'hidden'
}

const closePreview = () => {
  showPreview.value = false
  document.body.style.overflow = ''
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
