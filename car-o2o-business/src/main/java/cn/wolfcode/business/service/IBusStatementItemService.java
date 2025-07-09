package cn.wolfcode.business.service;

import java.util.List;
import cn.wolfcode.business.domain.BusStatementItem;
import cn.wolfcode.business.vo.StatementItemVO;

/**
 * 结算单明细Service接口
 *
 * @author wolfcode
 * @date 2025-07-06
 */
public interface IBusStatementItemService
{
    /**
     * 查询结算单明细
     *
     * @param id 结算单明细主键
     * @return 结算单明细
     */
    public BusStatementItem selectBusStatementItemById(Long id);

    /**
     * 查询结算单明细列表
     *
     * @param busStatementItem 结算单明细
     * @return 结算单明细集合
     */
    public List<BusStatementItem> selectBusStatementItemList(BusStatementItem busStatementItem);

    /**
     * 新增结算单明细
     *
     * @param busStatementItem 结算单明细
     * @return 结果
     */
    public int insertBusStatementItem(BusStatementItem busStatementItem);

    /**
     * 修改结算单明细
     *
     * @param busStatementItem 结算单明细
     * @return 结果
     */
    public int updateBusStatementItem(BusStatementItem busStatementItem);

    /**
     * 批量删除结算单明细
     *
     * @param ids 需要删除的结算单明细主键集合
     * @return 结果
     */
    public int deleteBusStatementItemByIds(Long[] ids);

    /**
     * 删除结算单明细信息
     *
     * @param id 结算单明细主键
     * @return 结果
     */
    public int deleteBusStatementItemById(Long id);

    /**
     * 批量插入结算单明细并修改结算单
     * @param vo
     * @return
     */
    int itemSave(StatementItemVO vo);

    /**
     * 根据结算单id查询结算单明细列表
     * @param statementId
     * @return
     */
    List<BusStatementItem> getStatementItemListByStatementId(Long statementId);

    /**
     * 支付功能
     * @param statementId
     * @return
     */
    int pay(Long statementId);
}
