package cn.wolfcode.business.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


import cn.wolfcode.business.domain.BusAppointment;
import cn.wolfcode.business.mapper.BusAppointmentMapper;
import cn.wolfcode.business.utils.DateUtils;
import cn.wolfcode.business.utils.RegexUtils;
import cn.wolfcode.business.utils.VehiclePlateNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.business.mapper.BusStatementMapper;
import cn.wolfcode.business.domain.BusStatement;
import cn.wolfcode.business.service.IBusStatementService;

/**
 * 结算单Service业务层处理
 *
 * @author wolfcode
 * @date 2025-07-04
 */
@Service
public class BusStatementServiceImpl implements IBusStatementService {
    @Autowired
    private BusStatementMapper busStatementMapper;

    /**
     * 查询结算单
     *
     * @param id 结算单主键
     * @return 结算单
     */
    @Override
    public BusStatement selectBusStatementById(Long id) {
        return busStatementMapper.selectBusStatementById(id);
    }

    /**
     * 查询结算单列表
     *
     * @param busStatement 结算单
     * @return 结算单
     */
    @Override
    public List<BusStatement> selectBusStatementList(BusStatement busStatement) throws ParseException {
        String setDate = (String) busStatement.getParams().get("endTime");
        Date date = DateUtils.parseDate(setDate, "yyyy-MM-dd HH:mm:ss");
        Date endDateTime = DateUtils.getEndTime(date);
        String endTime = DateUtils.formatDate(endDateTime, "yyyy-MM-dd HH:mm:ss");
        busStatement.getParams().put("endTime", endTime);
        return busStatementMapper.selectBusStatementList(busStatement);
    }

    /**
     * 新增结算单
     *
     * @param busStatement 结算单
     * @return 结果
     */
    @Override
    public int insertBusStatement(BusStatement busStatement) {
        if (busStatement == null) {
            throw new RuntimeException("非法参数");
        }
        if (busStatement.getCustomerName() == null ||
                busStatement.getCustomerPhone() == null ||
                busStatement.getActualArrivalTime() == null ||
                busStatement.getLicensePlate() == null ||
                busStatement.getCarSeries() == null || busStatement.getServiceType() == null) {
            throw new RuntimeException("非法参数");
        }
        if (RegexUtils.isChinaPhoneLegal(busStatement.getCustomerPhone())) {
            throw new RuntimeException("手机号码格式不正确");
        }
        VehiclePlateNoUtil.VehiclePlateNoEnum vehiclePlateNo = VehiclePlateNoUtil.getVehiclePlateNo(busStatement.getLicensePlate());
        if (vehiclePlateNo == null) {
            throw new RuntimeException("车牌号码格式不正确");
        }
        Date date = new Date();
        if (!busStatement.getActualArrivalTime().before(date)) {
            throw new RuntimeException("到店时间异常");
        }
        busStatement.setCreateTime(new Date());
        //为了安全性考虑,那我们需要将新插入的数据中的状态再次设置消费中
        busStatement.setStatus(BusStatement.STATUS_CONSUME);
        return busStatementMapper.insertBusStatement(busStatement);
    }

    /**
     * 修改结算单
     *
     * @param busStatement 结算单
     * @return 结果
     */
    @Override
    public int updateBusStatement(BusStatement busStatement) {
        if (busStatement == null) {
            throw new RuntimeException("非法参数");
        }
        if (busStatement.getCustomerName() == null ||
                busStatement.getCustomerPhone() == null ||
                busStatement.getActualArrivalTime() == null ||
                busStatement.getLicensePlate() == null ||
                busStatement.getCarSeries() == null || busStatement.getServiceType() == null) {
            throw new RuntimeException("非法参数");
        }
        if (RegexUtils.isChinaPhoneLegal(busStatement.getCustomerPhone())) {
            throw new RuntimeException("手机号码格式不正确");
        }
        VehiclePlateNoUtil.VehiclePlateNoEnum vehiclePlateNo = VehiclePlateNoUtil.getVehiclePlateNo(busStatement.getLicensePlate());
        if (vehiclePlateNo == null) {
            throw new RuntimeException("车牌号码格式不正确");
        }
        Date date = new Date();
        if (!busStatement.getActualArrivalTime().before(date)) {
            throw new RuntimeException("到店时间异常");
        }
        //6.状态必须属于消费中才可以进行编辑操作
        BusStatement oldObj = this.selectBusStatementById(busStatement.getId());
        if (!BusStatement.STATUS_CONSUME.equals(oldObj.getStatus())) {
            throw new RuntimeException("必须为消费中的状态才可以进行编辑操作");
        }
        //安全设置
        busStatement.setStatus(oldObj.getStatus());
        return busStatementMapper.updateBusStatement(busStatement);
    }

    /**
     * 批量删除结算单
     *
     * @param ids 需要删除的结算单主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementByIds(Long[] ids) {
        if (ids == null || ids.length <= 0) {
            throw new RuntimeException("非法参数");
        }
        //因为当前方法不存在批量
        Long id = ids[0];
        BusStatement oldObj = this.selectBusStatementById(id);
        if (oldObj == null) {
            throw new RuntimeException("非法参数");
        }
        if (!BusStatement.STATUS_CONSUME.equals(oldObj.getStatus())) {
            throw new RuntimeException("只有状态为消费中的记录才可以被删除");
        }
        return busStatementMapper.deleteBusStatementByIds(ids);
    }


    /**
     * 删除结算单信息
     *
     * @param id 结算单主键
     * @return 结果
     */
    @Override
    public int deleteBusStatementById(Long id) {
        return busStatementMapper.deleteBusStatementById(id);
    }

    /**
     * 根据结算单id更新结算单总价格,总数量,折扣价
     */
    @Override
    public int updateAmount(Long statementId, BigDecimal totalAmount, BigDecimal totalQuantity, BigDecimal discountAmount) {
        return busStatementMapper.updateAmount(statementId, totalAmount, totalQuantity, discountAmount);
    }

    /**
     * 根据id 更新 收款人,收款时间,状态
     */
    @Override
    public int updatePayStatusAndPayTimeAndPayId(Long statementId, Long payId, Date date, Integer statusPaid) {
       return busStatementMapper.updatePayStatusAndPayTimeAndPayId(statementId, payId, date, statusPaid);
    }

    /**
     * 根据预约单ID 查询结算单
     */
    @Override
    public BusStatement queryStatementByAppointmentId(Long appointmentId) {
        return busStatementMapper.queryStatementByAppointmentId(appointmentId);
    }
}

