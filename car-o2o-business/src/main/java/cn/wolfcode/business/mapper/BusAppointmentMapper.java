package cn.wolfcode.business.mapper;

import java.util.Date;
import java.util.List;
import cn.wolfcode.business.domain.BusAppointment;
import org.apache.ibatis.annotations.Param;

/**
 * 养修信息预约Mapper接口
 *
 * @author wolfcode
 * @date 2025-04-07
 */
public interface BusAppointmentMapper
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
     * 删除养修信息预约
     *
     * @param id 养修信息预约主键
     * @return 结果
     */
    public int deleteBusAppointmentById(Long id);

    /**
     * 批量删除养修信息预约
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBusAppointmentByIds(Long[] ids);

    /**
     * 到店功能
     * @param date
     * @param statusArrived
     * @param id
     * @return
     */
    int changeStatusAndArrivedTimeById(@Param("date") Date date, @Param("status") Integer statusArrived, @Param("id") Long id);
    /**
     * 取消功能
     */

    int changeStatusByAppointmentId(@Param("id") Long id, @Param("status") Integer statusUserCancelled);

    /**
     * 按照ids批量查询预约对象
     * @param ids
     * @return
     */
    List<BusAppointment> selectBusAppointListBatch(@Param("ids") Long[] ids);

    int changeStatusById(@Param("status") Integer status, @Param("id") Long id);
}
