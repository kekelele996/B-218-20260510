import request from './index'

export const breedApi = {
  // 获取品种列表
  getList(petType) {
    return request.get('/breed/list', { params: { petType } })
  },

  // 获取所有品种
  getAll() {
    return request.get('/breed/list')
  }
}
