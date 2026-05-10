import request from './index'

export const petApi = {
  // 获取宠物列表
  getList(params) {
    return request.get('/pet/list', { params })
  },

  // 获取宠物详情
  getDetail(id) {
    return request.get(`/pet/${id}`)
  },

  // 获取推荐宠物
  getRecommended(limit = 6) {
    return request.get('/pet/recommended', { params: { limit } })
  }
}
