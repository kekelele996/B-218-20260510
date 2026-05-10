<template>
  <div class="min-h-screen flex flex-col lg:flex-row">
    <!-- 移动端 -->
    <div class="lg:hidden flex-1 px-6 py-8">
      <header class="flex items-center gap-4 mb-8">
        <router-link
          to="/login"
          class="w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center shadow-[2px_2px_0px_black]"
        >
          <i class="fas fa-chevron-left"></i>
        </router-link>
        <h1 class="text-2xl font-black">新用户注册</h1>
      </header>

      <RegisterForm />
    </div>

    <!-- PC端 -->
    <div class="hidden lg:flex flex-1">
      <!-- 左侧装饰区 -->
      <div class="w-1/2 bg-gradient-to-br from-pink-100 to-yellow-100 flex items-center justify-center p-12">
        <div class="text-center">
          <div class="w-32 h-32 bg-pink-400 border-4 border-black rounded-[3rem] mx-auto flex items-center justify-center text-6xl shadow-playful-lg mb-8">
            🐱
          </div>
          <h2 class="text-4xl font-black italic mb-4" style="font-family: 'ZCOOL KuaiLe', cursive;">
            加入宠遇
          </h2>
          <p class="text-lg font-bold opacity-60">成为领养大家庭的一员</p>
          <p class="text-lg font-bold opacity-60">给毛孩子一个温暖的家</p>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="w-1/2 flex items-center justify-center p-12">
        <div class="w-full max-w-md">
          <h1 class="text-3xl font-black mb-2">新用户注册</h1>
          <p class="font-bold opacity-60 mb-8">创建您的账号</p>

          <RegisterForm />

          <p class="text-center font-bold text-sm mt-6">
            已有账号？<router-link to="/login" class="text-pink-500 underline">立即登录</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import RegisterForm from '@/components/RegisterForm.vue'
</script>
