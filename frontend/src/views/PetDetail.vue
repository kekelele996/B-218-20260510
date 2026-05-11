<template>
  <div>
    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center min-h-[60vh]">
      <div class="w-16 h-16 bg-white border-4 border-black rounded-2xl flex items-center justify-center shadow-playful animate-pulse">
        <i class="fas fa-spinner text-3xl text-pink-500 animate-spin"></i>
      </div>
    </div>

    <template v-else-if="pet">
      <!-- 图片区域 -->
      <div class="relative lg:max-w-5xl lg:mx-auto lg:px-8 lg:pt-8">
        <PetCarousel
          :images="petImages"
          :alt="pet.name"
          :showThumbnails="true"
          containerClass="h-[350px] md:h-[400px] lg:h-[500px] lg:rounded-3xl lg:border-4 lg:border-black lg:overflow-hidden"
        />

        <!-- 返回按钮 -->
        <router-link
          to="/pets"
          class="absolute top-4 left-4 lg:top-12 lg:left-12 w-12 h-12 bg-white border-2 border-black rounded-2xl flex items-center justify-center shadow-[4px_4px_0px_black] z-10"
        >
          <i class="fas fa-times text-xl"></i>
        </router-link>

        <!-- 收藏按钮 -->
        <button
          @click="toggleFavorite"
          class="absolute top-4 right-4 lg:top-12 lg:right-12 w-12 h-12 bg-white border-2 border-black rounded-2xl flex items-center justify-center shadow-[4px_4px_0px_black] z-10"
          :class="isFavorite ? 'text-pink-500' : 'text-gray-400'"
        >
          <i :class="isFavorite ? 'fas fa-heart' : 'far fa-heart'" class="text-xl"></i>
        </button>
      </div>

      <!-- 详情内容 -->
      <main class="px-6 -mt-12 relative z-20 lg:max-w-5xl lg:mx-auto lg:px-8">
        <div class="bg-white border-4 border-black rounded-[2.5rem] p-6 shadow-[8px_8px_0px_rgba(0,0,0,1)] mb-8">
          <!-- 基本信息 -->
          <div class="flex justify-between items-start mb-2">
            <h2 class="text-3xl lg:text-4xl font-black">{{ pet.name }}</h2>
            <span
              class="px-3 py-1 rounded-full border-2 border-black text-sm font-bold"
              :class="pet.gender === '公' ? 'bg-blue-400 text-white' : 'bg-pink-400 text-white'"
            >
              {{ pet.gender === '公' ? '男生' : '女生' }}
            </span>
          </div>
          <p class="font-bold text-sm opacity-60 mb-6 uppercase tracking-widest">
            {{ pet.breed }} • {{ pet.type }}
          </p>

          <!-- 属性卡片 -->
          <div class="grid grid-cols-3 lg:grid-cols-6 gap-3 mb-8 text-center">
            <div class="bg-blue-50 p-3 rounded-2xl border-2 border-black">
              <p class="text-[10px] font-bold opacity-40">体重</p>
              <p class="text-sm font-black">{{ pet.weight ? `${pet.weight} kg` : '未知' }}</p>
            </div>
            <div class="bg-yellow-50 p-3 rounded-2xl border-2 border-black">
              <p class="text-[10px] font-bold opacity-40">年龄</p>
              <p class="text-sm font-black">{{ formatAge(pet.age) }}</p>
            </div>
            <div class="bg-green-50 p-3 rounded-2xl border-2 border-black">
              <p class="text-[10px] font-bold opacity-40">健康</p>
              <p class="text-sm font-black">{{ pet.vaccinated ? '已打疫苗' : '待疫苗' }}</p>
            </div>
            <div class="bg-purple-50 p-3 rounded-2xl border-2 border-black">
              <p class="text-[10px] font-bold opacity-40">体型</p>
              <p class="text-sm font-black">{{ pet.size || '中型' }}</p>
            </div>
            <div class="bg-pink-50 p-3 rounded-2xl border-2 border-black">
              <p class="text-[10px] font-bold opacity-40">毛色</p>
              <p class="text-sm font-black">{{ pet.color || '未知' }}</p>
            </div>
            <div class="bg-orange-50 p-3 rounded-2xl border-2 border-black">
              <p class="text-[10px] font-bold opacity-40">绝育</p>
              <p class="text-sm font-black">{{ pet.neutered ? '已绝育' : '未绝育' }}</p>
            </div>
          </div>

          <!-- 详细信息 -->
          <div class="space-y-6">
            <section v-if="pet.personality">
              <h3 class="font-black text-lg mb-2 flex items-center gap-2">
                <i class="fas fa-quote-left text-pink-400"></i> 关于它
              </h3>
              <p class="text-sm font-bold opacity-70 leading-relaxed">{{ pet.personality }}</p>
            </section>

            <section v-if="pet.healthDesc">
              <h3 class="font-black text-lg mb-2 flex items-center gap-2">
                <i class="fas fa-heart text-red-400"></i> 健康状况
              </h3>
              <p class="text-sm font-bold opacity-70 leading-relaxed">{{ pet.healthDesc }}</p>
            </section>

            <section v-if="pet.requirement">
              <h3 class="font-black text-lg mb-2 flex items-center gap-2">
                <i class="fas fa-home text-blue-400"></i> 领养要求
              </h3>
              <p class="text-sm font-bold opacity-70 leading-relaxed whitespace-pre-line">{{ pet.requirement }}</p>
            </section>
          </div>
        </div>

        <!-- 申请按钮 -->
        <div class="pb-8">
          <template v-if="pet.status === 1">
            <router-link
              v-if="isLoggedIn"
              :to="`/apply/${pet.id}`"
              class="block w-full btn-main text-center"
            >
              申请领养
            </router-link>
            <router-link
              v-else
              :to="{ name: 'Login', query: { redirect: `/apply/${pet.id}` } }"
              class="block w-full btn-main text-center"
            >
              登录后申请领养
            </router-link>
          </template>
          <div v-else class="text-center py-4">
            <span class="inline-block bg-gray-200 text-gray-600 px-6 py-3 rounded-2xl border-3 border-black font-bold">
              {{ pet.status === 2 ? '已被领养' : '暂不可领养' }}
            </span>
          </div>
        </div>
      </main>
    </template>

    <!-- 未找到 -->
    <EmptyState
      v-else
      icon="🐾"
      title="宠物不存在"
      description="这只小可爱可能已经找到了新家~"
    >
      <template #action>
        <router-link
          to="/pets"
          class="mt-4 inline-block px-6 py-2 bg-pink-400 text-white border-3 border-black rounded-2xl font-bold shadow-playful-sm"
        >
          查看其他宠物
        </router-link>
      </template>
    </EmptyState>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { petApi } from '@/api/pet'
import { useUserStore } from '@/store/user'
import { formatAge } from '@/utils/format'
import PetCarousel from '@/components/PetCarousel.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const userStore = useUserStore()

const pet = ref(null)
const loading = ref(true)
const isFavorite = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)

const petImages = computed(() => {
  if (pet.value?.images && Array.isArray(pet.value.images)) {
    return pet.value.images.filter(img => img)
  }
  return ['https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=800']
})

const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
}

const loadPetDetail = async () => {
  loading.value = true
  try {
    const res = await petApi.getDetail(route.params.id)
    if (res.code === 200) {
      pet.value = res.data
    }
  } catch (e) {
    console.error('Failed to load pet:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPetDetail()
})
</script>
