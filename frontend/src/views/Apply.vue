<template>
  <div class="px-6 lg:max-w-3xl lg:mx-auto lg:px-8 pb-8">
    <!-- 头部 -->
    <header class="flex items-center gap-4 mt-6 mb-8">
      <router-link
        :to="`/pet/${petId}`"
        class="w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center shadow-[2px_2px_0px_black]"
      >
        <i class="fas fa-chevron-left"></i>
      </router-link>
      <h1 class="text-2xl font-black">填写申请表</h1>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="w-16 h-16 bg-white border-4 border-black rounded-2xl flex items-center justify-center shadow-playful animate-pulse">
        <i class="fas fa-spinner text-3xl text-pink-500 animate-spin"></i>
      </div>
    </div>

    <template v-else-if="pet">
      <div class="space-y-8">
        <!-- 宠物信息卡片 -->
        <div class="flex items-center gap-4 bg-white border-4 border-black p-4 rounded-3xl">
          <img
            :src="petImage"
            class="w-16 h-16 rounded-2xl object-cover border-2 border-black"
          />
          <div>
            <p class="text-xs font-bold opacity-40">您正在申请领养</p>
            <h3 class="text-xl font-black">{{ pet.name }}</h3>
            <p class="text-xs font-bold opacity-60">{{ pet.breed }}</p>
          </div>
        </div>

        <!-- 申请表单 -->
        <section class="space-y-6">
          <!-- 居住类型 -->
          <div>
            <label class="block font-black mb-2 ml-2">居住类型 *</label>
            <div class="grid grid-cols-3 gap-2">
              <button
                v-for="type in livingTypes"
                :key="type"
                @click="form.livingType = type"
                class="border-2 border-black py-2 rounded-xl font-black text-xs transition-colors"
                :class="form.livingType === type ? 'bg-yellow-300' : 'bg-white'"
              >
                {{ type }}
              </button>
            </div>
          </div>

          <!-- 住房情况 -->
          <div>
            <label class="block font-black mb-2 ml-2">住房情况 *</label>
            <div class="grid grid-cols-2 gap-2">
              <button
                v-for="type in housingTypes"
                :key="type"
                @click="form.housingType = type"
                class="border-2 border-black py-2 rounded-xl font-black text-xs transition-colors"
                :class="form.housingType === type ? 'bg-yellow-300' : 'bg-white'"
              >
                {{ type }}
              </button>
            </div>
          </div>

          <!-- 是否有院子 -->
          <div>
            <label class="block font-black mb-2 ml-2">是否有院子 *</label>
            <div class="grid grid-cols-2 gap-2">
              <button
                @click="form.hasYard = true"
                class="border-2 border-black py-2 rounded-xl font-black text-xs transition-colors"
                :class="form.hasYard === true ? 'bg-yellow-300' : 'bg-white'"
              >
                是
              </button>
              <button
                @click="form.hasYard = false"
                class="border-2 border-black py-2 rounded-xl font-black text-xs transition-colors"
                :class="form.hasYard === false ? 'bg-yellow-300' : 'bg-white'"
              >
                否
              </button>
            </div>
          </div>

          <!-- 养宠经验 -->
          <div>
            <label class="block font-black mb-2 ml-2">养宠经验 *</label>
            <select v-model="form.experience" class="input-playful">
              <option value="">请选择</option>
              <option v-for="exp in experiences" :key="exp" :value="exp">{{ exp }}</option>
            </select>
          </div>

          <!-- 每日陪伴时间 -->
          <div>
            <label class="block font-black mb-2 ml-2">每日陪伴时间 *</label>
            <select v-model="form.dailyTime" class="input-playful">
              <option value="">请选择</option>
              <option v-for="time in dailyTimes" :key="time" :value="time">{{ time }}</option>
            </select>
          </div>

          <!-- 家中现有宠物 -->
          <div>
            <label class="block font-black mb-2 ml-2">家中现有宠物</label>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="pet in currentPetOptions"
                :key="pet"
                @click="toggleCurrentPet(pet)"
                class="border-2 border-black px-4 py-2 rounded-xl font-black text-xs transition-colors"
                :class="form.currentPets.includes(pet) ? 'bg-yellow-300' : 'bg-white'"
              >
                {{ pet }}
              </button>
            </div>
          </div>

          <!-- 领养理由 -->
          <div>
            <label class="block font-black mb-2 ml-2">领养理由 * <span class="text-xs font-normal opacity-60">(50-500字)</span></label>
            <textarea
              v-model="form.reason"
              class="input-playful h-32"
              placeholder="请详细说明您想领养它的初衷及生活环境..."
            ></textarea>
            <p class="text-right text-xs font-bold mt-1" :class="form.reason.length < 50 || form.reason.length > 500 ? 'text-red-500' : 'opacity-60'">
              {{ form.reason.length }}/500
            </p>
          </div>

          <!-- 详细地址 -->
          <div>
            <label class="block font-black mb-2 ml-2">详细地址 *</label>
            <input
              v-model="form.contactAddress"
              type="text"
              class="input-playful"
              placeholder="请输入您的联系地址"
            />
          </div>

          <p v-if="errorMessage" class="text-red-500 font-bold text-sm text-center">{{ errorMessage }}</p>

          <button
            @click="showConfirmModal = true"
            :disabled="!isFormValid || submitting"
            class="btn-main mt-4"
          >
            提交申请
          </button>
        </section>
      </div>
    </template>

    <!-- 确认弹窗 -->
    <Modal
      v-model:visible="showConfirmModal"
      title="确认提交"
      confirmText="确认提交"
      @confirm="handleSubmit"
    >
      <div class="text-center py-4">
        <div class="w-16 h-16 bg-yellow-100 border-3 border-black rounded-2xl mx-auto flex items-center justify-center text-3xl mb-4">
          🐾
        </div>
        <p class="font-bold">确定要提交领养申请吗？</p>
        <p class="text-sm opacity-60 mt-2">提交后我们会尽快审核您的申请</p>
      </div>
    </Modal>

    <!-- Loading -->
    <Loading :visible="submitting" text="提交中..." />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { petApi } from '@/api/pet'
