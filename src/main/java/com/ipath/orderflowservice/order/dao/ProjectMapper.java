package com.ipath.orderflowservice.order.dao;

import java.util.List;

import com.ipath.orderflowservice.order.bean.param.JinduApproverParam;
import com.ipath.orderflowservice.order.bean.vo.JinduCostCenterVo;
import com.ipath.orderflowservice.order.bean.vo.UserBaseInfoVo;
import com.ipath.orderflowservice.order.dao.bean.Project;
import com.ipath.orderflowservice.order.dao.param.JinduProjectDaoParam;
import org.apache.ibatis.annotations.Param;

public interface ProjectMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Project record);

    int insertSelective(Project record);

    Project selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Project record);

    int updateByPrimaryKey(Project record);

    List<Project> selectProjects(Project project);

    /**
     * 查询人员对应的成本中心
     * @param userId
     * @param companyId
     * @return
     */
    List<JinduCostCenterVo> selectJinduCostCenter(@Param("userId") Long userId, @Param("companyId") Long companyId);

    /**
     * 查询成本中心
     * @param param
     * @return
     */
    List<JinduCostCenterVo> getJinduCostCenter(JinduProjectDaoParam param);

    /**
     * 查询 成本中心审批人
     * @param param
     * @return
     */
    List<UserBaseInfoVo> getJinduApprover(JinduApproverParam param);

    /**
     * 查询 案件
     * @param bean
     * @return
     */
    List<JinduCostCenterVo> getJinduCase(JinduProjectDaoParam bean);
}