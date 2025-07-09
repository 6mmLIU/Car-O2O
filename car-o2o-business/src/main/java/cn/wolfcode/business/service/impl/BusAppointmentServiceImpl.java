package cn.wolfcode.business.service.impl;


import java.util.Date;
import java.util.List;

import cn.wolfcode.business.domain.BusStatement;
import cn.wolfcode.business.service.IBusStatementService;
import cn.wolfcode.business.utils.RegexUtils;
import cn.wolfcode.business.utils.VehiclePlateNoUtil;
import cn.wolfcode.common.utils.DateUtils;
import cn.wolfcode.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.business.mapper.BusAppointmentMapper;
import cn.wolfcode.business.domain.BusAppointment;
import cn.wolfcode.business.service.IBusAppointmentService;

/**
 * 养修信息预约Service业务层处理
 *
 * @author wolfcode
 * @date 2025-04-07
 */
@Service
public class BusAppointmentServiceImpl implements IBusAppointmentService {
    @Autowired
    private BusAppointmentMapper busAppointmentMapper;
    @Autowired
    private IBusStatementService statementService;

    /**
     * 查询养修信息预约
     *
     * @param id 养修信息预约主键
     * @return 养修信息预约
     */
    @Override
    public BusAppointment selectBusAppointmentById(Long id) {

        return busAppointmentMapper.selectBusAppointmentById(id);
    }

    /**
     * 查询养修信息预约列表
     *
     * @param busAppointment 养修信息预约
     * @return 养修信息预约
     */
    @Override
    public List<BusAppointment> selectBusAppointmentList(BusAppointment busAppointment) {

        return busAppointmentMapper.selectBusAppointmentList(busAppointment);
    }

    /**
     * 新增养修信息预约
     *
     * @param busAppointment 养修信息预约
     * @return 结果
     */
    @Override
    public int insertBusAppointment(BusAppointment busAppointment) {
        //1.验证参数合法性
        if (StringUtils.isNull(busAppointment)
        ) {
            throw new RuntimeException("非法参数");
        }//2.验证相应字段不能为Null
        if (StringUtils.isNull(busAppointment.getCustomerPhone())
                || StringUtils.isNull(busAppointment.getAppointmentTime())
                || StringUtils.isNull(busAppointment.getLicensePlate())
                || StringUtils.isNull(busAppointment.getCarSeries())
                || StringUtils.isNull(busAppointment.getServiceType())) {
            throw new RuntimeException("非法参数");
        }
        //3.验证手机号码合法性
        if (!RegexUtils.isChinaPhoneLegal(busAppointment.getCustomerPhone())) {
            throw new RuntimeException("手机号码格式有误");

        }
        //4.验证车牌号码合法性
        VehiclePlateNoUtil.VehiclePlateNoEnum vehiclePlateNo = VehiclePlateNoUtil.getVehiclePlateNo(busAppointment.getLicensePlate());
        if (vehiclePlateNo == null) {
            throw new RuntimeException("车牌号码格式有误");
        }
        //5.验证时间合法性
        Date nowDate = DateUtils.getNowDate();
        if (busAppointment.getAppointmentTime().before(nowDate)) {
            throw new RuntimeException("预约时间不合法");
        }
        //为了系统的安全性，一般前台直接传递的对象是不可以直接使用的，因为有一些默认值的存在。
        //防止被恶意携带数据（默认值）。导致系统出现异常，尤其一些关键数据
        //即使直接使用，那么也需要给这些关键值重新赋值
        busAppointment.setStatus(BusAppointment.STATUS_BOOKING);
        busAppointment.setCreateTime(DateUtils.getNowDate());
        return busAppointmentMapper.insertBusAppointment(busAppointment);
    }

    /**
     * 修改养修信息预约
     *
     * @param busAppointment 养修信息预约
     * @return 结果
     */
    @Override
    public int updateBusAppointment(BusAppointment busAppointment) {
        //1.验证参数合法性
        if (StringUtils.isNull(busAppointment)
        ) {
            throw new RuntimeException("非法参数");
        }//2.验证相应字段不能为Null
        if (StringUtils.isNull(busAppointment.getCustomerPhone())
                || StringUtils.isNull(busAppointment.getAppointmentTime())
                || StringUtils.isNull(busAppointment.getLicensePlate())
                || StringUtils.isNull(busAppointment.getCarSeries())
                || StringUtils.isNull(busAppointment.getServiceType())) {
            throw new RuntimeException("非法参数");
        }
        //3.验证手机号码合法性
        if (!RegexUtils.isChinaPhoneLegal(busAppointment.getCustomerPhone())) {
            throw new RuntimeException("手机号码格式有误");

        }
        //4.验证车牌号码合法性
        VehiclePlateNoUtil.VehiclePlateNoEnum vehiclePlateNo = VehiclePlateNoUtil.getVehiclePlateNo(busAppointment.getLicensePlate());
        if (vehiclePlateNo == null) {
            throw new RuntimeException("车牌号码格式有误");
        }
        //5.验证时间合法性
        Date nowDate = DateUtils.getNowDate();
        if (busAppointment.getAppointmentTime().before(nowDate)) {
            throw new RuntimeException("预约时间不合法");
        }
        //只有状态为预约中才可以进行编辑操作
        //前台传递的数据很可能将一些重要的参数篡改过，所以我们不能直接使用
        BusAppointment oldObj = busAppointmentMapper.selectBusAppointmentById(busAppointment.getId());
        if (!oldObj.getStatus().equals(BusAppointment.STATUS_BOOKING)) {
            throw new RuntimeException("养修预约记录必须处于预约中才可以进行编辑操作");
        }
        //为了防止恶意篡改数据，我们可以将状态重新赋值
        busAppointment.setStatus(oldObj.getStatus());
        return busAppointmentMapper.updateBusAppointment(busAppointment);
    }

