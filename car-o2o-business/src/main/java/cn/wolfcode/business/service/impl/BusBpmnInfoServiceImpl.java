package cn.wolfcode.business.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.activiti.image.impl.DefaultProcessDiagramGenerator;

import cn.wolfcode.common.utils.StringUtils;
import cn.wolfcode.common.utils.file.FileUploadUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import cn.wolfcode.business.mapper.BusBpmnInfoMapper;
import cn.wolfcode.business.domain.BusBpmnInfo;
import cn.wolfcode.business.service.IBusBpmnInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 流程定义明细Service业务层处理
 *
 * @author wolfcode
 * @date 2025-07-07
 */
@Service
@Transactional
public class BusBpmnInfoServiceImpl implements IBusBpmnInfoService {
    @Autowired
    private BusBpmnInfoMapper busBpmnInfoMapper;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 查询流程定义明细
     *
     * @param id 流程定义明细主键
     * @return 流程定义明细
     */
    @Override
    public BusBpmnInfo selectBusBpmnInfoById(Long id) {
        return busBpmnInfoMapper.selectBusBpmnInfoById(id);
    }

    /**
     * 查询流程定义明细列表
     *
     * @param busBpmnInfo 流程定义明细
     * @return 流程定义明细
     */
    @Override
    public List<BusBpmnInfo> selectBusBpmnInfoList(BusBpmnInfo busBpmnInfo) {
        return busBpmnInfoMapper.selectBusBpmnInfoList(busBpmnInfo);
    }

    /**
     * 新增流程定义明细
     *
     * @param busBpmnInfo 流程定义明细
     * @return 结果
     */
    @Override
    public int insertBusBpmnInfo(BusBpmnInfo busBpmnInfo) {
        return busBpmnInfoMapper.insertBusBpmnInfo(busBpmnInfo);
    }

    /**
     * 修改流程定义明细
     *
     * @param busBpmnInfo 流程定义明细
     * @return 结果
     */
    @Override
    public int updateBusBpmnInfo(BusBpmnInfo busBpmnInfo) {
        return busBpmnInfoMapper.updateBusBpmnInfo(busBpmnInfo);
    }

    /**
     * 批量删除流程定义明细
     *
     * @param ids 需要删除的流程定义明细主键
     * @return 结果
     */
    @Override
    public int deleteBusBpmnInfoByIds(Long[] ids) {
        return busBpmnInfoMapper.deleteBusBpmnInfoByIds(ids);
    }

    /**
     * 删除流程定义明细信息
     *
     * @param id 流程定义明细主键
     * @return 结果
     */
    @Override
    public int deleteBusBpmnInfoById(Long id) {
        return busBpmnInfoMapper.deleteBusBpmnInfoById(id);
    }

    /**
     * 新增流程定义明细(部署)
     */
    @Override
    @Transactional
    public int deploy(MultipartFile file, Long bpmnType, String info) throws IOException {
        //参数合理化验证
        if (file == null || bpmnType == null || info == null) {
            throw new RuntimeException("非法参数");
        }
        //验证文件格式
        String originalFilename = file.getOriginalFilename();
        //根据文件后缀名提取后缀名
        String extension = FileUploadUtils.getExtension(file);
        if (!("xml".equalsIgnoreCase(extension) || "bpmn".equalsIgnoreCase(extension))) {
            throw new RuntimeException("上传文件格式必须是XML或者BPMN");
        }
        // 使用 Activiti7 实现部署操作。得到部署对象

        Deployment deploy = repositoryService.createDeployment()
                .addInputStream(originalFilename, file.getInputStream())
                .deploy();

// 我们就可以根据部署 ID 查询流程定义对象（我们一般都是一次部署对应一个流程定义）。
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploy.getId())
                .latestVersion()
                .singleResult();
        //删除较低版本bpmn数据,0表示服务套餐审核
        busBpmnInfoMapper.deleteBusBpmnInfoByType(0);
        //创建bpmn对象,向其内插入数据
        BusBpmnInfo busBpmnInfo = new BusBpmnInfo();
        busBpmnInfo.setBpmnLabel(originalFilename);
        busBpmnInfo.setBpmnType(bpmnType);
        busBpmnInfo.setProcessDefinitionKey(processDefinition.getKey());
        busBpmnInfo.setDeployTime(deploy.getDeploymentTime());
        busBpmnInfo.setVersion(processDefinition.getVersion());
        busBpmnInfo.setInfo(info);
// 通过流程定义对象就可以拿到流程定义相关信息。ProcessDefinitionKey 和 version。
// 最终将数据整合插入到 BpmnInfo 表中。
        return busBpmnInfoMapper.insertBusBpmnInfo(busBpmnInfo);

    }

    //id表示当前bpmn info的id
    @Override
    public InputStream getResource(Long id, String type) {
        if (id == null || StringUtils.isBlank(type)) {
            throw new RuntimeException("非法参数");
        }
        BusBpmnInfo bpmnInfo = busBpmnInfoMapper.selectBusBpmnInfoById(id);
        if (bpmnInfo == null) {
            throw new RuntimeException("非法参数");
        }
        //查到activity关联的流程定义的key
        String processDefinitionKey = bpmnInfo.getProcessDefinitionKey();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult();
        if ("xml".equals(type)) {
            //查询的是bpmn文件
            String resourceName = processDefinition.getResourceName();
            String deploymentId = processDefinition.getDeploymentId();
            //查询资源文件内容
            InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
            return inputStream;
        } else {
            //查询的是png文件
            //bpmn的所有数据
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            //图片生成器对象
            DefaultProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();

            InputStream inputStream = processDiagramGenerator.generateDiagram(bpmnModel,
                    Collections.emptyList()
                    , Collections.emptyList()
                    , "宋体",
                    "宋体",
                    "宋体");
            return inputStream;

        }

    }

    @Override
    public int cancel(Long id) {
        if (id == null) {
            throw new RuntimeException("非法参数");
        }
        BusBpmnInfo bpmnInfo = busBpmnInfoMapper.selectBusBpmnInfoById(id);
        if (bpmnInfo == null) {
            throw new RuntimeException("非法参数");
        }
        String processDefinitionKey = bpmnInfo.getProcessDefinitionKey();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .list();
        for (ProcessDefinition processDefinition : list) {
            String deploymentId = processDefinition.getDeploymentId();
            repositoryService.deleteDeployment(deploymentId, true);
        }
        int i = busBpmnInfoMapper.deleteBusBpmnInfoById(id);
        return i;
    }

    @Override
    public BusBpmnInfo getByType(int type) {
        return busBpmnInfoMapper.selectByType(type);
    }
}
