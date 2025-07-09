package cn.wolfcode.business.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import cn.wolfcode.business.domain.BusStatement;

/**
 * 结算单Service接口
 *
 * @author wolfcode
 * @date 2025-07-04
 */
public interface IBusStatementService
{
    /**
     * 查询结算单
     *
     * @param id 结算单主键
     * @return 结算单
     */
    public BusStatement selectBusStatementById(Long id);

    /**
     * 查询结算单列表
     *
     * @param busStatement 结算单
     * @return 结算单集合
     */
    public List<BusStatement> selectBusStatementList(BusStatement busStatement) throws ParseException;

    /**
     * 新增结算单
     *
     * @param busStatement 结算单
     * @return 结果
     */
    public int insertBusStatement(BusStatement busStatement);

    /**
     * 修改结算单
     *
     * @param busStatement 结算单
     * @return 结果
     */
    public int updateBusStatement(BusStatement busStatement);

    /**
     * 批量删除结算单
     *
     * @param ids 需要删除的结算单主键集合
     * @return 结果
     */
    public int deleteBusStatementByIds(Long[] ids);

    /**
     * 删除结算单信息
     *
     * @param id 结算单主键
     * @return 结果
     */
    public int deleteBusStatementById(Long id);

    /**
     *根据结算单id更新结算单总价格,总数量,折扣价
     */
    int updateAmount(Long statementId, BigDecimal totalAmount, BigDecimal totalQuantity, BigDecimal discountAmount);

    /**
     * 根据id 更新 收款人,收款时间,状态
     */
    int updatePayStatusAndPayTimeAndPayId(Long statementId, Long payId, Date date, Integer statusPaid);

    /**
     * 根据预约单ID 查询结算单
     */
    BusStatement queryStatementByAppointmentId(Long appointmentId);
}
