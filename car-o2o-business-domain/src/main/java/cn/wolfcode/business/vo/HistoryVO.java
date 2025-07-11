package cn.wolfcode.business.vo;

import lombok.Data;

@Data
public class HistoryVO {
    private String taskName;
    private String startTime;
    private String endTime;
    private String durationInMillis;
    private String comment;
}
