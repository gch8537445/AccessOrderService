package com.ipath.orderflowservice.order.dao;


import com.ipath.orderflowservice.order.dao.bean.CompanyPreApprovalSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CompanyPreApprovalSettingMapper {

    List<CompanyPreApprovalSetting> getPreApprovalSettingBySceneIdAndParaCode(@Param("companyId") Long companyId, @Param("sceneId") Long sceneId, @Param("paraCode") String paraCode);
}