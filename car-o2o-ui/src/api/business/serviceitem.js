import request from '@/utils/request'

// 查询服务项列表
export function listServiceitem(query) {
  return request({
    url: '/business/serviceitem/list',
    method: 'get',
    params: query
  })
}

// 查询服务项详细
export function getServiceitem(id) {
  return request({
    url: '/business/serviceitem/' + id,
    method: 'get'
  })
}
//查询服务项列表(不分页)
export function getServiceitemList(query) {
  return request({
    url: '/business/serviceitem/list',
    method: 'get',
    params: query,
    headers:{'cmd':'listNotPage'}
  })
}



export function getAuditInfo(id) {
  return request({
    url: '/business/serviceitem/getAuditInfo?id='+id,
    method: 'get',
  })
}

// 新增服务项
export function addServiceitem(data) {
  return request({
    url: '/business/serviceitem',
    method: 'post',
    data: data
  })
}

// 修改服务项
export function updateServiceitem(data) {
  return request({
    url: '/business/serviceitem',
    method: 'put',
    data: data
  })
}

// 删除服务项
export function delServiceitem(id) {
  return request({
    url: '/business/serviceitem/' + id,
    method: 'delete'
  })
}

// 上架操作
export function saleOnOp(id) {
  return request({
    url: '/business/serviceitem/' + id,
    method: 'patch',
    headers:{'cmd':'saleOn'}
  })
}

// 下架操作
export function saleOffOp(id) {
  return request({
    url: '/business/serviceitem/' + id,
    method: 'patch',
    headers:{'cmd':'saleOff'}
  })
}




//
export function startAudit(data) {
  return request({
    url: '/business/serviceitem/startAudit',
    method: 'post',
    data: data
  })
}
