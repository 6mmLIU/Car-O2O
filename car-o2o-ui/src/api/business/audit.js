import request from '@/utils/request'

// 查询套餐审核列表
export function listAudit(query) {
  return request({
    url: '/business/audit/list',
    method: 'get',
    params: query
  })
}

// 查询套餐审核详细
export function getAudit(id) {
  return request({
    url: '/business/audit/' + id,
    method: 'get'
  })
}

// 新增套餐审核
export function addAudit(data) {
  return request({
    url: '/business/audit',
    method: 'post',
    data: data
  })
}

// 修改套餐审核
export function updateAudit(data) {
  return request({
    url: '/business/audit',
    method: 'put',
    data: data
  })
}

// 查看流程图 getProcessImg
export function getProcessImg(instanceId) {
  return request({
    url: '/business/audit/getProcessImg?instanceId=' + instanceId,
    method: 'get',
  })
}

// 撤销套餐审核  processCancel
export function processCancel(id) {
  return request({
    url: '/business/audit/' + id,
    method: 'delete',
    headers:{"cmd":"processCancel"}
  })
}

// 查询审批历史列表集合数据  getHistoryTaskList
export function getHistoryTaskList(instanceId) {
  return request({
    url: '/business/audit/historyTask?instanceId=' + instanceId,
    method: 'get',
  })
}

// 查询待办任务列表 listTodoTask
export function listTodoTask(query) {
  return request({
    url: '/business/audit/todoTaskList',
    method: 'get',
    params:query,
  })
}

// 审批功能实现
export function audit(data) {
  return request({
    url: '/business/audit',
    method: 'patch',
    data: data,
    headers:{"cmd":"audit"}
  })
}

// 我的已办列表 listDoneTask
export function listDoneTask(query) {
  return request({
    url: '/business/audit/doneTaskList',
    method: 'get',
    params:query,
  })
}