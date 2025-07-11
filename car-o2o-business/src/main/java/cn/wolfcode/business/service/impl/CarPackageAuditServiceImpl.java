package cn.wolfcode.business.service.impl;

import cn.wolfcode.business.domain.BusBpmnInfo;
import cn.wolfcode.business.domain.BusServiceItem;
import cn.wolfcode.business.dto.AuditDTO;
import cn.wolfcode.business.mapper.BusBpmnInfoMapper;
import cn.wolfcode.business.mapper.BusServiceItemMapper;
import cn.wolfcode.business.vo.HistoryVO;
import cn.wolfcode.common.utils.PageUtils;
import cn.wolfcode.common.utils.SecurityUtils;
import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.*;

import cn.wolfcode.common.utils.DateUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wolfcode.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.business.domain.CarPackageAudit;
import cn.wolfcode.business.service.ICarPackageAuditService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 套餐审核Service业务层处理
 *
 * @author wolfcode
 * @date 2025-07-09
 */
@Transactional
@Service
public class CarPackageAuditServiceImpl implements ICarPackageAuditService {
    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BusBpmnInfoMapper busBpmnInfoMapper;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BusServiceItemMapper serviceItemMapper;

    /**
     * 查询套餐审核
     *
     * @param id 套餐审核主键
     * @return 套餐审核
     */
    @Override
    public CarPackageAudit selectCarPackageAuditById(Long id) {
        return carPackageAuditMapper.selectCarPackageAuditById(id);
    }

    /**
     * 查询套餐审核列表
     *
     * @param carPackageAudit 套餐审核
     * @return 套餐审核
     */
    @Override
    public List<CarPackageAudit> selectCarPackageAuditList(CarPackageAudit carPackageAudit) {
        return carPackageAuditMapper.selectCarPackageAuditList(carPackageAudit);
    }

    /**
     * 新增套餐审核
     *
     * @param carPackageAudit 套餐审核
     * @return 结果
     */
    @Override
    public int insertCarPackageAudit(CarPackageAudit carPackageAudit) {
        carPackageAudit.setCreateTime(DateUtils.getNowDate());
        return carPackageAuditMapper.insertCarPackageAudit(carPackageAudit);
    }

    /**
     * 修改套餐审核
     *
     * @param carPackageAudit 套餐审核
     * @return 结果
     */
    @Override
    public int updateCarPackageAudit(CarPackageAudit carPackageAudit) {
        return carPackageAuditMapper.updateCarPackageAudit(carPackageAudit);
    }

    /**
     * 批量删除套餐审核
     *
     * @param ids 需要删除的套餐审核主键
     * @return 结果
     */
    @Override
    public int deleteCarPackageAuditByIds(Long[] ids) {
        return carPackageAuditMapper.deleteCarPackageAuditByIds(ids);
    }

    /**
     * 删除套餐审核信息
     *
     * @param id 套餐审核主键
     * @return 结果
     */
    @Override
    public int deleteCarPackageAuditById(Long id) {
        return carPackageAuditMapper.deleteCarPackageAuditById(id);
    }


    @Override
    public InputStream getProcessImg(String instanceId) {
        if (instanceId == null) {
            throw new RuntimeException("非法参数");
        }

        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

        BpmnModel bpmnModel;
        List<String> activeActivityIds = new ArrayList<>();  // 当前活动节点
        List<String> highLightedFlows = new ArrayList<>();   // 高亮流程线

        if (processInstance != null) {
            // 流程实例正在运行
            bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

            // 只获取当前活动的节点
            List<Execution> executionList = runtimeService.createExecutionQuery()
                    .processInstanceId(instanceId)
                    .list();

            for (Execution execution : executionList) {
                if (execution.getActivityId() != null) {
                    activeActivityIds.add(execution.getActivityId());
                }
            }
        } else {
            // 流程已结束，不高亮任何节点，或者只高亮结束节点
            BusBpmnInfo bpmnInfo = busBpmnInfoMapper.selectByType(0);
            String processDefinitionKey = bpmnInfo.getProcessDefinitionKey();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processDefinitionKey)
                    .latestVersion()
                    .singleResult();
            bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

            // 对于已结束的流程，可以选择不高亮任何节点
            // 或者只高亮最后一个活动节点
            List<HistoricActivityInstance> lastActivity = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(instanceId)
                    .orderByHistoricActivityInstanceEndTime()
                    .desc()
                    .listPage(0, 1);

            if (!lastActivity.isEmpty()) {
                activeActivityIds.add(lastActivity.get(0).getActivityId());
            }
        }

        InputStream inputStream = generator.generateDiagram(
                bpmnModel,
                activeActivityIds,      // 只高亮当前活动节点
                highLightedFlows,       // 高亮的流程线
                "宋体", "宋体", "宋体"
        );

