package cn.wolfcode.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import cn.wolfcode.common.annotation.Excel;
import cn.wolfcode.common.core.domain.BaseEntity;
@Getter
@Setter
/**
 * 流程定义明细对象 bus_bpmn_info
 *
 * @author wolfcode
 * @date 2025-07-07
 */
public class BusBpmnInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 流程名称 */
    @Excel(name = "流程名称")
    private String bpmnLabel;

    /** 流程类型 */
    @Excel(name = "流程类型")
    private Long bpmnType;

    /** activity流程定义生成的key */
    @Excel(name = "activity流程定义生成的key")
    private String processDefinitionKey;

    /** 部署时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "部署时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deployTime;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 描述信息 */
    @Excel(name = "描述信息")
    private String info;


}
