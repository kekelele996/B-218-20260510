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
      <h1 class="text-2xl font-black">修改密码</h1>
    </header>

    <div class="space-y-6">
      <div>
        <label class="block font-black text-sm mb-1 ml-2">原密码 *</label>
        <input
          v-model="form.oldPassword"
          type="password"
          placeholder="请输入原密码"
          class="input-playful"
          :class="{ 'border-red-500': errors.oldPassword }"
        />
        <p v-if="errors.oldPassword" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.oldPassword }}</p>
      </div>

      <div>
        <label class="block font-black text-sm mb-1 ml-2">新密码 *</label>
        <input
          v-model="form.newPassword"
          type="password"
          placeholder="6-20位，包含字母和数字"
          class="input-playful"
          :class="{ 'border-red-500': errors.newPassword }"
          @input="validateNewPasswordField"
        />
        <p v-if="errors.newPassword" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.newPassword }}</p>
      </div>

      <div>
        <label class="block font-black text-sm mb-1 ml-2">确认新密码 *</label>
        <input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          class="input-playful"
          :class="{ 'border-red-500': errors.confirmPassword }"
          @input="validateConfirmPasswordField"
        />
        <p v-if="errors.confirmPassword" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.confirmPassword }}</p>
      </div>

      <p v-if="errorMessage" class="text-red-500 font-bold text-sm text-center">{{ errorMessage }}</p>
      <p v-if="successMessage" class="text-green-500 font-bold text-sm text-center">{{ successMessage }}</p>

      <button
        @click="handleSubmit"
        :disabled="submitting"
        class="btn-main"
      >
        <span v-if="submitting"><i class="fas fa-spinner animate-spin mr-2"></i>修改中...</span>
        <span v-else>确认修改</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/user'
import { validatePassword, validateConfirmPassword } from '@/utils/validate'

const router = useRouter()

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const errors = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const submitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const validateNewPasswordField = () => {
  const result = validatePassword(form.newPassword)
  errors.newPassword = result.message
  if (form.confirmPassword) {
    validateConfirmPasswordField()
  }
}

const validateConfirmPasswordField = () => {
  const result = validateConfirmPassword(form.newPassword, form.confirmPassword)
  errors.confirmPassword = result.message
}

const validate = () => {
  let valid = true

  if (!form.oldPassword) {
    errors.oldPassword = '请输入原密码'
    valid = false
  } else {
    errors.oldPassword = ''
  }

  const newPwdResult = validatePassword(form.newPassword)
  errors.newPassword = newPwdResult.message
  if (!newPwdResult.valid) valid = false

  const confirmResult = validateConfirmPassword(form.newPassword, form.confirmPassword)
  errors.confirmPassword = confirmResult.message
  if (!confirmResult.valid) valid = false

  return valid
}

const handleSubmit = async () => {
  if (!validate()) return

  submitting.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const res = await userApi.updatePassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword,
      confirmPassword: form.confirmPassword
    })

    if (res.code === 200) {
      successMessage.value = '密码修改成功，请重新登录'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      errorMessage.value = res.message || '修改失败'
    }
  } catch (e) {
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    submitting.value = false
  }
}
</script>