        return inputStream;
    }

    @Override
    public void processCancel(Long id) {
        if (id == null)
            throw new RuntimeException("非法参数");
        CarPackageAudit audit = carPackageAuditMapper.selectCarPackageAuditById(id);
        if (audit == null) throw new RuntimeException("非法参数");
        if (audit.getStatus() != CarPackageAudit.STATUS_IN_PROGRESS) throw new RuntimeException("只能撤销审核中的请求");
        runtimeService.deleteProcessInstance(audit.getInstanceId(), "用户主动撤销审核");
        audit.setStatus(CarPackageAudit.STATUS_CANCEL);
        carPackageAuditMapper.updateCarPackageAudit(audit);
        serviceItemMapper.changeAuditStatus(audit.getServiceItemId(), BusServiceItem.AUDITSTATUS_INIT);


    }

    @Override
    public List<HistoryVO> getHistoryVOList(String instanceId) {
        if (instanceId == null) {
            throw new RuntimeException("非法参数");
        }
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(instanceId)
                .list();
        List<HistoryVO> vos = new ArrayList<>();
        for (HistoricTaskInstance historicTaskInstance : list) {
            HistoryVO vo = new HistoryVO();
            vo.setTaskName(historicTaskInstance.getName());
            vo.setStartTime(historicTaskInstance.getStartTime().toLocaleString());
            Date end = historicTaskInstance.getEndTime();
            vo.setEndTime(end == null ? "" : end.toLocaleString());
            Long durationInMillis = historicTaskInstance.getDurationInMillis();
            vo.setDurationInMillis(durationInMillis == null ? "" : durationInMillis / 1000 + "");
            List<Comment> taskComments = taskService.getTaskComments(historicTaskInstance.getId());
            if (taskComments != null && taskComments.size() > 0) {
                String comment = taskComments.get(0).getFullMessage();
                vo.setComment(comment);
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public void audit(AuditDTO dto) {
        //合法性判断
        if (dto == null) {
            throw new RuntimeException("非法参数,dto对象为null");
        }
        if (dto.getId() == null || dto.getAuditStatus() == null) {
            throw new RuntimeException("非法参数");
        }
        CarPackageAudit audit = carPackageAuditMapper.selectCarPackageAuditById(dto.getId());
        if (audit == null) {
            throw new RuntimeException("非法参数");
        }
        if (audit.getStatus() != CarPackageAudit.STATUS_IN_PROGRESS) {
            throw new RuntimeException("只能对审核中状态的申请发起审核");
        }
        Long userId = SecurityUtils.getUserId();
        Task task = taskService.createTaskQuery()
                .processInstanceId(audit.getInstanceId())
                .taskAssignee(userId.toString())
                .singleResult();
        if (task == null) {
            throw new RuntimeException("未查询到要审批的任务,审核失败");
        }
        String username = SecurityUtils.getUsername();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<br>")
                .append("[")
                .append(username)
                .append("]")
                .append(dto.getAuditStatus() == 2 ? "审核通过" : "审核拒绝")
                .append("。")
                .append(dto.getInfo());
        audit.setInfo(audit.getInfo() + stringBuffer.toString());

        taskService.addComment(task.getId(), audit.getInstanceId(), stringBuffer.toString());
        Map<String, Object> params = new HashMap<>();
        params.put("shopOwner", dto.getAuditStatus() == 2 ? true : false);
        //当前登录用户审批完成
        taskService.complete(task.getId(), params);
        if (dto.getAuditStatus() == 1) {
            //审批拒绝
            //修改serviceItem表的审核状态
            serviceItemMapper.changeAuditStatus(audit.getServiceItemId(), BusServiceItem.AUDITSTATUS_INIT);
            audit.setStatus(CarPackageAudit.STATUS_REJECT);
            carPackageAuditMapper.updateCarPackageAudit(audit);
            //修改car_packageAudit表的审核状态
        } else {
            //判断有没有下一个任务
            List<Task> list = taskService.createTaskQuery()
                    .processInstanceId(audit.getInstanceId())
                    .list();
            if (list != null && list.size() > 0) {
                carPackageAuditMapper.updateCarPackageAudit(audit);
            } else {
                //没有下一个任务,修改serviceItem表的审核状态 修改car_packageAudit表的审核状态
                serviceItemMapper.changeAuditStatus(audit.getServiceItemId(), BusServiceItem.AUDITSTATUS_APPROVED);
                audit.setStatus(CarPackageAudit.STATUS_PASS);
                carPackageAuditMapper.updateCarPackageAudit(audit);
            }
        }

    }

    @Override
    public List<CarPackageAudit> selectDoneTaskList(CarPackageAudit carPackageAudit) {
        carPackageAudit.getParams().put("userId", SecurityUtils.getUsername());
        return carPackageAuditMapper.selectCarPackageAuditList(carPackageAudit);
    }

    @Override
    public List<CarPackageAudit> selectTodoTaskList(CarPackageAudit carPackageAudit) {
        //当前登录用户的id
        Long userId = SecurityUtils.getUserId();
        BusBpmnInfo bpmnInfo = busBpmnInfoMapper.selectByType(0);
        if (bpmnInfo == null) {
            throw new RuntimeException("非法参数");
        }
        String processDefinitionKey = bpmnInfo.getProcessDefinitionKey();
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskAssignee(userId.toString())
                .list();
        if (taskList == null || taskList.size() == 0) {
            //没有审批任务,直接返回空集合
            return Collections.emptyList();
        }
        List<String> instanceIdList = new ArrayList<>();
        for (Task task : taskList) {
            instanceIdList.add(task.getProcessInstanceId());
        }
//
        PageUtils.startPage();
        if (instanceIdList != null && instanceIdList.size() > 0) {
            carPackageAudit.getParams().put("processInstanceList", instanceIdList);
        }
        List<CarPackageAudit> auditList = carPackageAuditMapper.selectCarPackageAuditList(carPackageAudit);
        return auditList;
    }
}
