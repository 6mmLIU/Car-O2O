package cn.wolfcode.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.wolfcode.common.annotation.Excel;
import cn.wolfcode.common.core.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
/**
 * 养修信息预约对象 bus_appointment
 *
 * @author wolfcode
 * @date 2025-04-07
 */
public class BusAppointment extends BaseEntity
{


    private static final long serialVersionUID = 1L;
    //对应五种状态的常量值
    public static final Integer STATUS_BOOKING = 0;//预约中
    public static final Integer STATUS_ARRIVED = 1;//到店
    public static final Integer STATUS_USER_CANCELLED = 2;//用户取消
    public static final Integer STATUS_TIMEOUT_CANCELLED = 3;//超时取消
    public static final Integer STATUS_SETTLED = 4;//结算单生成
    public static final Integer STATUS_PAID = 5;//已支付

    /** 主键 */
    private Long id;

    /** 微信用户ID */
    @Excel(name = "微信用户ID")
    private Long wxUserId;

    /** 客户联系方式 */
    @Excel(name = "客户联系方式")
    private String customerPhone;

    /** 预约时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预约时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date appointmentTime;

    /** 实际到店时间 */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @Excel(name = "实际到店时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualArrivalTime;

    /** 车牌号码 */
    @Excel(name = "车牌号码")
    private String licensePlate;

    /** 汽车类型 */
    @Excel(name = "汽车类型")
    private String carSeries;

    /** 服务类型【维修0/保养1】 */
    @Excel(name = "服务类型【维修0/保养1】")
    private Long serviceType;

    /** 备注信息 */
    @Excel(name = "备注信息")
    private String info;

    /** 状态【预约中0/已到店1/用户取消2/超时取消3/已结算4/已支付5】 */
    @Excel(name = "状态【预约中0/已到店1/用户取消2/超时取消3/已结算4/已支付5】")
    private Integer status;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("wxUserId", getWxUserId())
            .append("customerPhone", getCustomerPhone())
            .append("appointmentTime", getAppointmentTime())
            .append("actualArrivalTime", getActualArrivalTime())
            .append("licensePlate", getLicensePlate())
            .append("carSeries", getCarSeries())
            .append("serviceType", getServiceType())
            .append("createTime", getCreateTime())
            .append("info", getInfo())
            .append("status", getStatus())
            .toString();
    }
}
