import request from '@/utils/request'

// 查询流程定义明细列表
export function listBpmninfo(query) {
  return request({
    url: '/business/bpmninfo/list',
    method: 'get',
    params: query
  })
}

// 查询流程定义明细详细
export function getBpmninfo(id) {
  return request({
    url: '/business/bpmninfo/' + id,
    method: 'get'
  })
}



// 查询流程定义xml,png文件
export function getResourceByType(id,type) {
  return request({
    url: '/business/bpmninfo/showResource?id='+id+"&type="+type,
    method: 'get'
  })
}

// 新增流程定义明细
export function addBpmninfo(data) {
  return request({
    url: '/business/bpmninfo',
    method: 'post',
    data: data
  })
}

// 修改流程定义明细
export function updateBpmninfo(data) {
  return request({
    url: '/business/bpmninfo',
    method: 'put',
    data: data
  })
}

// 删除流程定义明细
export function delBpmninfo(id) {
  return request({
    url: '/business/bpmninfo/' + id,
    method: 'delete'
  })
}

// 撤销流程定义明细
export function cancelRequest(id) {
  return request({
    url: '/business/bpmninfo/cancel?id='+id,
    method: 'delete'
  })
}
