package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.Admin;
import com.ipath.orderflowservice.order.dao.vo.PreDepartApplyVo;

public interface AdminMapper {
    Admin selectByCompanyId(Long companyId);

    PreDepartApplyVo selectPreDepartApplyById(Long id);
}