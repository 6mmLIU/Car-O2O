package cn.wolfcode.business.domain;

import cn.wolfcode.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class BusServiceItem extends BaseEntity {

    // 序列化ID
    private static final long serialVersionUID = 1L;

    // 套餐状态
    public static final Integer CARPACKAGE_NO = 0;  // 不是套餐
    public static final Integer CARPACKAGE_YES = 1; // 是套餐

    // 审核状态
    public static final Integer AUDITSTATUS_INIT = 0; // 初始化
    public static final Integer AUDITSTATUS_AUDITING = 1; // 审核中
    public static final Integer AUDITSTATUS_APPROVED = 2; // 审核通过
    public static final Integer AUDITSTATUS_REPLY = 3; // 审核拒绝
    public static final Integer AUDITSTATUS_NO_REQUIRED = 4; // 无需审核

    // 上架状态
    public static final Integer SALESTATUS_OFF = 0; // 下架
    public static final Integer SALESTATUS_ON = 1; // 上架


    private Long id;                   // 虚拟ID
    private String name;              // 服务项名称
    private BigDecimal originalPrice; // 服务项原价
    private BigDecimal discountPrice; // 服务项折扣价
    private Integer carPackage;       // 是否套餐【1是/0否】
    private String info;              // 备注信息
    private Date createTime;          // 创建时间
    private Integer serviceCatalog;   // 服务分类【0维修/1保养/2其他】
    private Integer auditStatus;      // 审核状态【0初始化/1审核中/2审核通过/3审核拒绝/4无需审核】
    private Integer saleStatus;       // 上架状态【1已上架/0未上架】
}
