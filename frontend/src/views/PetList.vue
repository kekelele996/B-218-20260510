<template>
  <div class="px-6 lg:max-w-7xl lg:mx-auto lg:px-8">
    <!-- 头部 -->
    <header class="flex items-center gap-4 mt-6 mb-8">
      <router-link
        to="/"
        class="lg:hidden w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center shadow-[2px_2px_0px_black]"
      >
        <i class="fas fa-chevron-left"></i>
      </router-link>
      <h1 class="text-2xl lg:text-3xl font-black">全部领养</h1>
    </header>

    <!-- 筛选器 -->
    <PetFilter
      :initial-type="initialType"
      :initial-keyword="initialKeyword"
      @filter="handleFilter"
    />

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="w-16 h-16 bg-white border-4 border-black rounded-2xl flex items-center justify-center shadow-playful animate-pulse">
        <i class="fas fa-spinner text-3xl text-pink-500 animate-spin"></i>
      </div>
    </div>

    <!-- 宠物列表 -->
    <div v-else-if="pets.length > 0" class="mt-6">
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-x-4 gap-y-6 lg:gap-6">
        <PetCard v-for="pet in pets" :key="pet.id" :pet="pet" />
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="mt-8 mb-12">
        <Pagination
          v-model:currentPage="currentPage"
          :totalPages="totalPages"
          @change="loadPets"
        />
      </div>

      <!-- 底部间距 -->
      <div v-else class="mb-12"></div>
    </div>

    <!-- 空状态 -->
    <EmptyState
      v-else
      icon="🔍"
      title="没有找到宠物"
      description="换个筛选条件试试吧~"
    >
      <template #action>
        <button
          @click="resetFilters"
          class="mt-4 px-6 py-2 bg-pink-400 text-white border-3 border-black rounded-2xl font-bold shadow-playful-sm"
        >
          重置筛选
        </button>
      </template>
    </EmptyState>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { petApi } from '@/api/pet'
import PetCard from '@/components/PetCard.vue'
import PetFilter from '@/components/PetFilter.vue'
import Pagination from '@/components/Pagination.vue'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()

const pets = ref([])
const loading = ref(true)
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 12
const filters = ref({})

// 从 URL 获取初始参数
const initialType = computed(() => route.query.type || '')
const initialKeyword = computed(() => route.query.keyword || '')

const handleFilter = (newFilters) => {
  filters.value = newFilters
  currentPage.value = 1
  loadPets()
}

const resetFilters = () => {
  filters.value = {}
  currentPage.value = 1
  loadPets()
}

const loadPets = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize,
      status: 1,
      ...filters.value
    }

    // 处理年龄范围
    if (filters.value.ageRange) {
      const [minAge, maxAge] = filters.value.ageRange.split('-')
      params.minAge = parseInt(minAge)
      params.maxAge = parseInt(maxAge)
      delete params.ageRange
    }

    // 处理排序
    if (filters.value.sort) {
      if (filters.value.sort === 'latest') {
        params.orderBy = 'create_time'
        params.orderDir = 'desc'
      } else if (filters.value.sort === 'age_asc') {
        params.orderBy = 'age'
        params.orderDir = 'asc'
      } else if (filters.value.sort === 'age_desc') {
        params.orderBy = 'age'
        params.orderDir = 'desc'
      }
      delete params.sort
    }

    const res = await petApi.getList(params)
    if (res.code === 200) {
      pets.value = res.data?.list || res.data || []
      const total = res.data?.total || pets.value.length
      totalPages.value = Math.ceil(total / pageSize)
    }
  } catch (e) {
    console.error('Failed to load pets:', e)
  } finally {
    loading.value = false
  }
}

watch(() => route.query, (query) => {
  if (query.type) {
    filters.value.type = query.type
  }
  if (query.keyword) {
    filters.value.keyword = query.keyword
  }
  loadPets()
}, { immediate: false })

onMounted(() => {
  if (route.query.type) {
    filters.value.type = route.query.type
  }
  if (route.query.keyword) {
    filters.value.keyword = route.query.keyword
  }
  loadPets()
})
</script>
