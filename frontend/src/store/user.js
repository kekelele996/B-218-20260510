import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref('')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken) => {
    token.value = newToken
  }

  const setUserInfo = (info) => {
    userInfo.value = info
  }

  const login = async (loginData) => {
    const res = await userApi.login(loginData)
    if (res.code === 200) {
      token.value = res.data.token
      userInfo.value = res.data.user
    }
    return res
  }

  const logout = async () => {
    try {
      await userApi.logout()
    } finally {
      token.value = ''
      userInfo.value = null
    }
  }

  const fetchUserInfo = async () => {
    if (!token.value) return
    const res = await userApi.getUserInfo()
    if (res.code === 200) {
      userInfo.value = res.data
    }
    return res
  }

  const updateUserInfo = async (data) => {
    const res = await userApi.updateUserInfo(data)
    if (res.code === 200) {
      userInfo.value = { ...userInfo.value, ...data }
    }
    return res
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    login,
    logout,
    fetchUserInfo,
    updateUserInfo
  }
}, {
  persist: {
    paths: ['token', 'userInfo']
  }
})