import { adoptionApi } from '@/api/adoption'
import Modal from '@/components/Modal.vue'
import Loading from '@/components/Loading.vue'

const route = useRoute()
const router = useRouter()
const toast = inject('toast')

const petId = route.params.petId
const pet = ref(null)
const loading = ref(true)
const submitting = ref(false)
const showConfirmModal = ref(false)
const errorMessage = ref('')

const form = reactive({
  livingType: '',
  housingType: '',
  hasYard: null,
  experience: '',
  dailyTime: '',
  currentPets: [],
  reason: '',
  contactAddress: ''
})

const livingTypes = ['独居', '家人同住', '合租']
const housingTypes = ['自有房产', '租房']
const experiences = ['无经验', '1-3年', '3年以上']
const dailyTimes = ['少于2小时', '2-5小时', '5小时以上']
const currentPetOptions = ['猫', '狗', '其他', '无']

const petImage = computed(() => {
  if (pet.value?.images && Array.isArray(pet.value.images) && pet.value.images.length > 0) {
    return pet.value.images[0]
  }
  return 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=100'
})

const isFormValid = computed(() => {
  return (
    form.livingType &&
    form.housingType &&
    form.hasYard !== null &&
    form.experience &&
    form.dailyTime &&
    form.reason.length >= 50 &&
    form.reason.length <= 500 &&
    form.contactAddress.length >= 5
  )
})

const toggleCurrentPet = (petType) => {
  const index = form.currentPets.indexOf(petType)
  if (index > -1) {
    form.currentPets.splice(index, 1)
  } else {
    if (petType === '无') {
      form.currentPets = ['无']
    } else {
      const noIndex = form.currentPets.indexOf('无')
      if (noIndex > -1) {
        form.currentPets.splice(noIndex, 1)
      }
      form.currentPets.push(petType)
    }
  }
}

const loadPet = async () => {
  loading.value = true
  try {
    const res = await petApi.getDetail(petId)
    if (res.code === 200) {
      pet.value = res.data
    }
  } catch (e) {
    console.error('Failed to load pet:', e)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  showConfirmModal.value = false
  submitting.value = true
  errorMessage.value = ''

  try {
    const res = await adoptionApi.apply({
      petId: parseInt(petId),
      livingType: form.livingType,
      housingType: form.housingType,
      hasYard: form.hasYard ? 1 : 0,
      experience: form.experience,
      dailyTime: form.dailyTime,
      currentPets: form.currentPets,
      reason: form.reason,
      contactAddress: form.contactAddress
    })

    if (res.code === 200) {
      toast.success('申请提交成功！')
      router.push('/user/applications')
    } else {
      errorMessage.value = res.message || '提交失败'
    }
  } catch (e) {
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPet()
})
</script>
