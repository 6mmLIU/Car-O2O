package cn.wolfcode.business.controller;


import cn.wolfcode.business.domain.BusAppointment;
import cn.wolfcode.business.domain.BusServiceItem;
import cn.wolfcode.business.dto.StartAuditDTO;
import cn.wolfcode.business.service.IBusServiceItemService;
import cn.wolfcode.common.core.controller.BaseController;
import cn.wolfcode.common.core.domain.AjaxResult;
import cn.wolfcode.common.core.page.TableDataInfo;
import cn.wolfcode.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/business/serviceitem")
public class BusServiceItemController extends BaseController {
    @Autowired
    private IBusServiceItemService itemService;

    @Autowired
    private ISysUserService userService;
    /**
     * 服务项列表
     *
     * @param busServiceItem
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(BusServiceItem busServiceItem) {

        // 设置分页
        startPage();
        List<BusServiceItem> busServiceItems = itemService.selectBusServiceItemList(busServiceItem);
        return getDataTable(busServiceItems);
    }

    @GetMapping("/getAuditInfo")
    public AjaxResult getAuditInfo(Long id) {
        Map map = itemService.getAuditInfo(id);
        //查店长,放到map中


        //查财务,放到map中

        return AjaxResult.success(map);
    }

    /**
     * 服务项列表(不分页)
     *
     * @param busServiceItem
     * @return
     */
    @GetMapping(value = "/list", headers = "cmd=listNotPage")
    public AjaxResult listNotPage(BusServiceItem busServiceItem) {

        List<BusServiceItem> list = itemService.selectBusServiceItemList(busServiceItem);
        return AjaxResult.success(list);
    }

    /**
     * 按照id查询对应的服务项信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(itemService.selectBusServiceItemById(id));
    }

    /**
     * 服务项新增
     *
     * @param busServiceItem
     * @return
     */

    @PostMapping
    public AjaxResult add(@RequestBody BusServiceItem busServiceItem) {

        return toAjax(itemService.insertBusServiceItem(busServiceItem));
    }

    /**
     * 服务项编辑
     *
     * @param busServiceItem
     * @return
     */

    @PutMapping
    public AjaxResult edit(@RequestBody BusServiceItem busServiceItem) {

        return toAjax(itemService.updateBusServiceItem(busServiceItem));
    }

    /**
     * 上架功能
     *
     * @param id
     * @return
     */
    @PatchMapping(value = "/{id}", headers = "cmd=saleOn")
    public AjaxResult saleOnOp(@PathVariable Long id) {
        return toAjax(itemService.saleOn(id));
    }

    /**
     * 下架功能
     *
     * @param id
     * @return
     */
    @PatchMapping(value = "/{id}", headers = "cmd=saleOff")
    public AjaxResult saleOffOp(@PathVariable Long id) {
        return toAjax(itemService.saleOff(id));
    }

    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(itemService.deleteBusServiceItemById(id));
    }


    @PostMapping("/startAudit")
    public AjaxResult startAudit(@RequestBody StartAuditDTO auditDTO) {
        itemService.startAudit(auditDTO.getServiceItemId(), auditDTO.getShopOwnerId(), auditDTO.getFinanceId(), auditDTO.getInfo());
        return AjaxResult.success();
    }

}

