package cn.wolfcode.business.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;
import javax.servlet.http.HttpServletResponse;

import cn.wolfcode.common.utils.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.wolfcode.common.annotation.Log;
import cn.wolfcode.common.core.controller.BaseController;
import cn.wolfcode.common.core.domain.AjaxResult;
import cn.wolfcode.common.enums.BusinessType;
import cn.wolfcode.business.domain.BusStatement;
import cn.wolfcode.business.service.IBusStatementService;
import cn.wolfcode.common.utils.poi.ExcelUtil;
import cn.wolfcode.common.core.page.TableDataInfo;

/**
 * 结算单Controller
 *
 * @author wolfcode
 * @date 2025-07-04
 */
@RestController
@RequestMapping("/business/statement")
public class BusStatementController extends BaseController {
    @Autowired
    private IBusStatementService busStatementService;

    /**
     * 查询结算单列表
     */
    @PreAuthorize("@ss.hasPermi('business:statement:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusStatement busStatement) throws ParseException {
        startPage();
//        String endRelease="";
        //获取到额外参数
//        if (busStatement!=null && busStatement.getParams()!=null){
//            Map<String, Object> map = busStatement.getParams();
//
//            String beginTime = (String) map.get("beginTime");
//            String endTime = (String) map.get("endTime");
//            //将日期字符串转换为Date类型
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            if (StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime)){
//                Date beginDate = sdf.parse(beginTime);
//                Date endDate=sdf.parse(endTime);
//                //+1天 -1秒
//                Calendar endCalendar=Calendar.getInstance();
//                endCalendar.setTime(endDate);
//                //+1天
//                endCalendar.add(Calendar.DAY_OF_YEAR,1);
//                //-1秒
//                endCalendar.add(Calendar.SECOND,-1);
//                //将最终的时间转换回字符串传递
//                endRelease=sdf.format(endCalendar.getTime());
//                map.put("endTime",endRelease);
//            }
//
//        }

        List<BusStatement> list = busStatementService.selectBusStatementList(busStatement);
        return getDataTable(list);
    }

    /**
     * 导出结算单列表
     */
    @PreAuthorize("@ss.hasPermi('business:statement:export')")
    @Log(title = "结算单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusStatement busStatement) throws ParseException {
        List<BusStatement> list = busStatementService.selectBusStatementList(busStatement);
        ExcelUtil<BusStatement> util = new ExcelUtil<BusStatement>(BusStatement.class);
        util.exportExcel(response, list, "结算单数据");
    }

    /**
     * 获取结算单详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:statement:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(busStatementService.selectBusStatementById(id));
    }

    /**
     * 新增结算单
     */
    @PreAuthorize("@ss.hasPermi('business:statement:add')")
    @Log(title = "结算单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusStatement busStatement) {
        return toAjax(busStatementService.insertBusStatement(busStatement));
    }

    /**
     * 修改结算单
     */
    @PreAuthorize("@ss.hasPermi('business:statement:edit')")
    @Log(title = "结算单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusStatement busStatement) {
        return toAjax(busStatementService.updateBusStatement(busStatement));
    }

    /**
     * 删除结算单
     */
    @PreAuthorize("@ss.hasPermi('business:statement:remove')")
    @Log(title = "结算单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(busStatementService.deleteBusStatementByIds(ids));
    }
}
