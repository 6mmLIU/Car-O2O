package cn.wolfcode.business.dto;

import lombok.Data;

@Data
public class AuditDTO {
    private Long id;
    private Integer auditStatus; //1拒绝 2通过
    private String info;
}
