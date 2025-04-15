package com.ipath.orderflowservice.order.service;


import com.ipath.orderflowservice.order.bean.bo.RegulationBo;
import com.ipath.orderflowservice.order.bean.bo.RegulationConfigBo;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;

public interface RegulationService {


    boolean isInvalidationCompany(Long companyId);

    RegulationConfigBo invalidationCompanyConfig(Long companyId);

    boolean isInvalidationCompany(Long companyId, Short status);

    RegulationBo getRegulationConfig();

    RegulationBo reRegulationConfig();

    boolean isErrorByCode(CreateOrderParam orderParam, Integer code);
}
