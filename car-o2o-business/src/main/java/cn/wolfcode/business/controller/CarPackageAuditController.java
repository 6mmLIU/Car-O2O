package cn.wolfcode.business.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.wolfcode.business.dto.AuditDTO;
import cn.wolfcode.business.vo.HistoryVO;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.wolfcode.common.annotation.Log;
import cn.wolfcode.common.core.controller.BaseController;
import cn.wolfcode.common.core.domain.AjaxResult;
import cn.wolfcode.common.enums.BusinessType;
import cn.wolfcode.business.domain.CarPackageAudit;
import cn.wolfcode.business.service.ICarPackageAuditService;
import cn.wolfcode.common.utils.poi.ExcelUtil;
import cn.wolfcode.common.core.page.TableDataInfo;

/**
 * 套餐审核Controller
 *
 * @author wolfcode
 * @date 2025-07-09
 */
@RestController
@RequestMapping("/business/audit")
public class CarPackageAuditController extends BaseController {
    @Autowired
    private ICarPackageAuditService carPackageAuditService;

    /**
     * 查询套餐审核列表
     */
    @PreAuthorize("@ss.hasPermi('business:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(CarPackageAudit carPackageAudit) {
        startPage();
        List<CarPackageAudit> list = carPackageAuditService.selectCarPackageAuditList(carPackageAudit);
        return getDataTable(list);
    }

    /**
     * 查询我的待办
     */

    @GetMapping("/todoTaskList")
    public TableDataInfo todoTaskList(CarPackageAudit carPackageAudit) {
        startPage();
        List<CarPackageAudit> list = carPackageAuditService.selectTodoTaskList(carPackageAudit);
        return getDataTable(list);
    }

    /**
     * 查询我的已办任务列表
     */
    @GetMapping("/doneTaskList")
    public TableDataInfo doneTaskList(CarPackageAudit carPackageAudit) {
        startPage();
        List<CarPackageAudit> list = carPackageAuditService.selectDoneTaskList(carPackageAudit);
        return getDataTable(list);
    }

    /**
     * 导出套餐审核列表
     */
    @PreAuthorize("@ss.hasPermi('business:audit:export')")
    @Log(title = "套餐审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CarPackageAudit carPackageAudit) {
        List<CarPackageAudit> list = carPackageAuditService.selectCarPackageAuditList(carPackageAudit);
        ExcelUtil<CarPackageAudit> util = new ExcelUtil<CarPackageAudit>(CarPackageAudit.class);
        util.exportExcel(response, list, "套餐审核数据");
    }

    /**
     * 获取套餐审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:audit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(carPackageAuditService.selectCarPackageAuditById(id));
    }

    /**
     * 新增套餐审核
     */
    @PreAuthorize("@ss.hasPermi('business:audit:add')")
    @Log(title = "套餐审核", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CarPackageAudit carPackageAudit) {
        return toAjax(carPackageAuditService.insertCarPackageAudit(carPackageAudit));
    }

    /**
     * 修改套餐审核
     */
    @PreAuthorize("@ss.hasPermi('business:audit:edit')")
    @Log(title = "套餐审核", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CarPackageAudit carPackageAudit) {
        return toAjax(carPackageAuditService.updateCarPackageAudit(carPackageAudit));
    }

    /**
     * 删除套餐审核
     */
    @PreAuthorize("@ss.hasPermi('business:audit:remove')")
    @Log(title = "套餐审核", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(carPackageAuditService.deleteCarPackageAuditByIds(ids));
    }

    @GetMapping("/getProcessImg")
    public void getProcessImg(String instanceId, HttpServletResponse response) throws IOException {
        InputStream inputStream = carPackageAuditService.getProcessImg(instanceId);
        response.setContentType("application/octet-stream");
        IOUtils.copy(inputStream, response.getOutputStream());
    }

    @PatchMapping(headers = "cmd=audit")
    public AjaxResult audit(@RequestBody AuditDTO dto) {
        carPackageAuditService.audit(dto);
        return AjaxResult.success();

    }

    /**
     * 查询审批历史列表
     */
    @GetMapping("/historyTask")
    public AjaxResult historyTask(String instanceId) {
        // 需要在Service中实现这个方法
        List<HistoryVO> vos = carPackageAuditService.getHistoryVOList(instanceId);
        return AjaxResult.success(vos);
    }

    @DeleteMapping(value = "/{id}", headers = "cmd=processCancel")
    public AjaxResult processCancel(@PathVariable("id") Long id) {
        carPackageAuditService.processCancel(id);
        return AjaxResult.success();
    }

    @GetMapping("/selectDoneTaskList")
    public TableDataInfo selectDoneTakList(CarPackageAudit carPackageAudit) {
        startPage();
        List<CarPackageAudit> list = carPackageAuditService.selectDoneTaskList(carPackageAudit);
        return getDataTable(list);
    }

}
