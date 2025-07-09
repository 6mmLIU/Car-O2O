package cn.wolfcode.business.vo;

import cn.wolfcode.business.domain.BusStatementItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class StatementItemVO {
    private List<BusStatementItem> statementItemList=new ArrayList<BusStatementItem>();
    private BigDecimal discountAmount;
    private Long statementId;
}
