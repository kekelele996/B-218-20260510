<template>
  <div class="px-6 lg:max-w-3xl lg:mx-auto lg:px-8 pb-8">
    <!-- 头部 -->
    <header class="flex items-center gap-4 mt-6 mb-8">
      <router-link
        to="/user/applications"
        class="w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center shadow-[2px_2px_0px_black]"
      >
        <i class="fas fa-chevron-left"></i>
      </router-link>
      <h1 class="text-2xl font-black">申请详情</h1>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="w-16 h-16 bg-white border-4 border-black rounded-2xl flex items-center justify-center shadow-playful animate-pulse">
        <i class="fas fa-spinner text-3xl text-pink-500 animate-spin"></i>
      </div>
    </div>

    <template v-else-if="application">
      <div class="space-y-6">
        <!-- 宠物信息 -->
        <div class="flex items-center gap-4 bg-white border-4 border-black p-4 rounded-3xl shadow-playful">
          <img
            :src="petImage"
            class="w-20 h-20 rounded-2xl object-cover border-2 border-black"
          />
          <div class="flex-grow">
            <h3 class="text-xl font-black">{{ application.petName || '宠物' }}</h3>
            <p class="text-xs font-bold opacity-60">{{ application.petBreed }}</p>
          </div>
          <span
            class="px-3 py-1 rounded-lg border-2 text-xs font-black"
            :class="getStatusClass(application.status)"
          >
            {{ getStatusText(application.status) }}
          </span>
        </div>

        <!-- 申请信息 -->
        <div class="bg-white border-4 border-black rounded-3xl p-6 shadow-playful">
          <h3 class="font-black text-lg mb-4 flex items-center gap-2">
            <i class="fas fa-clipboard-list text-blue-400"></i> 申请信息
          </h3>

          <div class="space-y-4">
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="font-bold opacity-60">申请时间</span>
              <span class="font-black">{{ formatDate(application.createTime, 'YYYY-MM-DD HH:mm') }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="font-bold opacity-60">居住类型</span>
              <span class="font-black">{{ application.livingType }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="font-bold opacity-60">住房情况</span>
              <span class="font-black">{{ application.housingType }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="font-bold opacity-60">是否有院子</span>
              <span class="font-black">{{ application.hasYard ? '是' : '否' }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="font-bold opacity-60">养宠经验</span>
              <span class="font-black">{{ application.experience }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="font-bold opacity-60">每日陪伴时间</span>
              <span class="font-black">{{ application.dailyTime }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-gray-100">
              <span class="font-bold opacity-60">现有宠物</span>
              <span class="font-black">{{ application.currentPets || '无' }}</span>
            </div>
            <div class="py-2 border-b border-gray-100">
              <span class="font-bold opacity-60 block mb-2">联系地址</span>
              <span class="font-black">{{ application.contactAddress }}</span>
            </div>
            <div class="py-2">
              <span class="font-bold opacity-60 block mb-2">领养理由</span>
              <p class="font-bold text-sm leading-relaxed">{{ application.reason }}</p>
            </div>
          </div>
        </div>

        <!-- 审核备注 -->
        <div v-if="application.remark" class="bg-white border-4 border-black rounded-3xl p-6 shadow-playful">
          <h3 class="font-black text-lg mb-4 flex items-center gap-2">
            <i class="fas fa-comment-alt text-yellow-400"></i> 审核备注
          </h3>
          <p class="font-bold text-sm leading-relaxed">{{ application.remark }}</p>
        </div>

        <!-- 操作按钮 -->
        <div v-if="application.status === 0" class="pt-4">
          <button
            @click="showCancelModal = true"
            class="w-full py-4 font-black text-red-500 bg-white border-4 border-black rounded-2xl shadow-[4px_4px_0px_black]"
          >
            取消申请
          </button>
        </div>
      </div>
    </template>

    <!-- 未找到 -->
    <EmptyState
      v-else
      icon="📋"
      title="申请不存在"
      description="该申请可能已被删除"
    />

    <!-- 确认弹窗 -->
    <Modal
      v-model:visible="showCancelModal"
      title="确认取消"
      confirmText="确认取消"
      confirmClass="btn-pink"
      @confirm="handleCancel"
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
import { ref, computed, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { adoptionApi } from '@/api/adoption'
import { formatDate } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'
import Modal from '@/components/Modal.vue'
import Loading from '@/components/Loading.vue'

const route = useRoute()
const router = useRouter()
const toast = inject('toast')

const application = ref(null)
const loading = ref(true)
const showCancelModal = ref(false)
const cancelling = ref(false)

const petImage = computed(() => {
  if (application.value?.petImages) {
    return application.value.petImages.split(',')[0]
  }
  return 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=100'
})

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

const loadApplication = async () => {
  loading.value = true
  try {
    const res = await adoptionApi.getDetail(route.params.id)
    if (res.code === 200) {
      application.value = res.data
    }
  } catch (e) {
    console.error('Failed to load application:', e)
  } finally {
    loading.value = false
  }
}

const handleCancel = async () => {
  showCancelModal.value = false
  cancelling.value = true

  try {
    const res = await adoptionApi.cancel(route.params.id)
    if (res.code === 200) {
      toast.success('取消成功')
      router.push('/user/applications')
    } else {
      toast.error(res.message || '取消失败')
    }
  } catch (e) {
    toast.error('网络错误')
  } finally {
    cancelling.value = false
  }
}

onMounted(() => {
  loadApplication()
})
</script>
