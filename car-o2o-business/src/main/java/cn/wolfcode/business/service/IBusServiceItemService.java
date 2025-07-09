package cn.wolfcode.business.service;

import cn.wolfcode.business.domain.BusServiceItem;

import java.util.List;
import java.util.Map;

/**
 * 服务项Service接口
 */
public interface IBusServiceItemService {
    BusServiceItem serviceBusServiceItemById(Long id);

    /**
     * 根据ID 查询服务项对象
     *
     * @param id
     * @return
     */
    BusServiceItem selectBusServiceItemById(Long id);

    /**
     * 查询服务项列表
     *
     * @return
     */
    List<BusServiceItem> selectBusServiceItemList(BusServiceItem busServiceItem);


    /**
     * 插入服务项信息
     *
     * @param busServiceItem
     * @return
     */

    int insertBusServiceItem(BusServiceItem busServiceItem);

    /**
     * 编辑服务项信息
     *
     * @param busServiceItem
     * @return
     */
    int updateBusServiceItem(BusServiceItem busServiceItem);

    /**
     * 删除服务项信息
     *
     * @param id
     * @return
     */
    int deleteBusServiceItemById(Long id);

    /**
     * 上架操作
     * @param id
     * @return
     */
    int saleOn(Long id);
    /**
     * 下架操作
     * @param id
     * @return
     */
    int saleOff(Long id);

    Map getAuditInfo(Long id);

    void startAudit(Long serviceItemId, Long shopOwnerId, Long financeId, String info);
}
