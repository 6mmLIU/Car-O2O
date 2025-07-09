package cn.wolfcode.business.mapper;

import cn.wolfcode.business.domain.BusServiceItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务项 mapper
 */
public interface BusServiceItemMapper {
    /**
     * 根据ID 查询服务项对象
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
     * 根据服务项id 修改服务项上下架状态
     * @param id
     * @param salestatusOn
     * @return
     */
    int changeSaleStaus(@Param("id") long id, @Param("saleStatus") Integer salestatusOn);

    void changeAuditStatus(@Param("id") Long id, @Param("auditStatus") Integer auditstatus);

}
