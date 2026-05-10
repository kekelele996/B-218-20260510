<template>
  <div class="space-y-6">
    <div>
      <label class="block font-black mb-2 ml-2">账号</label>
      <input
        v-model="form.account"
        type="text"
        placeholder="用户名/手机号"
        class="input-playful"
        @input="clearError"
      />
    </div>
    <div>
      <label class="block font-black mb-2 ml-2">密码</label>
      <input
        v-model="form.password"
        type="password"
        placeholder="请输入密码"
        class="input-playful"
        @input="clearError"
        @keyup.enter="handleSubmit"
      />
    </div>

    <div class="flex justify-between items-center px-2">
      <label class="flex items-center gap-2 font-bold text-sm cursor-pointer">
        <input v-model="form.remember" type="checkbox" class="w-5 h-5 border-2 border-black rounded" />
        记住我
      </label>
      <a href="#" class="font-bold text-sm text-blue-500">忘记密码？</a>
    </div>

    <p v-if="errorMessage" class="text-red-500 font-bold text-sm text-center">{{ errorMessage }}</p>

    <button
      @click="handleSubmit"
      :disabled="loading"
      class="btn-main mt-4"
    >
      <span v-if="loading"><i class="fas fa-spinner animate-spin mr-2"></i>登录中...</span>
      <span v-else>立即登录</span>
    </button>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = reactive({
  account: '',
  password: '',
  remember: false
})

const loading = ref(false)
const errorMessage = ref('')

const clearError = () => {
  errorMessage.value = ''
}

const handleSubmit = async () => {
  if (!form.account || !form.password) {
    errorMessage.value = '请填写账号和密码'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const res = await userStore.login({
      account: form.account,
      password: form.password,
      remember: form.remember
    })

    if (res.code === 200) {
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } else {
      errorMessage.value = res.message || '登录失败，请检查账号密码'
    }
  } catch (error) {
    // 优先显示后端返回的错误信息
    if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message
    } else if (error.message) {
      errorMessage.value = error.message
    } else {
      errorMessage.value = '网络错误，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}
</script>
