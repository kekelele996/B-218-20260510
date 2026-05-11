<template>
  <div class="px-6 lg:max-w-3xl lg:mx-auto lg:px-8 pb-8">
    <!-- 头部 -->
    <header class="flex items-center gap-4 mt-6 mb-8">
      <router-link
        to="/user"
        class="w-10 h-10 bg-white border-2 border-black rounded-xl flex items-center justify-center shadow-[2px_2px_0px_black]"
      >
        <i class="fas fa-chevron-left"></i>
      </router-link>
      <h1 class="text-2xl font-black">个人资料</h1>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="w-16 h-16 bg-white border-4 border-black rounded-2xl flex items-center justify-center shadow-playful animate-pulse">
        <i class="fas fa-spinner text-3xl text-pink-500 animate-spin"></i>
      </div>
    </div>

    <template v-else>
      <div class="space-y-6">
        <!-- 头像 -->
        <div class="flex justify-center mb-8">
          <div class="relative">
            <div class="w-24 h-24 bg-white border-4 border-black rounded-[2.5rem] overflow-hidden shadow-[6px_6px_0px_#FF85A2]">
              <img
                v-if="form.avatar"
                :src="form.avatar"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full bg-pink-100 flex items-center justify-center text-4xl">
                🐾
              </div>
            </div>
            <label class="absolute -bottom-1 -right-1 w-8 h-8 bg-yellow-400 border-2 border-black rounded-full flex items-center justify-center shadow-[2px_2px_0px_black] cursor-pointer">
              <i class="fas fa-camera text-xs"></i>
              <input type="file" class="hidden" accept="image/*" @change="handleAvatarChange" />
            </label>
          </div>
        </div>

        <!-- 表单 -->
        <div>
          <label class="block font-black text-sm mb-1 ml-2">用户名</label>
          <input
            :value="userInfo?.username"
            type="text"
            class="input-playful bg-gray-100"
            disabled
          />
          <p class="text-xs font-bold opacity-40 mt-1 ml-2">用户名不可修改</p>
        </div>

        <div>
          <label class="block font-black text-sm mb-1 ml-2">真实姓名</label>
          <input
            :value="userInfo?.realName"
            type="text"
            class="input-playful bg-gray-100"
            disabled
          />
        </div>

        <div>
          <label class="block font-black text-sm mb-1 ml-2">手机号</label>
          <input
            v-model="form.phone"
            type="text"
            placeholder="11位有效手机号"
            class="input-playful"
            :class="{ 'border-red-500': errors.phone }"
          />
          <p v-if="errors.phone" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.phone }}</p>
        </div>

        <div>
          <label class="block font-black text-sm mb-1 ml-2">邮箱</label>
          <input
            v-model="form.email"
            type="email"
            placeholder="有效邮箱格式"
            class="input-playful"
            :class="{ 'border-red-500': errors.email }"
          />
          <p v-if="errors.email" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.email }}</p>
        </div>

        <div>
          <label class="block font-black text-sm mb-1 ml-2">地址</label>
          <input
            v-model="form.address"
            type="text"
            placeholder="请输入您的地址"
            class="input-playful"
          />
        </div>

        <p v-if="errorMessage" class="text-red-500 font-bold text-sm text-center">{{ errorMessage }}</p>
        <p v-if="successMessage" class="text-green-500 font-bold text-sm text-center">{{ successMessage }}</p>

        <button
          @click="handleSubmit"
          :disabled="submitting"
          class="btn-main"
        >
          <span v-if="submitting"><i class="fas fa-spinner animate-spin mr-2"></i>保存中...</span>
          <span v-else>保存修改</span>
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { validatePhone, validateEmail } from '@/utils/validate'

const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const loading = ref(true)
const submitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const form = reactive({
  phone: '',
  email: '',
  address: '',
  avatar: ''
})

const errors = reactive({
  phone: '',
  email: ''
})

const initForm = () => {
  if (userInfo.value) {
    form.phone = userInfo.value.phone || ''
    form.email = userInfo.value.email || ''
    form.address = userInfo.value.address || ''
    form.avatar = userInfo.value.avatar || ''
  }
}

const handleAvatarChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      form.avatar = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

const validate = () => {
  let valid = true

  const phoneResult = validatePhone(form.phone)
  errors.phone = phoneResult.message
  if (!phoneResult.valid) valid = false

  const emailResult = validateEmail(form.email)
  errors.email = emailResult.message
  if (!emailResult.valid) valid = false

  return valid
}

const handleSubmit = async () => {
  if (!validate()) return

  submitting.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const res = await userStore.updateUserInfo({
      phone: form.phone,
      email: form.email || undefined,
      address: form.address || undefined,
      avatar: form.avatar || undefined
    })

    if (res.code === 200) {
      successMessage.value = '保存成功'
    } else {
      errorMessage.value = res.message || '保存失败'
    }
  } catch (e) {
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  initForm()
  loading.value = false
})
</script>
