package cn.wolfcode.business.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.wolfcode.business.vo.StatementItemVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.wolfcode.common.annotation.Log;
import cn.wolfcode.common.core.controller.BaseController;
import cn.wolfcode.common.core.domain.AjaxResult;
import cn.wolfcode.common.enums.BusinessType;
import cn.wolfcode.business.domain.BusStatementItem;
import cn.wolfcode.business.service.IBusStatementItemService;
import cn.wolfcode.common.utils.poi.ExcelUtil;
import cn.wolfcode.common.core.page.TableDataInfo;

/**
 * 结算单明细Controller
 *
 * @author wolfcode
 * @date 2025-07-06
 */
@RestController
@RequestMapping("/business/item")
public class BusStatementItemController extends BaseController
{
    @Autowired
    private IBusStatementItemService busStatementItemService;

    /**
     * 新增结算单明细
     */
    @PreAuthorize("@ss.hasPermi('business:item:add')")
    @Log(title = "结算单明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StatementItemVO vo)
    {
        return toAjax(busStatementItemService.itemSave(vo));
    }
/**
 * 根据结算单id 查询结算单明细列表
 */
@GetMapping(path="/{id}")
    public AjaxResult getStatementItemListByStatementId(@PathVariable Long id){
    return AjaxResult.success(busStatementItemService.getStatementItemListByStatementId(id));
}
/**
 * 支付功能
 */
@PatchMapping("/{statementId}")
    public AjaxResult pay(@PathVariable Long statementId){
    return toAjax(busStatementItemService.pay(statementId));

}

}