    /**
     * 批量删除养修信息预约
     *
     * @param ids 需要删除的养修信息预约主键
     * @return 结果
     */
    @Override
    public int deleteBusAppointmentByIds(Long[] ids) {
        if (ids == null || ids.length <= 0) {
            throw new RuntimeException("非法参数");

        }
        //for (Long id : ids) {
        //    BusAppointment oldObj = this.selectBusAppointmentById(id);
        //    if (oldObj==null){
        //        throw new RuntimeException("非法参数");
        //    }
        //    if (BusAppointment.STATUS_BOOKING.equals(oldObj.getStatus())){
        //        throw new RuntimeException("非预约中的记录无法删除");
        //    }
        //}
        boolean flag = true;
        List<BusAppointment> busAppointmentList = busAppointmentMapper.selectBusAppointListBatch(ids);
        if (ids == null || ids.length <= 0) {
            throw new RuntimeException("非法参数");
        }
        for (BusAppointment busAppointment : busAppointmentList) {
            //只要存在一次状态不是预约中，就不让删除
            flag = flag && BusAppointment.STATUS_BOOKING.equals(busAppointment.getStatus());
        }
        if (!flag) {
            throw new RuntimeException("非预约中的记录无法删除");
        }
        return busAppointmentMapper.deleteBusAppointmentByIds(ids);
    }

    /**
     * 删除养修信息预约信息
     *
     * @param id 养修信息预约主键
     * @return 结果
     */
    @Override
    public int deleteBusAppointmentById(Long id) {
        return busAppointmentMapper.deleteBusAppointmentById(id);
    }

    /**
     * 到店功能
     *
     * @param id
     * @return
     */
    @Override
    public int arrived(Long id) {
// 参数合理化验证
        if (id == null) {
            throw new RuntimeException("非法参数");
        }
        BusAppointment oldObj = this.selectBusAppointmentById(id);
        if (oldObj == null) {
            throw new RuntimeException("非法参数");
        }
//     1. 必须是预约中才可以进行到店操作。
        if (!BusAppointment.STATUS_BOOKING.equals(oldObj.getStatus())) {
            throw new RuntimeException("处于预约中的记录才允许进行到店操作");
        }
//     2. 到店之后需要完成的功能
        return busAppointmentMapper.changeStatusAndArrivedTimeById(new Date(), BusAppointment.STATUS_ARRIVED, id);
//         - 到店时间需要设置。
//         - 修改状态为已到店。

    }

    @Override
    public int cancel(Long id) {
        // 参数合理化验证
        if (id == null) {
            throw new RuntimeException("非法参数");
        }
        BusAppointment oldObj = this.selectBusAppointmentById(id);
        if (oldObj == null) {
            throw new RuntimeException("非法参数");
        }
//     1. 必须是预约中才可以进行到店操作。
        if (!BusAppointment.STATUS_BOOKING.equals(oldObj.getStatus())) {
            throw new RuntimeException("处于预约中的记录才允许进行取消操作");
        }
        return busAppointmentMapper.changeStatusByAppointmentId(id, BusAppointment.STATUS_USER_CANCELLED);
    }

    /**
     * 根据id修改支付状态
     *
     * @param id
     * @param status
     */
    @Override
    public void updateStatusById(Long id, Integer status) {
        busAppointmentMapper.changeStatusById(status, id);
    }

    /**
     * 查找或生成结算单
     */
    @Override
    public Long createOrSelectStatement(Long id) {
        //参数合理化校验
        if (id == null) {
            throw new RuntimeException("非法参数");
        }
        BusAppointment busAppointment = this.selectBusAppointmentById(id);
        if (busAppointment == null) {
            throw new RuntimeException("非法参数");
        }
        //判断预约单状态 0-预约中 2-用户取消 3-超时取消都不可以取消结算单
        if (BusAppointment.STATUS_BOOKING.equals(busAppointment.getStatus()) ||
                BusAppointment.STATUS_USER_CANCELLED.equals(busAppointment.getStatus()) ||
                BusAppointment.STATUS_TIMEOUT_CANCELLED.equals(busAppointment.getStatus())) {
            throw new RuntimeException("预约单处于预约中,用户取消,超时取消则不允许生成结算单");
        }
        //根据参数(预约单id查询结算单)
        BusStatement statement = statementService.queryStatementByAppointmentId(id);
        //没有查询到则新建结算单
        if (statement == null) {
            statement = new BusStatement();
            if (busAppointment.getWxUserId() == null) {
                statement.setCustomerName("客服录入");
            } else {
                //通过id获取用户信息,将用户名赋予

            }

            statement.setCustomerPhone(busAppointment.getCustomerPhone());
            statement.setActualArrivalTime(busAppointment.getActualArrivalTime());
            statement.setLicensePlate(busAppointment.getLicensePlate());
            statement.setCarSeries(busAppointment.getCarSeries());
            statement.setServiceType(Math.toIntExact(busAppointment.getServiceType()));
            statement.setInfo(busAppointment.getInfo());
            //将预约单id也需要添加到结算单对象中
            statement.setAppointmentId(id);
            //安全性设置
            statement.setStatus(BusStatement.STATUS_CONSUME);
            //插入结算单
            statementService.insertBusStatement(statement);
            //更新预约单状态
            busAppointmentMapper.changeStatusById(BusAppointment.STATUS_SETTLED,id);
        }
        //没有查询则返回结算单id
        return statement.getId();
    }


}
