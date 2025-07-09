package cn.wolfcode.business.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class StartAuditDTO {
    private Long serviceItemId;
    private Long shopOwnerId;
    private Long financeId;
    private String info;

}
