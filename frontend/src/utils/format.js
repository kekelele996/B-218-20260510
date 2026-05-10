// 日期格式化
export const formatDate = (date, format = 'YYYY-MM-DD') => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
}

// 年龄格式化 (月数转换为年龄描述)
export const formatAge = (months) => {
  if (!months && months !== 0) return '未知'
  if (months < 12) return `${months}个月`
  const years = Math.floor(months / 12)
  const remainMonths = months % 12
  if (remainMonths === 0) return `${years}岁`
  return `${years}岁${remainMonths}个月`
}

// 状态格式化
export const formatStatus = (status) => {
  const statusMap = {
    0: { text: '待审核', color: 'blue', bg: 'blue-100' },
    1: { text: '审核中', color: 'blue', bg: 'blue-100' },
    2: { text: '已通过', color: 'green', bg: 'green-100' },
    3: { text: '已拒绝', color: 'red', bg: 'red-100' },
    4: { text: '已取消', color: 'gray', bg: 'gray-100' }
  }
  return statusMap[status] || { text: '未知', color: 'gray', bg: 'gray-100' }
}

// 宠物状态格式化
export const formatPetStatus = (status) => {
  const statusMap = {
    1: { text: '可领养', color: 'green' },
    2: { text: '已领养', color: 'gray' },
    0: { text: '已下架', color: 'red' }
  }
  return statusMap[status] || { text: '未知', color: 'gray' }
}

// 截断文本
export const truncateText = (text, length = 30) => {
  if (!text) return ''
  if (text.length <= length) return text
  return text.slice(0, length) + '...'
}

// 获取随机阴影颜色
export const getRandomShadowColor = () => {
  const colors = ['#FFD600', '#FF85A2', '#60A5FA', '#4ADE80', '#A78BFA']
  return colors[Math.floor(Math.random() * colors.length)]
}
