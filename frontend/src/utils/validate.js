// 用户名验证: 4-20位字母数字
export const validateUsername = (username) => {
  if (!username) return { valid: false, message: '请输入用户名' }
  if (!/^[a-zA-Z0-9]{4,20}$/.test(username)) {
    return { valid: false, message: '用户名为4-20位字母数字' }
  }
  return { valid: true, message: '' }
}

// 密码验证: 6-20位，至少包含字母和数字
export const validatePassword = (password) => {
  if (!password) return { valid: false, message: '请输入密码' }
  if (password.length < 6 || password.length > 20) {
    return { valid: false, message: '密码长度为6-20位' }
  }
  if (!/[a-zA-Z]/.test(password) || !/[0-9]/.test(password)) {
    return { valid: false, message: '密码需包含字母和数字' }
  }
  return { valid: true, message: '' }
}

// 确认密码验证
export const validateConfirmPassword = (password, confirmPassword) => {
  if (!confirmPassword) return { valid: false, message: '请确认密码' }
  if (password !== confirmPassword) {
    return { valid: false, message: '两次密码不一致' }
  }
  return { valid: true, message: '' }
}

// 手机号验证
export const validatePhone = (phone) => {
  if (!phone) return { valid: false, message: '请输入手机号' }
  if (!/^1[3-9]\d{9}$/.test(phone)) {
    return { valid: false, message: '请输入有效手机号' }
  }
  return { valid: true, message: '' }
}

// 邮箱验证
export const validateEmail = (email) => {
  if (!email) return { valid: true, message: '' } // 非必填
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return { valid: false, message: '请输入有效邮箱' }
  }
  return { valid: true, message: '' }
}

// 真实姓名验证: 2-10位中文字符
export const validateRealName = (name) => {
  if (!name) return { valid: false, message: '请输入真实姓名' }
  if (!/^[\u4e00-\u9fa5]{2,10}$/.test(name)) {
    return { valid: false, message: '姓名为2-10位中文字符' }
  }
  return { valid: true, message: '' }
}

// 领养理由验证: 50-500字
export const validateReason = (reason) => {
  if (!reason) return { valid: false, message: '请输入领养理由' }
  if (reason.length < 50 || reason.length > 500) {
    return { valid: false, message: '领养理由需50-500字' }
  }
  return { valid: true, message: '' }
}

// 地址验证
export const validateAddress = (address) => {
  if (!address) return { valid: false, message: '请输入联系地址' }
  if (address.length < 5) {
    return { valid: false, message: '请输入详细地址' }
  }
  return { valid: true, message: '' }
}
