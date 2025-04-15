package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.CompanyHolidaysConfig;

import java.util.List;

public interface CompanyHolidaysConfigMapper {

    List<CompanyHolidaysConfig> selectCompanyHolidaysConfig(CompanyHolidaysConfig record);
}