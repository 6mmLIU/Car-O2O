package cn.wolfcode.business.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.wolfcode.business.domain.BusAppointment;
import cn.wolfcode.business.domain.BusStatement;
import cn.wolfcode.business.service.IBusAppointmentService;
import cn.wolfcode.business.service.IBusStatementService;
import cn.wolfcode.business.vo.StatementItemVO;
import cn.wolfcode.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.business.mapper.BusStatementItemMapper;
import cn.wolfcode.business.domain.BusStatementItem;
import cn.wolfcode.business.service.IBusStatementItemService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 结算单明细Service业务层处理
 *
 * @author wolfcode
 * @date 2025-07-06
 */
@Service
public class BusStatementItemServiceImpl implements IBusStatementItemService {
    @Autowired
    private BusStatementItemMapper busStatementItemMapper;
    @Autowired
    private IBusStatementService statementService;
    @Autowired
    private IBusAppointmentService appointmentService;

    /**
     * 查询结算单明细
     *
     * @param id 结算单明细主键
     * @return 结算单明细
     */
    @Override
    public BusStatementItem selectBusStatementItemById(Long id) {
        return busStatementItemMapper.selectBusStatementItemById(id);
    }

    /**
     * 查询结算单明细列表
     *
     * @param busStatementItem 结算单明细
     * @return 结算单明细
     */
    @Override
    public List<BusStatementItem> selectBusStatementItemList(BusStatementItem busStatementItem) {
        return busStatementItemMapper.selectBusStatementItemList(busStatementItem);
    }

    /**
     * 新增结算单明细
     *
     * @param busStatementItem 结算单明细
     * @return 结果
     */
    @Override
    public int insertBusStatementItem(BusStatementItem busStatementItem) {
        return busStatementItemMapper.insertBusStatementItem(busStatementItem);
    }

    /**
     * 修改结算单明细
     *
     * @param busStatementItem 结算单明细
     * @return 结果
     */
    @Override
    public int updateBusStatementItem(BusStatementItem busStatementItem) {
        return busStatementItemMapper.updateBusStatementItem(busStatementItem);
    }

    /**
     * 批量删除结算单明细
     *
     * @param ids 需要删除的结算单明细主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementItemByIds(Long[] ids) {
        return busStatementItemMapper.deleteBusStatementItemByIds(ids);
    }

    /**
     * 批量插入结算单明细 并修改结算单
     *
     * @param vo
     * @return
     */
    @Override
    @Transactional
    public int itemSave(StatementItemVO vo) {

        // 1. 参数合理化验证。
        if (vo == null) {
            throw new RuntimeException("非法参数");
        }
        if (vo.getDiscountAmount() == null || vo.getStatementItemList() == null || vo.getStatementItemList().size() == 0) {
            throw new RuntimeException("非法参数");
        }
// 2. 从传递参数的结算单明细列表集合中获取第一条数据，获取其结算单 id。
        Long statementId = vo.getStatementItemList().get(0).getStatementId();
        BusStatement busStatement = statementService.selectBusStatementById(statementId);
        if (busStatement == null) {
            throw new RuntimeException("非法参数");
        }
// 3. 判断结算单状态，必须是“消费中”才可以进行编辑操作。
        if (BusStatement.STATUS_PAID.equals(busStatement.getStatus())) {
            throw new RuntimeException("已支付的结算单不能进行编辑");
        }
// 4. 遍历结算单明细集合，拿到每一条结算单明细对象。
        //定义总价格,总数量
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal totalQuantity = new BigDecimal(0);
        for (BusStatementItem item : vo.getStatementItemList()) {
            // 5. 在遍历过程中计算出总价格和总数量。
            totalAmount = totalAmount.add(item.getItemPrice().multiply(new BigDecimal(item.getItemQuantity())));
            //总数量
            totalQuantity = totalQuantity.add(new BigDecimal(item.getItemQuantity()));
        }

// 6. 遍历结束后对金额进行验证：折扣价 >= 0 && 总价格 >= 0 && 折扣价 <= 总价格。
        if (vo.getDiscountAmount().compareTo(new BigDecimal(0)) < 0 || totalAmount.compareTo(new BigDecimal(0)) < 0) {
            throw new RuntimeException("折扣价或总价必须都大于0");
        }
        if (vo.getDiscountAmount().compareTo(totalAmount) > 0) {
            throw new RuntimeException("折扣价应小于或等于总价格");
        }
        //先将原始的关联数据删除(结算单明细数据,因为可能被改动过)
        busStatementItemMapper.deleteBytatementId(statementId);
// 7. 批量插入结算单明细集合。
        busStatementItemMapper.insertBatch(vo.getStatementItemList());
// 8. 更新结算单表数据。
        return statementService.updateAmount(statementId, totalAmount, totalQuantity, vo.getDiscountAmount());
// 9. 事务管理。

    }

    /**
     * 删除结算单明细信息
     *
     * @param id 结算单明细主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementItemById(Long id) {
        return busStatementItemMapper.deleteBusStatementItemById(id);
    }

    /**
     * 根据结算单id查询结果明细列表
     *
     * @param statementId
     * @return
     */
    @Override
    public List<BusStatementItem> getStatementItemListByStatementId(Long statementId) {
        if (statementId == null) {
            throw new RuntimeException("非法参数");
        }
        BusStatementItem busStatementItem = new BusStatementItem();
        busStatementItem.setStatementId(statementId);
        return busStatementItemMapper.selectBusStatementItemList(busStatementItem);
    }

    @Override
    @Transactional
    public int pay(Long statementId) {
        // 1. 参数合理化验证。
        if (statementId == null) {
            throw new RuntimeException("非法参数");
        }
// 2. 根据 statementId 查询出 statement 对象（结算单对象）。
        BusStatement busStatement = statementService.selectBusStatementById(statementId);
        if (busStatement == null) {
            throw new RuntimeException("非法参数");
        }
// 3. 判断结算单状态，必须是消费中才可以进行支付。
        if (BusStatement.STATUS_PAID.equals(busStatement.getStatus())) {
            throw new RuntimeException("该结算单已经支付");
        }
// 4. 更新结算单状态为已支付。更新收款人和收款时间。
        //获取收款人id
        Long payId = SecurityUtils.getUserId();


// 5. 若该结算关联预约单，我们还需要修改预约单状态为已支付。
        if (busStatement.getAppointmentId() != null) {
            //说明是预约用户
            appointmentService.updateStatusById(busStatement.getAppointmentId(), BusAppointment.STATUS_PAID);
        }
        return statementService.updatePayStatusAndPayTimeAndPayId(statementId, payId, new Date(), BusStatement.STATUS_PAID);

    }


}


