package cn.wolfcode.business.service.impl;

import cn.wolfcode.business.domain.BusBpmnInfo;
import cn.wolfcode.business.domain.BusServiceItem;
import cn.wolfcode.business.domain.CarPackageAudit;
import cn.wolfcode.business.mapper.BusServiceItemMapper;
import cn.wolfcode.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.business.service.IBusBpmnInfoService;
import cn.wolfcode.business.service.IBusServiceItemService;
import cn.wolfcode.common.core.domain.entity.SysUser;
import cn.wolfcode.common.utils.DateUtils;
import cn.wolfcode.common.utils.SecurityUtils;
import cn.wolfcode.system.service.ISysConfigService;
import cn.wolfcode.system.service.ISysUserService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class BusServiceItemServiceImpl implements IBusServiceItemService {
    @Autowired
    private BusServiceItemMapper busServiceItemMapper;

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;
    @Autowired
    private IBusBpmnInfoService busBpmnInfoService;

    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private ISysUserService sysUserService;


    @Override
    public BusServiceItem serviceBusServiceItemById(Long id) {
        return null;
    }


    @Override
    public BusServiceItem selectBusServiceItemById(Long id) {
        return busServiceItemMapper.selectBusServiceItemById(id);
    }

    @Override
    public List<BusServiceItem> selectBusServiceItemList(BusServiceItem busServiceItem) {
        return busServiceItemMapper.selectBusServiceItemList(busServiceItem);
    }

    @Override
    public int insertBusServiceItem(BusServiceItem busServiceItem) {
        // 1. 参数合理化校验
        if (busServiceItem == null) {
            throw new RuntimeException("非法参数");
        }
        // 2. 再次校验必填字段
        // 修正2：之前两次都是 getOriginalPrice()，忘了校验 getDiscountPrice()
        if (busServiceItem.getName() == null
                || busServiceItem.getOriginalPrice() == null
                || busServiceItem.getDiscountPrice() == null
                || busServiceItem.getCarPackage() == null
                || busServiceItem.getServiceCatalog() == null) {
            throw new RuntimeException("非法参数");
        }
        // 3. 价格合理化校验
        if (!(busServiceItem.getDiscountPrice().compareTo(BigDecimal.ZERO) >= 0
                && busServiceItem.getDiscountPrice().compareTo(busServiceItem.getOriginalPrice()) <= 0)) {
            throw new RuntimeException("折扣价必须大于0且不能大于原始价格");
        }
        // 4. 审核状态设置
        if (BusServiceItem.CARPACKAGE_NO.equals(busServiceItem.getCarPackage())) {
            busServiceItem.setAuditStatus(BusServiceItem.AUDITSTATUS_NO_REQUIRED);
        } else {
            busServiceItem.setAuditStatus(BusServiceItem.AUDITSTATUS_INIT);
        }
        // 安全性：强制下架并设置创建时间
        busServiceItem.setSaleStatus(BusServiceItem.SALESTATUS_OFF);
        busServiceItem.setCreateTime(DateUtils.getNowDate());

        return busServiceItemMapper.insertBusServiceItem(busServiceItem);
    }

    @Override
    public int updateBusServiceItem(BusServiceItem busServiceItem) {
        // 1. 参数合理化校验
        if (busServiceItem == null) {
            throw new RuntimeException("非法参数");
        }
        // 2. 再次校验必填字段
        // 修正4：同样修正了 discountPrice 的非空校验
        if (busServiceItem.getName() == null
                || busServiceItem.getOriginalPrice() == null
                || busServiceItem.getDiscountPrice() == null
                || busServiceItem.getCarPackage() == null
                || busServiceItem.getServiceCatalog() == null) {
            throw new RuntimeException("非法参数");
        }
        // 3. 价格合理化校验
        if (!(busServiceItem.getDiscountPrice().compareTo(BigDecimal.ZERO) >= 0
                && busServiceItem.getDiscountPrice().compareTo(busServiceItem.getOriginalPrice()) <= 0)) {
            throw new RuntimeException("折扣价必须大于0且不能大于原始价格");
        }
        // 4. 安全性校验：必须下架才能编辑
        BusServiceItem oldObj = this.selectBusServiceItemById(busServiceItem.getId());
        if (oldObj == null) {
            throw new RuntimeException("非法参数");
        }
        if (BusServiceItem.SALESTATUS_ON.equals(oldObj.getSaleStatus())) {
            throw new RuntimeException("上架中的服务项禁止编辑");
        }
        // 5. 禁止修改套餐属性
        if (!oldObj.getCarPackage().equals(busServiceItem.getCarPackage())) {
            throw new RuntimeException("套餐状态禁止修改");
        }
        // 6. 审核中套餐不能修改
        if (BusServiceItem.CARPACKAGE_YES.equals(oldObj.getCarPackage())
                && BusServiceItem.AUDITSTATUS_AUDITING.equals(oldObj.getAuditStatus())) {
            throw new RuntimeException("审批中的服务项禁止编辑");
        }
        // 7. 审核通过或拒绝的套餐，修改后要重置为初始化
        // 修正5：补上括号，保证 “是套餐 且 (已通过 或 已拒绝)” 时才重置
        if (BusServiceItem.CARPACKAGE_YES.equals(oldObj.getCarPackage())
                && (BusServiceItem.AUDITSTATUS_APPROVED.equals(oldObj.getAuditStatus())
                || BusServiceItem.AUDITSTATUS_REPLY.equals(oldObj.getAuditStatus()))) {
            busServiceItem.setAuditStatus(BusServiceItem.AUDITSTATUS_INIT);
        }
        // 8. 再次强制下架
        busServiceItem.setSaleStatus(BusServiceItem.SALESTATUS_OFF);

        return busServiceItemMapper.updateBusServiceItem(busServiceItem);
    }

    @Override
    public int deleteBusServiceItemById(Long id) {
        if (id == null) {
            throw new RuntimeException("非法参数");
        }
        BusServiceItem busServiceItem = this.selectBusServiceItemById(id);
        if (busServiceItem == null) {
            throw new RuntimeException("非法参数");
        }
        //2.上架的商品不能删除
        if (BusServiceItem.SALESTATUS_ON.equals(busServiceItem.getSaleStatus())) {
            throw new RuntimeException("上架的商品禁止删除");
        }
        //3.若是下架状态且是套餐且状态是审核中禁止删除
        if (BusServiceItem.CARPACKAGE_YES.equals(busServiceItem.getCarPackage()) && BusServiceItem.AUDITSTATUS_AUDITING.equals(busServiceItem.getAuditStatus())) {
            throw new RuntimeException("审核中的商品禁止删除");
        }
        return busServiceItemMapper.deleteBusServiceItemById(id);
    }


    @Override
    public int saleOn(Long id) {
        // 参数合理化校验
        if (id == null) {
            throw new RuntimeException("非法参数");
        }
        BusServiceItem busServiceItem = this.selectBusServiceItemById(id);
        if (busServiceItem == null) {
            throw new RuntimeException("非法参数");
        }
        // - 必须是下架商品才可以进行上架操作。
        if (BusServiceItem.SALESTATUS_ON.equals(busServiceItem.getSaleStatus())) {
            throw new RuntimeException("该服务项已经上架");
        }

        //- 下架且非套餐可以直接上架
        //- 下架且是套餐且状态必须是审核通过才可以进行上架
        //是套餐&&不是审核通过
        if (BusServiceItem.CARPACKAGE_YES.equals
                (busServiceItem.getCarPackage()) && !BusServiceItem.AUDITSTATUS_APPROVED.
                equals(busServiceItem.getAuditStatus())) {
            throw new RuntimeException("该服务项必须审核通过后才可以进行上架操作");
        }
        return busServiceItemMapper.changeSaleStaus(id, BusServiceItem.SALESTATUS_ON);
    }

    @Override
    public int saleOff(Long id) {
        // 参数合理化校验
        if (id == null) {
            throw new RuntimeException("非法参数");
        }
        BusServiceItem busServiceItem = this.selectBusServiceItemById(id);
        if (busServiceItem == null) {
            throw new RuntimeException("非法参数");
        }
        // - 必须是上架商品才可以进行下架操作。
        if (BusServiceItem.SALESTATUS_OFF.equals(busServiceItem.getSaleStatus())) {
            throw new RuntimeException("该服务项已经下架");
        }

        return busServiceItemMapper.changeSaleStaus(id, BusServiceItem.SALESTATUS_OFF);


    }

    @Override
    public Map getAuditInfo(Long id) {
        // 1. 参数校验
        if (id == null) {
            throw new RuntimeException("非法操作");
        }

        // 2. 查询服务项信息
        BusServiceItem busServiceItem = busServiceItemMapper.selectBusServiceItemById(id);
        if (busServiceItem == null) {
            throw new RuntimeException("非法操作");
        }

        // 3. 业务规则验证
        // 判断是否是套餐
        if (!BusServiceItem.CARPACKAGE_YES.equals(busServiceItem.getCarPackage())) {
            throw new RuntimeException("必须是套餐才可以审核");
        }

        // 判断状态是否为初始化
        if (!BusServiceItem.AUDITSTATUS_INIT.equals(busServiceItem.getAuditStatus())) {
            throw new RuntimeException("非法操作");
        }

        // 4. 创建返回的 Map
        Map<String, Object> map = new HashMap<>();

        // 5. 获取折扣价限制配置
        String discountPriceLimitStr = sysConfigService.selectConfigByKey("discountPriceLimit");
        BigDecimal discountPriceLimit = new BigDecimal(discountPriceLimitStr != null ? discountPriceLimitStr : "3000");

        // 6. 查店长，放到map中
        List<SysUser> shopOwnerList = sysUserService.queryByRoleKey("shopOwner");
        map.put("shopOwnerList", shopOwnerList);

        // 7. 查财务，放到map中（根据价格条件判断是否需要）
        // 如果折扣价大于等于限制价格，需要财务审核
        if (busServiceItem.getDiscountPrice().compareTo(discountPriceLimit) >= 0) {
            List<SysUser> financeList = sysUserService.queryByRoleKey("finance");
            map.put("financeList", financeList);
        } else {
            // 不需要财务审核时，返回空列表
            map.put("financeList", new ArrayList<>());
        }

        // 8. 添加其他必要信息
        map.put("serviceItem", busServiceItem);
        map.put("discountPriceLimit", discountPriceLimit);

        return map;
    }

    @Override
    public void startAudit(Long serviceItemId, Long shopOwnerId, Long financeId, String info) {
        //合理性判断
        if (serviceItemId == null || shopOwnerId == null) {
            throw new RuntimeException("非法参数");
        }
        BusServiceItem serviceItem = busServiceItemMapper.selectBusServiceItemById(serviceItemId);
        if (serviceItem == null) {
            throw new RuntimeException("非法参数");
        }
        if (serviceItem.getSaleStatus() == BusServiceItem.SALESTATUS_ON) {
            throw new RuntimeException("已上架的服务不能发起审核");
        }
        if (serviceItem.getCarPackage() == BusServiceItem.CARPACKAGE_NO) {
            throw new RuntimeException("只有套餐可以发起审核");
        }
        if (serviceItem.getAuditStatus() != BusServiceItem.AUDITSTATUS_INIT) {
            throw new RuntimeException("只有初始化状态才能发起审核");
        }
        //添加car_package_audit表的记录
        CarPackageAudit audit = new CarPackageAudit();
        audit.setServiceItemId(serviceItem.getId());
        audit.setServiceItemName(serviceItem.getName());
        audit.setServiceItemInfo(serviceItem.getInfo());
        audit.setServiceItemPrice(serviceItem.getDiscountPrice());
        String userId = SecurityUtils.getUserId().toString();
        audit.setCreatorId(userId);
        audit.setInfo(info);
        audit.setStatus(CarPackageAudit.STATUS_IN_PROGRESS);
        audit.setCreateTime(new Date());
        carPackageAuditMapper.insertCarPackageAudit(audit);
        //到activity中生成流程实例
        BusBpmnInfo bpmnInfo = busBpmnInfoService.getByType(0);
        String processDefinitionKey = bpmnInfo.getProcessDefinitionKey();
        String businessKey = audit.getId().toString();
        Map map = new HashMap();
        map.put("shopOwnerId", shopOwnerId);
        if (financeId != null) {
            map.put("financeId", financeId);
        }
        map.put("disCountPrice", audit.getServiceItemPrice().longValue());
        //发起流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, map);
        //将activity中生成流程实例的id更新到cat_package_audit表中
        String processInstanceId = processInstance.getId();
        audit.setInstanceId(processInstanceId);
        carPackageAuditMapper.updateCarPackageAudit(audit);
        //修改service_item表的状态
        busServiceItemMapper.changeAuditStatus(serviceItemId, BusServiceItem.AUDITSTATUS_AUDITING);


    }


}
