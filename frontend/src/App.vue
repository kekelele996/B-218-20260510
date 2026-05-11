<template>
  <div class="min-h-screen flex flex-col">
    <Header />
    <main class="flex-1 pb-24 lg:pb-0">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <Footer />
    <Toast ref="toastRef" />
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import Toast from '@/components/Toast.vue'

const toastRef = ref(null)

// 提供全局toast方法
provide('toast', {
  success: (msg) => toastRef.value?.success(msg),
  error: (msg) => toastRef.value?.error(msg),
  warning: (msg) => toastRef.value?.warning(msg),
  info: (msg) => toastRef.value?.info(msg)
})
</script>

<style>
.page-enter-active,
.page-leave-active {
  transition: opacity 0.2s ease;
}

.page-enter-from,
.page-leave-to {
  opacity: 0;
}
</style>
