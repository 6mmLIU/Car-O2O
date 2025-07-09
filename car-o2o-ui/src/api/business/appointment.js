import request from '@/utils/request'

// 查询养修信息预约列表
export function listAppointment(query) {
  return request({
    url: '/business/appointment/list',
    method: 'get',
    params: query
  })
}

// 查询养修信息预约详细
export function getAppointment(id) {
  return request({
    url: '/business/appointment/' + id,
    method: 'get'
  })
}

// 新增养修信息预约
export function addAppointment(data) {
  return request({
    url: '/business/appointment',
    method: 'post',
    data: data
  })
}

// 修改养修信息预约
export function updateAppointment(data) {
  return request({
    url: '/business/appointment',
    method: 'put',
    data: data
  })
}

// 删除养修信息预约
export function delAppointment(id) {
  return request({
    url: '/business/appointment/' + id,
    method: 'delete'
  })
}

//到店功能
//请求方式 我们使用patch 局部更新
export function arrivedOp(id){
  return request({
    url:'/business/appointment/'+id,
    method:'patch',
    headers:{"cmd":"arrived"}
    
  })
}

//取消功能
//若方法名相同，请求参数也相同。我们可以使用请求头的方式进行区分
export function cancelOp(id){
  return request({
    url:'/business/appointment/'+id,
    method:'patch',
    headers:{"cmd":"cancel"}
  })
}
//查找或生成结算单

export function createOrSelectStatement(id){
  return request({
    url:'/business/appointment/'+id+ "/statement",
    method:'post',
  })
}
