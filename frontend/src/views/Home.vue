<template>
  <div class="px-6 lg:max-w-7xl lg:mx-auto lg:px-8">
    <!-- 移动端头部 -->
    <header class="lg:hidden text-center mt-6 mb-8">
      <h1 class="text-4xl font-black italic tracking-tighter mb-2" style="font-family: 'ZCOOL KuaiLe', cursive;">
        寻找你的<span class="text-pink-500 underline decoration-wavy">超萌伙伴</span>!
      </h1>
      <p class="font-bold text-sm opacity-60">让每一只流浪的小生命都有个家</p>
    </header>

    <!-- PC端头部 -->
    <header class="hidden lg:block text-center py-12">
      <h1 class="text-5xl font-black italic tracking-tighter mb-4" style="font-family: 'ZCOOL KuaiLe', cursive;">
        寻找你的<span class="text-pink-500 underline decoration-wavy">超萌伙伴</span>!
      </h1>
      <p class="font-bold text-lg opacity-60">让每一只流浪的小生命都有个家</p>
    </header>

    <!-- 搜索框 -->
    <div class="bg-white border-4 border-black rounded-[2rem] p-2 flex mb-8 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] lg:max-w-xl lg:mx-auto">
      <input
        v-model="searchKeyword"
        type="text"
        placeholder="想领养什么品种？"
        class="bg-transparent flex-grow px-4 font-bold outline-none text-sm"
        @keyup.enter="goToSearch"
      />
      <button
        @click="goToSearch"
        class="bg-pink-500 text-white w-10 h-10 rounded-[1.2rem] border-2 border-black"
      >
        <i class="fas fa-search"></i>
      </button>
    </div>

    <!-- 分类图标 -->
    <div class="flex gap-4 overflow-x-auto no-scrollbar mb-8 lg:justify-center lg:gap-8">
      <div
        v-for="category in categories"
        :key="category.type"
        @click="goToCategory(category.type)"
        class="flex-shrink-0 flex flex-col items-center gap-2 cursor-pointer group"
      >
        <div class="w-16 h-16 lg:w-20 lg:h-20 bg-white border-4 border-black rounded-2xl flex items-center justify-center text-3xl lg:text-4xl shadow-[4px_4px_0px_black] group-hover:shadow-[6px_6px_0px_black] transition-all">
          {{ category.icon }}
        </div>
        <span class="text-xs lg:text-sm font-black">{{ category.name }}</span>
      </div>
    </div>

    <!-- 今日推荐 -->
    <div class="flex justify-between items-end mb-6">
      <h2 class="text-2xl lg:text-3xl font-black italic">今日推荐</h2>
      <router-link to="/pets" class="text-sm lg:text-base font-black text-blue-500 ">查看全部</router-link>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="w-16 h-16 bg-white border-4 border-black rounded-2xl flex items-center justify-center shadow-playful animate-pulse">
        <i class="fas fa-spinner text-3xl text-pink-500 animate-spin"></i>
      </div>
    </div>

    <!-- 宠物列表 -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 lg:gap-8">
      <div
        v-for="pet in recommendedPets"
        :key="pet.id"
        class="card-playful"
        :style="{ '--shadow-color': getRandomColor() }"
      >
        <img
          :src="getPetImage(pet)"
          :alt="pet.name"
          class="w-full h-56 lg:h-64 object-cover border-b-4 border-black"
        />
        <div class="p-5">
          <div class="flex justify-between items-center mb-1">
            <h3 class="text-2xl font-black uppercase">{{ pet.name }}</h3>
            <span
              class="px-3 py-1 rounded-full border-2 border-black text-xs font-bold"
              :class="pet.gender === '公' ? 'bg-blue-400 text-white' : 'bg-pink-400 text-white'"
            >
              {{ pet.gender === '公' ? '男生' : '女生' }}
            </span>
          </div>
          <p class="font-bold text-xs opacity-60 mb-4 uppercase">
            {{ pet.breed }} • {{ formatAge(pet.age) }} • {{ pet.personality ? truncateText(pet.personality, 10) : '温顺可爱' }}
          </p>
          <button
            @click="goToPet(pet.id)"
            class="block w-full btn-playful font-black py-3 rounded-2xl text-center"
          >
            带它回家
          </button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <EmptyState
      v-if="!loading && recommendedPets.length === 0"
      icon="🐾"
      title="暂无推荐"
      description="暂时没有可领养的宠物，请稍后再来~"
    />

    <!-- 底部间距 -->
    <div class="mb-12"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { petApi } from '@/api/pet'
import { formatAge, truncateText } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()

const searchKeyword = ref('')
const recommendedPets = ref([])
const loading = ref(true)

const categories = [
  { type: '狗', name: '汪星人', icon: '🐶' },
  { type: '猫', name: '喵主子', icon: '🐱' },
  { type: '其他', name: '其他', icon: '🐹' }
]

const colors = ['#FFD600', '#FF85A2', '#60A5FA', '#4ADE80', '#A78BFA']

const getRandomColor = () => {
  return colors[Math.floor(Math.random() * colors.length)]
}

const getPetImage = (pet) => {
  if (pet.image) {
    return pet.image
  }
  return 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=800'
}

const goToSearch = () => {
  if (searchKeyword.value) {
    router.push({ path: '/pets', query: { keyword: searchKeyword.value } })
  } else {
    router.push('/pets')
  }
}

const goToCategory = (type) => {
  router.push({ path: '/pets', query: { type } })
}

const goToPet = (petId) => {
  router.push(`/pet/${petId}`)
}

const loadRecommendedPets = async () => {
  loading.value = true
  try {
    const res = await petApi.getList({ page: 1, pageSize: 8, status: 1 })
    if (res.code === 200) {
      recommendedPets.value = res.data?.list || res.data || []
    }
  } catch (e) {
    console.error('Failed to load pets:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRecommendedPets()
})
</script>
