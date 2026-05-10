import request from './index'

export const adoptionApi = {
  // 提交领养申请
  apply(data) {
    return request.post('/adoption/apply', data)
  },

  // 获取我的申请列表
  getList(params) {
    return request.get('/adoption/list', { params })
  },

  // 获取申请详情
  getDetail(id) {
    return request.get(`/adoption/${id}`)
  },

  // 取消申请
  cancel(id) {
    return request.put(`/adoption/${id}/cancel`)
  }
}
