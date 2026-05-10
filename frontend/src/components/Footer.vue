<template>
  <footer class="hidden lg:block bg-white border-t-4 border-black py-8 mt-auto">
    <div class="max-w-7xl mx-auto px-6">
      <div class="flex flex-wrap justify-between items-start gap-8">
        <!-- Logo和简介 -->
        <div class="flex-1 min-w-[200px]">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-10 h-10 bg-yellow-300 border-3 border-black rounded-xl flex items-center justify-center text-xl shadow-playful-sm">
              🐾
            </div>
            <span class="text-xl font-black">宠遇</span>
          </div>
          <p class="text-sm font-bold opacity-60 max-w-xs">
            让每一只流浪的小生命都有个家，让每一份爱都有归宿。
          </p>
        </div>

        <!-- 快速链接 -->
        <div class="flex-1 min-w-[150px]">
          <h4 class="font-black mb-4">快速链接</h4>
          <ul class="space-y-2">
            <li><router-link to="/" class="text-sm font-bold opacity-60 hover:opacity-100">首页</router-link></li>
            <li><router-link to="/pets" class="text-sm font-bold opacity-60 hover:opacity-100">领养列表</router-link></li>
            <li><router-link to="/user" class="text-sm font-bold opacity-60 hover:opacity-100">个人中心</router-link></li>
          </ul>
        </div>

        <!-- 联系我们 -->
        <div class="flex-1 min-w-[150px]">
          <h4 class="font-black mb-4">联系我们</h4>
          <ul class="space-y-2 text-sm font-bold opacity-60">
            <li><i class="fas fa-phone mr-2"></i>400-123-4567</li>
            <li><i class="fas fa-envelope mr-2"></i>contact@petadoption.com</li>
            <li><i class="fas fa-map-marker-alt mr-2"></i>上海市浦东新区</li>
          </ul>
        </div>

        <!-- 社交媒体 -->
        <div class="flex-1 min-w-[150px]">
          <h4 class="font-black mb-4">关注我们</h4>
          <div class="flex gap-3">
            <div class="w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center hover:bg-gray-50 cursor-pointer">
              <i class="fab fa-weixin text-green-500"></i>
            </div>
            <div class="w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center hover:bg-gray-50 cursor-pointer">
              <i class="fab fa-weibo text-red-500"></i>
            </div>
            <div class="w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center hover:bg-gray-50 cursor-pointer">
              <i class="fab fa-qq text-blue-500"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="border-t-2 border-black mt-8 pt-6 text-center">
        <p class="text-sm font-bold opacity-60">© 2026 宠遇 PetAdoption. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>
