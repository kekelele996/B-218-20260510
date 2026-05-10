<template>
  <div class="px-6 lg:max-w-3xl lg:mx-auto lg:px-8 pb-8">
    <!-- 用户信息头部 -->
    <header class="text-center mt-10 mb-8">
      <div class="inline-block relative">
        <div class="w-24 h-24 bg-white border-4 border-black rounded-[2.5rem] overflow-hidden shadow-[6px_6px_0px_#FF85A2]">
          <img
            v-if="userInfo?.avatar"
            :src="userInfo.avatar"
            class="w-full h-full object-cover"
          />
          <div v-else class="w-full h-full bg-pink-100 flex items-center justify-center text-4xl">
            🐾
          </div>
        </div>
        <div class="absolute -bottom-1 -right-1 w-8 h-8 bg-yellow-400 border-2 border-black rounded-full flex items-center justify-center shadow-[2px_2px_0px_black] cursor-pointer">
          <i class="fas fa-camera text-xs"></i>
        </div>
      </div>
      <h2 class="text-2xl font-black mt-4">{{ userInfo?.realName || '用户' }}</h2>
      <p class="text-[10px] font-bold opacity-40 uppercase tracking-[0.2em] mt-1">
        ID: PA-{{ userInfo?.id?.toString().padStart(8, '0') || '00000000' }}
      </p>
    </header>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-2 gap-4 mb-8">
      <router-link to="/user/applications" class="card-playful p-4 text-center block">
        <p class="text-[10px] font-black opacity-40 uppercase mb-1">领养申请</p>
        <p class="text-3xl font-black">{{ applicationCount }}</p>
      </router-link>
      <div class="card-playful p-4 text-center">
        <p class="text-[10px] font-black opacity-40 uppercase mb-1">收藏关注</p>
        <p class="text-3xl font-black">0</p>
      </div>
    </div>

    <!-- 菜单列表 -->
    <div class="space-y-4">
      <router-link
        to="/user/profile"
        class="flex items-center justify-between p-5 bg-white border-4 border-black rounded-[1.5rem] shadow-[4px_4px_0px_black] hover:shadow-[6px_6px_0px_black] transition-all"
      >
        <div class="flex items-center gap-4">
          <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center text-blue-500 border-2 border-black">
            <i class="fas fa-user-edit"></i>
          </div>
          <span class="font-black">个人资料修改</span>
        </div>
        <i class="fas fa-chevron-right opacity-30"></i>
      </router-link>

      <router-link
        to="/user/password"
        class="flex items-center justify-between p-5 bg-white border-4 border-black rounded-[1.5rem] shadow-[4px_4px_0px_black] hover:shadow-[6px_6px_0px_black] transition-all"
      >
        <div class="flex items-center gap-4">
          <div class="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center text-green-500 border-2 border-black">
            <i class="fas fa-lock"></i>
          </div>
          <span class="font-black">修改密码</span>
        </div>
        <i class="fas fa-chevron-right opacity-30"></i>
      </router-link>

      <router-link
        to="/user/applications"
        class="flex items-center justify-between p-5 bg-white border-4 border-black rounded-[1.5rem] shadow-[4px_4px_0px_black] hover:shadow-[6px_6px_0px_black] transition-all"
      >
        <div class="flex items-center gap-4">
          <div class="w-10 h-10 bg-purple-100 rounded-xl flex items-center justify-center text-purple-500 border-2 border-black">
            <i class="fas fa-clipboard-list"></i>
          </div>
          <span class="font-black">申请记录</span>
        </div>
        <i class="fas fa-chevron-right opacity-30"></i>
      </router-link>
    </div>

    <!-- 退出登录 -->
    <button
      @click="handleLogout"
      class="w-full mt-10 py-4 font-black text-red-500 bg-white border-4 border-black rounded-2xl shadow-[4px_4px_0px_black] hover:shadow-[6px_6px_0px_black] transition-all"
    >
      退出当前账号
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { adoptionApi } from '@/api/adoption'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const applicationCount = ref(0)

const loadApplicationCount = async () => {
  try {
    const res = await adoptionApi.getList({ page: 1, pageSize: 1 })
    if (res.code === 200) {
      applicationCount.value = res.data?.total || 0
    }
  } catch (e) {
    // ignore
  }
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}

onMounted(() => {
  loadApplicationCount()
})
</script>
