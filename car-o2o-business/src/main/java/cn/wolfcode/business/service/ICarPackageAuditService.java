package cn.wolfcode.business.service;

import java.io.InputStream;
import java.util.List;
import cn.wolfcode.business.domain.CarPackageAudit;
import cn.wolfcode.business.dto.AuditDTO;
import cn.wolfcode.business.vo.HistoryVO;

/**
 * 套餐审核Service接口
 *
 * @author wolfcode
 * @date 2025-07-09
 */
public interface ICarPackageAuditService
{
    /**
     * 查询套餐审核
     *
     * @param id 套餐审核主键
     * @return 套餐审核
     */
    public CarPackageAudit selectCarPackageAuditById(Long id);

    /**
     * 查询套餐审核列表
     *
     * @param carPackageAudit 套餐审核
     * @return 套餐审核集合
     */
    public List<CarPackageAudit> selectCarPackageAuditList(CarPackageAudit carPackageAudit);

    /**
     * 新增套餐审核
     *
     * @param carPackageAudit 套餐审核
     * @return 结果
     */
    public int insertCarPackageAudit(CarPackageAudit carPackageAudit);

    /**
     * 修改套餐审核
     *
     * @param carPackageAudit 套餐审核
     * @return 结果
     */
    public int updateCarPackageAudit(CarPackageAudit carPackageAudit);

    /**
     * 批量删除套餐审核
     *
     * @param ids 需要删除的套餐审核主键集合
     * @return 结果
     */
    public int deleteCarPackageAuditByIds(Long[] ids);

    /**
     * 删除套餐审核信息
     *
     * @param id 套餐审核主键
     * @return 结果
     */
    public int deleteCarPackageAuditById(Long id);

    InputStream getProcessImg(String instanceId);

    List<CarPackageAudit> selectTodoTaskList(CarPackageAudit carPackageAudit);

    List<CarPackageAudit> selectDoneTaskList(CarPackageAudit carPackageAudit);

    void audit(AuditDTO dto);

    List<HistoryVO> getHistoryVOList(String instanceId);

    void processCancel(Long id);
}
