package cn.wolfcode.business.service.impl;

import org.activiti.bpmn.model.BpmnModel;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.wolfcode.common.utils.DateUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.image.impl.DefaultProcessDiagramCanvas;
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
//TODO:2025/7/8 当审批完成后,需要验证是否正常
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        //List<String> collect=Stream.of("sid-xxxxxxxxxxxxx").collect(Collectors.toList());
        List<Execution> executionList = runtimeService.createExecutionQuery()
                .processInstanceId(processInstance.getProcessInstanceId())
                .list();
        List<String> list=new ArrayList<>();
        for (Execution execution:executionList){
            String activityId=execution.getActivityId();
            list.add(activityId);
        }
        InputStream inputStream = generator.generateDiagram(bpmnModel, list, Collections.emptyList(),
                "宋体", "宋体", "宋体");
        return inputStream;
    }
}
