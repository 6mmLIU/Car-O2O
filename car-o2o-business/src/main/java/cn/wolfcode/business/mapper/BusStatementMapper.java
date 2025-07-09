package cn.wolfcode.business.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import cn.wolfcode.business.domain.BusStatement;
import org.apache.ibatis.annotations.Param;

/**
 * 结算单Mapper接口
 *
 * @author wolfcode
 * @date 2025-07-04
 */
public interface BusStatementMapper
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
    public List<BusStatement> selectBusStatementList(BusStatement busStatement);

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
     * 删除结算单
     *
     * @param id 结算单主键
     * @return 结果
     */
    public int deleteBusStatementById(Long id);

    /**
     * 批量删除结算单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusStatementByIds(Long[] ids);

    /**
     * 根据结算单id更新结算单总价格,总数量,折扣价
     */

    int updateAmount(@Param("statementId") Long statementId, @Param("totalAmount") BigDecimal totalAmount, @Param("totalQuantity") BigDecimal totalQuantity, @Param("discountAmount") BigDecimal discountAmount);

    /**
     * 根据id 更新 收款人 收款时间 状态
     */
    int updatePayStatusAndPayTimeAndPayId(@Param("statementId") Long statementId, @Param("payId") Long payId, @Param("payTime") Date date, @Param("status") Integer statusPaid);

    /**
     * 根据预约单id查询结算单
     */

    BusStatement queryStatementByAppointmentId(Long appointmentId);
}
