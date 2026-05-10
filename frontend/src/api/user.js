import request from './index'

export const userApi = {
  // 用户注册
  register(data) {
    return request.post('/user/register', data)
  },

  // 用户登录
  login(data) {
    return request.post('/user/login', data)
  },

  // 退出登录
  logout() {
    return request.post('/user/logout')
  },

  // 获取用户信息
  getUserInfo() {
    return request.get('/user/info')
  },

  // 更新用户信息
  updateUserInfo(data) {
    return request.put('/user/info', data)
  },

  // 修改密码
  updatePassword(data) {
    return request.put('/user/password', data)
  },

  // 检查用户名是否存在
  checkUsername(username) {
    return request.get('/user/check/username', { params: { username } })
  },

  // 检查手机号是否存在
  checkPhone(phone) {
    return request.get('/user/check/phone', { params: { phone } })
  }
}
