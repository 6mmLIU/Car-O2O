package cn.wolfcode.business.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
import cn.wolfcode.business.domain.BusBpmnInfo;
import cn.wolfcode.business.service.IBusBpmnInfoService;
import cn.wolfcode.common.utils.poi.ExcelUtil;
import cn.wolfcode.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 流程定义明细Controller
 *
 * @author wolfcode
 * @date 2025-07-07
 */
@RestController
@RequestMapping("/business/bpmninfo")
public class BusBpmnInfoController extends BaseController {
    @Autowired
    private IBusBpmnInfoService busBpmnInfoService;

    @GetMapping("/showResource")
    public void showResource(Long id, String type, HttpServletResponse response) throws IOException {
        //读取资源文件的流
        InputStream is = (InputStream) busBpmnInfoService.getResource(id, type);

        response.setContentType("application/octet-stream");
        //写入客户端浏览器的流
        ServletOutputStream os = response.getOutputStream();
        //流拷贝
        IOUtils.copy(is, os);
    }


    /**
     * 查询流程定义明细列表
     */
    @PreAuthorize("@ss.hasPermi('business:bpmninfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(BusBpmnInfo busBpmnInfo) {
        startPage();
        List<BusBpmnInfo> list = busBpmnInfoService.selectBusBpmnInfoList(busBpmnInfo);
        return getDataTable(list);
    }

    /**
     * 导出流程定义明细列表
     */
    @PreAuthorize("@ss.hasPermi('business:bpmninfo:export')")
    @Log(title = "流程定义明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BusBpmnInfo busBpmnInfo) {
        List<BusBpmnInfo> list = busBpmnInfoService.selectBusBpmnInfoList(busBpmnInfo);
        ExcelUtil<BusBpmnInfo> util = new ExcelUtil<BusBpmnInfo>(BusBpmnInfo.class);
        util.exportExcel(response, list, "流程定义明细数据");
    }

    /**
     * 获取流程定义明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:bpmninfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(busBpmnInfoService.selectBusBpmnInfoById(id));
    }

    /**
     * 新增流程定义明细
     */
    @PreAuthorize("@ss.hasPermi('business:bpmninfo:add')")
    @Log(title = "流程定义明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(MultipartFile file, Long bpmnType, String info) throws IOException {
        return toAjax(busBpmnInfoService.deploy(file, bpmnType, info));
    }

    /**
     * 修改流程定义明细
     */
    @PreAuthorize("@ss.hasPermi('business:bpmninfo:edit')")
    @Log(title = "流程定义明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusBpmnInfo busBpmnInfo) {
        return toAjax(busBpmnInfoService.updateBusBpmnInfo(busBpmnInfo));
    }

    /**
     * 删除流程定义明细
     */
    @PreAuthorize("@ss.hasPermi('business:bpmninfo:remove')")
    @Log(title = "流程定义明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(busBpmnInfoService.deleteBusBpmnInfoByIds(ids));
    }
    @DeleteMapping("/cancel")
    public AjaxResult cancel(Long id){
        return toAjax(busBpmnInfoService.cancel(id));
    }
}
