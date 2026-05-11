<template>
  <div class="px-6 lg:max-w-4xl lg:mx-auto lg:px-8 pb-10">
    <!-- 头部 -->
    <header class="flex items-center gap-4 mt-6 mb-8">
      <router-link
        to="/user"
        class="w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center shadow-[2px_2px_0px_black]"
      >
        <i class="fas fa-chevron-left"></i>
      </router-link>
      <h1 class="text-2xl font-black">申请记录</h1>
    </header>

    <!-- 状态筛选 -->
    <div class="flex gap-2 mb-6 overflow-x-auto no-scrollbar">
      <button
        v-for="status in statusOptions"
        :key="status.value"
        @click="currentStatus = status.value"
        class="flex-shrink-0 px-4 py-2 border-2 border-black rounded-xl font-bold text-sm transition-colors"
        :class="currentStatus === status.value ? 'bg-pink-400 text-white' : 'bg-white'"
      >
        {{ status.label }}
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="w-16 h-16 bg-white border-4 border-black rounded-2xl flex items-center justify-center shadow-playful animate-pulse">
        <i class="fas fa-spinner text-3xl text-pink-500 animate-spin"></i>
      </div>
    </div>

    <!-- 申请列表 -->
    <div v-else-if="applications.length > 0" class="space-y-6">
      <div
        v-for="app in applications"
        :key="app.id"
        class="card-playful p-4 flex gap-4"
      >
        <img
          :src="getAppImage(app)"
          class="w-20 h-20 rounded-2xl object-cover border-2 border-black flex-shrink-0"
          :class="{ 'opacity-50': app.status === 3 || app.status === 4 }"
        />
        <div class="flex-grow min-w-0">
          <div class="flex justify-between items-start mb-1 gap-2">
            <h3 class="font-black truncate" :class="{ 'opacity-50': app.status === 3 || app.status === 4 }">
              {{ app.petName || '宠物' }}
            </h3>
            <span
              class="flex-shrink-0 px-2 py-0.5 rounded-lg border text-[10px] font-black uppercase"
              :class="getStatusClass(app.status)"
            >
              {{ getStatusText(app.status) }}
            </span>
          </div>
          <p class="text-[10px] font-bold opacity-40 mb-2 uppercase tracking-widest">
            {{ app.petBreed || '未知品种' }} • 提交于 {{ formatDate(app.createTime, 'MM-DD') }}
          </p>
          <div class="flex gap-2 flex-wrap">
            <router-link
              :to="`/user/application/${app.id}`"
              class="bg-white border-2 border-black px-3 py-1 rounded-lg text-[10px] font-black shadow-[2px_2px_0px_black]"
            >
              查看详情
            </router-link>
            <button
              v-if="app.status === 0"
              @click="handleCancel(app.id)"
              class="bg-red-50 border-2 border-red-500 text-red-500 px-3 py-1 rounded-lg text-[10px] font-black"
            >
              取消申请
            </button>
            <button
              v-if="app.status === 2"
              class="bg-[#4ADE80] border-2 border-black px-3 py-1 rounded-lg text-[10px] font-black shadow-[2px_2px_0px_black]"
            >
              预约面谈
            </button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <Pagination
        v-if="totalPages > 1"
        v-model:currentPage="currentPage"
        :totalPages="totalPages"
        @change="loadApplications"
      />
    </div>

    <!-- 空状态 -->
    <EmptyState
      v-else
      icon="📋"
      title="暂无申请记录"
      description="快去看看有没有心仪的小可爱吧~"
    >
      <template #action>
        <router-link
          to="/pets"
          class="mt-4 inline-block px-6 py-2 bg-pink-400 text-white border-3 border-black rounded-2xl font-bold shadow-playful-sm"
        >
          去逛逛
        </router-link>
      </template>
    </EmptyState>

    <!-- 确认弹窗 -->
    <Modal
      v-model:visible="showCancelModal"
      title="确认取消"
      confirmText="确认取消"
      confirmClass="btn-pink"
      @confirm="confirmCancel"
    >
      <div class="text-center py-4">
        <p class="font-bold">确定要取消这个申请吗？</p>
        <p class="text-sm opacity-60 mt-2">取消后不可恢复</p>
      </div>
    </Modal>

    <Loading :visible="cancelling" text="取消中..." />
  </div>
</template>

<script setup>
import { ref, watch, onMounted, inject } from 'vue'
import { adoptionApi } from '@/api/adoption'
import { formatDate } from '@/utils/format'
import Pagination from '@/components/Pagination.vue'
import EmptyState from '@/components/EmptyState.vue'
import Modal from '@/components/Modal.vue'
import Loading from '@/components/Loading.vue'

const toast = inject('toast')

const applications = ref([])
const loading = ref(true)
const currentPage = ref(1)
const totalPages = ref(1)
const currentStatus = ref('')
const showCancelModal = ref(false)
const cancelId = ref(null)
const cancelling = ref(false)

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待审核', value: '0' },
  { label: '审核中', value: '1' },
  { label: '已通过', value: '2' },
  { label: '已拒绝', value: '3' },
  { label: '已取消', value: '4' }
]

const getAppImage = (app) => {
  if (app.petImages) {
    return app.petImages.split(',')[0]
  }
  return 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=100'
}

const getStatusText = (status) => {
  const texts = ['待审核', '审核中', '已通过', '已拒绝', '已取消']
  return texts[status] || '未知'
}

const getStatusClass = (status) => {
  const classes = [
    'bg-yellow-100 text-yellow-600 border-yellow-600',
    'bg-blue-100 text-blue-600 border-blue-600',
    'bg-green-100 text-green-600 border-green-600',
    'bg-red-100 text-red-600 border-red-600',
    'bg-gray-100 text-gray-600 border-gray-600'
  ]
  return classes[status] || classes[4]
}

const loadApplications = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: 10
    }
    if (currentStatus.value) {
      params.status = parseInt(currentStatus.value)
    }

    const res = await adoptionApi.getList(params)
    if (res.code === 200) {
      applications.value = res.data?.list || res.data || []
      const total = res.data?.total || applications.value.length
      totalPages.value = Math.ceil(total / 10)
    }
  } catch (e) {
    console.error('Failed to load applications:', e)
  } finally {
    loading.value = false
  }
}

const handleCancel = (id) => {
  cancelId.value = id
  showCancelModal.value = true
}

const confirmCancel = async () => {
  showCancelModal.value = false
  cancelling.value = true

  try {
    const res = await adoptionApi.cancel(cancelId.value)
    if (res.code === 200) {
      toast.success('取消成功')
      loadApplications()
    } else {
      toast.error(res.message || '取消失败')
    }
  } catch (e) {
    toast.error('网络错误')
  } finally {
    cancelling.value = false
    cancelId.value = null
  }
}

watch(currentStatus, () => {
  currentPage.value = 1
  loadApplications()
})

onMounted(() => {
  loadApplications()
})
</script>
