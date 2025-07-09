package cn.wolfcode.business.service;

import java.util.List;
import cn.wolfcode.business.domain.BusAppointment;

/**
 * 养修信息预约Service接口
 *
 * @author wolfcode
 * @date 2025-04-07
 */
public interface IBusAppointmentService
{
    /**
     * 查询养修信息预约
     *
     * @param id 养修信息预约主键
     * @return 养修信息预约
     */
    public BusAppointment selectBusAppointmentById(Long id);

    /**
     * 查询养修信息预约列表
     *
     * @param busAppointment 养修信息预约
     * @return 养修信息预约集合
     */
    public List<BusAppointment> selectBusAppointmentList(BusAppointment busAppointment);

    /**
     * 新增养修信息预约
     *
     * @param busAppointment 养修信息预约
     * @return 结果
     */
    public int insertBusAppointment(BusAppointment busAppointment);

    /**
     * 修改养修信息预约
     *
     * @param busAppointment 养修信息预约
     * @return 结果
     */
    public int updateBusAppointment(BusAppointment busAppointment);

    /**
     * 批量删除养修信息预约
     *
     * @param ids 需要删除的养修信息预约主键集合
     * @return 结果
     */
    public int deleteBusAppointmentByIds(Long[] ids);

    /**
     * 删除养修信息预约信息
     *
     * @param id 养修信息预约主键
     * @return 结果
     */
    public int deleteBusAppointmentById(Long id);

    /**
     *
     * 到店功能
     * @param id
     * @return
     */
    int arrived(Long id);

    int cancel(Long id);

    /**
     * 根据id修改支付状态
     */
    void updateStatusById(Long id, Integer status);

    /**
     * 结算单按钮
     */
    Long createOrSelectStatement(Long id);
}
