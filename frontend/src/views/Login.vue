<template>
  <div class="min-h-screen flex flex-col lg:flex-row">
    <!-- 移动端 -->
    <div class="lg:hidden flex-1 px-6 py-8">
      <div class="mt-10 mb-12 text-center">
        <div class="w-20 h-20 bg-yellow-300 border-4 border-black rounded-[2rem] mx-auto flex items-center justify-center text-4xl shadow-[6px_6px_0px_black] mb-6">
          🐾
        </div>
        <h1 class="text-3xl font-black italic tracking-tighter">欢迎回来！</h1>
        <p class="font-bold opacity-60 mt-2">宠遇 - 让每一份爱都有归宿</p>
      </div>

      <LoginForm />

      <p class="text-center font-bold text-sm mt-8">
        还没有账号？<router-link to="/register" class="text-pink-500 underline">立即注册</router-link>
      </p>
    </div>

    <!-- PC端 -->
    <div class="hidden lg:flex flex-1">
      <!-- 左侧装饰区 -->
      <div class="w-1/2 bg-gradient-to-br from-pink-100 to-yellow-100 flex items-center justify-center p-12">
        <div class="text-center">
          <div class="w-32 h-32 bg-yellow-300 border-4 border-black rounded-[3rem] mx-auto flex items-center justify-center text-6xl shadow-playful-lg mb-8">
            🐾
          </div>
          <h2 class="text-4xl font-black italic mb-4" style="font-family: 'ZCOOL KuaiLe', cursive;">
            宠遇
          </h2>
          <p class="text-lg font-bold opacity-60">让每一只流浪的小生命都有个家</p>
          <p class="text-lg font-bold opacity-60">让每一份爱都有归宿</p>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="w-1/2 flex items-center justify-center p-12">
        <div class="w-full max-w-md">
          <h1 class="text-3xl font-black mb-2">欢迎回来！</h1>
          <p class="font-bold opacity-60 mb-8">请登录您的账号</p>

          <LoginForm />

          <p class="text-center font-bold text-sm mt-8">
            还没有账号？<router-link to="/register" class="text-pink-500 underline">立即注册</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import LoginForm from '@/components/LoginForm.vue'
</script>
