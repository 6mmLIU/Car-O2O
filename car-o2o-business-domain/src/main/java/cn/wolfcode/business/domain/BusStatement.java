package cn.wolfcode.business.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.wolfcode.common.annotation.Excel;
import cn.wolfcode.common.core.domain.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 结算单对象 bus_statement
 *
 * @author wolfcode
 * @date 2025-07-04
 */
@Getter
@Setter
public class BusStatement extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final Integer STATUS_CONSUME = 0;
    public static final Integer STATUS_PAID = 1;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 客户姓名
     */
    @Excel(name = "客户姓名")
    private String customerName;

    /**
     * 客户联系方式
     */
    @Excel(name = "客户联系方式")
    private String customerPhone;

    /**
     * 实际到店时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际到店时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualArrivalTime;

    /**
     * 车牌号码
     */
    @Excel(name = "车牌号码")
    private String licensePlate;

    /**
     * 汽车类型
     */
    @Excel(name = "汽车类型")
    private String carSeries;

    /**
     * 服务类型【0维修|1保养】
     */
    @Excel(name = "服务类型【0维修|1保养】")
    private Integer serviceType;

    /**
     * 预约单ID（通过这个来判断是否预约用户，唯一标识）
     */
    @Excel(name = "预约单ID", readConverterExp = "通=过这个来判断是否预约用户，唯一标识")
    private Long appointmentId;

    /**
     * 结算状态（消费中0/已支付1）
     */
    @Excel(name = "结算状态", readConverterExp = "消=费中0/已支付1")
    private Integer status;

    /**
     * 收款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "收款时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /**
     * 收款人id
     */
    @Excel(name = "收款人id")
    private Long payeeId;

    /**
     * 总消费金额
     */
    @Excel(name = "总消费金额")
    private BigDecimal totalAmount;

    /**
     * 服务项数量
     */
    @Excel(name = "服务项数量")
    private BigDecimal totalQuantity;

    /**
     * 折扣金额
     */
    @Excel(name = "折扣金额")
    private BigDecimal discountAmount;

    /**
     * 备注信息
     */
    @Excel(name = "备注信息")
    private String info;

    /**
     * 0没有删除/1删除
     */
    @Excel(name = "0没有删除/1删除")
    private Long isDelete;

}
