package com.ipath.orderflowservice.order.service.impl;

import com.ipath.orderflowservice.order.bean.vo.CompanyRePlaceConfigVo;
import com.ipath.orderflowservice.order.bean.vo.CompanyRePlaceUpgradeConfigVo;
import com.ipath.orderflowservice.order.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


/***
 * 订单重新派单 ServiceImpl
 */
@Slf4j
public class OrderRePlaceServiceImpl implements OrderRePlaceService {

    @Autowired
    private CompanyService companyService;

    /**
     * 重新派单 无司机接单
     */
    @Override
    public void rePlaceForNoDriver(Long companyId){
        //企业自动重新派单配置
        CompanyRePlaceConfigVo rePlaceConfig = companyService.getCompanyRePlaceConfig(companyId);
        //重新派车自动升舱配置
        CompanyRePlaceUpgradeConfigVo rePlaceUpgradeConfig = companyService.getCompanyRePlaceUpgradeConfig(companyId);

    }


    /**
     * 重新派单 core端取消
     */
    @Override
    public void rePlaceForCoreCancel(Long companyId){
        //企业自动重新派单配置
        CompanyRePlaceConfigVo rePlaceConfig = companyService.getCompanyRePlaceConfig(companyId);
        //重新派车自动升舱配置
        CompanyRePlaceUpgradeConfigVo rePlaceUpgradeConfig = companyService.getCompanyRePlaceUpgradeConfig(companyId);

    }


    /**
     * 判断是否增加车型
     * @return
     */
    public boolean isAppendCarType(Long companyId){
        //企业自动重新派单配置
        CompanyRePlaceConfigVo rePlaceConfig = companyService.getCompanyRePlaceConfig(companyId);
        //重新派车自动升舱配置
        CompanyRePlaceUpgradeConfigVo rePlaceUpgradeConfig = companyService.getCompanyRePlaceUpgradeConfig(companyId);
        return true;
    }

}