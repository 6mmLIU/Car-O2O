package cn.wolfcode.business.service;

import java.io.IOException;
import java.util.List;
import cn.wolfcode.business.domain.BusBpmnInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

/**
 * 流程定义明细Service接口
 *
 * @author wolfcode
 * @date 2025-07-07
 */
public interface IBusBpmnInfoService
{
    /**
     * 查询流程定义明细
     *
     * @param id 流程定义明细主键
     * @return 流程定义明细
     */
    public BusBpmnInfo selectBusBpmnInfoById(Long id);

    /**
     * 查询流程定义明细列表
     *
     * @param busBpmnInfo 流程定义明细
     * @return 流程定义明细集合
     */
    public List<BusBpmnInfo> selectBusBpmnInfoList(BusBpmnInfo busBpmnInfo);

    /**
     * 新增流程定义明细
     *
     * @param busBpmnInfo 流程定义明细
     * @return 结果
     */
    public int insertBusBpmnInfo(BusBpmnInfo busBpmnInfo);

    /**
     * 修改流程定义明细
     *
     * @param busBpmnInfo 流程定义明细
     * @return 结果
     */
    public int updateBusBpmnInfo(BusBpmnInfo busBpmnInfo);

    /**
     * 批量删除流程定义明细
     *
     * @param ids 需要删除的流程定义明细主键集合
     * @return 结果
     */
    public int deleteBusBpmnInfoByIds(Long[] ids);

    /**
     * 删除流程定义明细信息
     *
     * @param id 流程定义明细主键
     * @return 结果
     */
    public int deleteBusBpmnInfoById(Long id);

    /**
     *新增流程定义明细(部署)
     */
    int deploy(MultipartFile file, Long bpmnType, String info) throws IOException;

    Object getResource(Long id,String type);

    int cancel(@Param("id") Long id);

    BusBpmnInfo getByType(int type);
}
