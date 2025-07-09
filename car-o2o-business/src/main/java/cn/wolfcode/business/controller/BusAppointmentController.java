package cn.wolfcode.business.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.wolfcode.common.annotation.Log;
import cn.wolfcode.common.core.controller.BaseController;
import cn.wolfcode.common.core.domain.AjaxResult;
import cn.wolfcode.common.enums.BusinessType;
import cn.wolfcode.business.domain.BusAppointment;
import cn.wolfcode.business.service.IBusAppointmentService;
import cn.wolfcode.common.utils.poi.ExcelUtil;
import cn.wolfcode.common.core.page.TableDataInfo;

/**
 * 养修信息预约Controller
 *
 * @author wolfcode
 * @date 2025-04-07
 */
@RestController
@RequestMapping("/business/appointment")
public class BusAppointmentController extends BaseController
{
    @Autowired
    private IBusAppointmentService busAppointmentService;

    /**
     * 查询养修信息预约列表
     */
    @PreAuthorize("@ss.hasPermi('business:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusAppointment busAppointment)
    {
        startPage();
        List<BusAppointment> list = busAppointmentService.selectBusAppointmentList(busAppointment);
        return getDataTable(list);
    }

    /**
     * 导出养修信息预约列表
     */
    @PreAuthorize("@ss.hasPermi('business:appointment:export')")
    @Log(title = "养修信息预约", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusAppointment busAppointment)
    {
        List<BusAppointment> list = busAppointmentService.selectBusAppointmentList(busAppointment);
        ExcelUtil<BusAppointment> util = new ExcelUtil<BusAppointment>(BusAppointment.class);
        util.exportExcel(response, list, "养修信息预约数据");
    }

    /**
     * 获取养修信息预约详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:appointment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(busAppointmentService.selectBusAppointmentById(id));
    }

    /**
     * 新增养修信息预约
     */
    @PreAuthorize("@ss.hasPermi('business:appointment:add')")
    @Log(title = "养修信息预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusAppointment busAppointment)
    {
        return toAjax(busAppointmentService.insertBusAppointment(busAppointment));
    }

    /**
     * 修改养修信息预约
     */
    @PreAuthorize("@ss.hasPermi('business:appointment:edit')")
    @Log(title = "养修信息预约", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusAppointment busAppointment)
    {
        return toAjax(busAppointmentService.updateBusAppointment(busAppointment));
    }

    /**
     * 删除养修信息预约
     */
    @PreAuthorize("@ss.hasPermi('business:appointment:remove')")
    @Log(title = "养修信息预约", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult cancel(@PathVariable Long[] ids)
    {
        return toAjax(busAppointmentService.deleteBusAppointmentByIds(ids));
    }
    /**
     * 到店功能
     */

    @PatchMapping(value = "/{id}",headers="cmd=arrived")
    public AjaxResult arrived(@PathVariable ("id") Long id)
    {
        return toAjax(busAppointmentService.arrived(id));
    }

    /**
     * 取消功能
     */
    @PatchMapping(value = "/{id}",headers="cmd=cancel")
    public AjaxResult cancel(@PathVariable ("id") Long id)
    {
        return toAjax(busAppointmentService.cancel(id));
    }
    /**
     * 结算单按钮
     */
    @PostMapping("/{id}/statement")
        public AjaxResult createOrSelectStatement(@PathVariable Long id){
               Long statementId= busAppointmentService.createOrSelectStatement(id);
               return AjaxResult.success(statementId);
        }

}
