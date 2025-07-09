package cn.wolfcode.business.mapper;

import java.util.List;
import cn.wolfcode.business.domain.CarPackageAudit;

/**
 * 套餐审核Mapper接口
 * 
 * @author wolfcode
 * @date 2025-07-09
 */
public interface CarPackageAuditMapper 
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
     * 删除套餐审核
     * 
     * @param id 套餐审核主键
     * @return 结果
     */
    public int deleteCarPackageAuditById(Long id);

    /**
     * 批量删除套餐审核
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCarPackageAuditByIds(Long[] ids);
}
