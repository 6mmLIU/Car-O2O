import request from '@/utils/request'

// 查询结算单列表
export function itemSave(data) {
  return request({
    url: '/business/item',
    method: 'post',
   data:data
  })
}
//根据结算单id 查询结算单明细列表
export function getStatementItemListByStatementId(id) {
  return request({
    url: '/business/item/'+id,
    method: 'get',
  })

}


//支付功能
export function pay(statementId) {
  return request({
    url: '/business/item/'+statementId,
    method: 'patch',
    headers: {'cmd':'pay'}
  })

}
