<template>
  <!-- PC端顶部导航 -->
  <header class="hidden lg:block bg-white border-b-4 border-black sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
      <!-- Logo -->
      <router-link to="/" class="flex items-center gap-3">
        <div class="w-12 h-12 bg-yellow-300 border-3 border-black rounded-2xl flex items-center justify-center text-2xl shadow-playful-sm">
          🐾
        </div>
        <span class="text-2xl font-black">宠遇</span>
      </router-link>

      <!-- 导航链接 -->
      <nav class="flex items-center gap-8">
        <router-link
          to="/"
          class="font-bold text-lg hover:text-pink-500 transition-colors"
          :class="{ 'text-pink-500': $route.path === '/' }"
        >
          首页
        </router-link>
        <router-link
          to="/pets"
          class="font-bold text-lg hover:text-pink-500 transition-colors"
          :class="{ 'text-pink-500': $route.path.startsWith('/pet') }"
        >
          领养列表
        </router-link>
      </nav>

      <!-- 用户区域 -->
      <div class="flex items-center gap-4">
        <template v-if="isLoggedIn">
          <router-link
            to="/user"
            class="flex items-center gap-3 bg-white border-3 border-black rounded-2xl px-4 py-2 shadow-playful-sm hover:shadow-playful transition-all"
          >
            <div class="w-8 h-8 bg-pink-100 rounded-full flex items-center justify-center border-2 border-black overflow-hidden">
              <img v-if="userInfo?.avatar" :src="userInfo.avatar" class="w-full h-full object-cover" />
              <i v-else class="fas fa-user text-pink-500 text-sm"></i>
            </div>
            <span class="font-bold">{{ userInfo?.realName || '用户' }}</span>
          </router-link>
        </template>
        <template v-else>
          <router-link
            to="/login"
            class="font-bold text-lg hover:text-pink-500 transition-colors"
          >
            登录
          </router-link>
          <router-link
            to="/register"
            class="bg-pink-400 text-white border-3 border-black rounded-2xl px-6 py-2 font-bold shadow-playful-sm hover:shadow-playful transition-all"
          >
            注册
          </router-link>
        </template>
      </div>
    </div>
  </header>

  <!-- 移动端底部导航 -->
  <nav class="lg:hidden fixed bottom-6 left-1/2 -translate-x-1/2 w-[85%] max-w-sm bg-white border-4 border-black rounded-[2rem] h-16 flex justify-around items-center px-6 shadow-[0_10px_30px_rgba(0,0,0,0.2)] z-50">
    <router-link to="/" class="flex flex-col items-center p-2">
      <i class="fas fa-home text-2xl" :class="$route.path === '/' ? 'text-pink-500' : 'text-slate-400'"></i>
    </router-link>
    <router-link to="/pets" class="flex flex-col items-center p-2">
      <i class="fas fa-paw text-2xl" :class="$route.path.startsWith('/pet') ? 'text-pink-500' : 'text-slate-400'"></i>
    </router-link>
    <router-link to="/user/applications" class="flex flex-col items-center p-2">
      <i class="fas fa-heart text-2xl" :class="$route.path === '/user/applications' ? 'text-pink-500' : 'text-slate-400'"></i>
    </router-link>
    <router-link to="/user" class="flex flex-col items-center p-2">
      <i class="fas fa-user text-2xl" :class="$route.path.startsWith('/user') && $route.path !== '/user/applications' ? 'text-pink-500' : 'text-slate-400'"></i>
    </router-link>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
</script>
