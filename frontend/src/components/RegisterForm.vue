<template>
  <div class="space-y-4">
    <div>
      <label class="block font-black text-sm mb-1 ml-2">用户名 *</label>
      <input
        v-model="form.username"
        type="text"
        placeholder="4-20位字母数字"
        class="input-playful"
        :class="{ 'border-red-500': errors.username }"
        @blur="checkUsername"
      />
      <p v-if="errors.username" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.username }}</p>
    </div>

    <div>
      <label class="block font-black text-sm mb-1 ml-2">手机号 *</label>
      <input
        v-model="form.phone"
        type="text"
        placeholder="11位有效手机号"
        class="input-playful"
        :class="{ 'border-red-500': errors.phone }"
        @blur="checkPhone"
      />
      <p v-if="errors.phone" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.phone }}</p>
    </div>

    <div class="grid grid-cols-2 gap-3">
      <div>
        <label class="block font-black text-sm mb-1 ml-2">密码 *</label>
        <input
          v-model="form.password"
          type="password"
          placeholder="6-20位"
          class="input-playful"
          :class="{ 'border-red-500': errors.password }"
          @input="validatePasswordField"
        />
        <p v-if="errors.password" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.password }}</p>
      </div>
      <div>
        <label class="block font-black text-sm mb-1 ml-2">确认密码 *</label>
        <input
          v-model="form.confirmPassword"
          type="password"
          placeholder="一致"
          class="input-playful"
          :class="{ 'border-red-500': errors.confirmPassword }"
          @input="validateConfirmPasswordField"
        />
        <p v-if="errors.confirmPassword" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.confirmPassword }}</p>
      </div>
    </div>

    <div>
      <label class="block font-black text-sm mb-1 ml-2">真实姓名 *</label>
      <input
        v-model="form.realName"
        type="text"
        placeholder="2-10位中文字符"
        class="input-playful"
        :class="{ 'border-red-500': errors.realName }"
        @input="validateRealNameField"
      />
      <p v-if="errors.realName" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.realName }}</p>
    </div>

    <div>
      <label class="block font-black text-sm mb-1 ml-2">邮箱</label>
      <input
        v-model="form.email"
        type="email"
        placeholder="有效邮箱格式"
        class="input-playful"
        :class="{ 'border-red-500': errors.email }"
        @input="validateEmailField"
      />
      <p v-if="errors.email" class="text-red-500 text-xs font-bold mt-1 ml-2">{{ errors.email }}</p>
    </div>

    <div class="flex items-start gap-2 px-2 py-2">
      <input v-model="form.agree" type="checkbox" class="mt-1 w-5 h-5 border-2 border-black" />
      <p class="text-[10px] font-bold opacity-60">
        我已阅读并同意 <span class="text-blue-500 cursor-pointer">《用户协议》</span> 与 <span class="text-blue-500 cursor-pointer">《隐私政策》</span>
      </p>
    </div>

    <p v-if="errorMessage" class="text-red-500 font-bold text-sm text-center">{{ errorMessage }}</p>

    <button
      @click="handleSubmit"
      :disabled="loading || !form.agree"
      class="btn-main btn-pink"
    >
      <span v-if="loading"><i class="fas fa-spinner animate-spin mr-2"></i>注册中...</span>
      <span v-else>确认注册</span>
    </button>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/user'
import {
  validateUsername,
  validatePassword,
  validateConfirmPassword,
  validatePhone,
  validateEmail,
  validateRealName
} from '@/utils/validate'

const router = useRouter()

const form = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: '',
  agree: false
})

const errors = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: ''
})

const loading = ref(false)
const errorMessage = ref('')

const checkUsername = async () => {
  const result = validateUsername(form.username)
  errors.username = result.message
  if (result.valid && form.username) {
    try {
      const res = await userApi.checkUsername(form.username)
      if (res.code === 200 && res.data) {
        errors.username = '用户名已存在'
      }
    } catch (e) {
      // ignore
    }
  }
}

const checkPhone = async () => {
  const result = validatePhone(form.phone)
  errors.phone = result.message
  if (result.valid && form.phone) {
    try {
      const res = await userApi.checkPhone(form.phone)
      if (res.code === 200 && res.data) {
        errors.phone = '手机号已注册'
      }
    } catch (e) {
      // ignore
    }
  }
}

const validatePasswordField = () => {
  const result = validatePassword(form.password)
  errors.password = result.message
  if (form.confirmPassword) {
    validateConfirmPasswordField()
  }
}

const validateConfirmPasswordField = () => {
  const result = validateConfirmPassword(form.password, form.confirmPassword)
  errors.confirmPassword = result.message
}

const validateRealNameField = () => {
  const result = validateRealName(form.realName)
  errors.realName = result.message
}

const validateEmailField = () => {
  const result = validateEmail(form.email)
  errors.email = result.message
}

const validateAll = () => {
  checkUsername()
  checkPhone()
  validatePasswordField()
  validateConfirmPasswordField()
  validateRealNameField()
  validateEmailField()

  return !Object.values(errors).some(e => e)
}

const handleSubmit = async () => {
  if (!validateAll()) {
    return
  }

  if (!form.agree) {
    errorMessage.value = '请阅读并同意用户协议'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const res = await userApi.register({
      username: form.username,
      phone: form.phone,
      password: form.password,
      realName: form.realName,
      email: form.email || undefined
    })

    if (res.code === 200) {
      router.push('/login')
    } else {
      errorMessage.value = res.message || '注册失败'
    }
  } catch (error) {
    errorMessage.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>
