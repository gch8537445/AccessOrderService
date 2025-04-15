package com.ipath.orderflowservice.order.service;

import com.ipath.orderflowservice.order.dao.bean.ComSceneHoliday;
import com.ipath.orderflowservice.order.dao.bean.CompanyPreApprovalSetting;

import java.util.Date;
import java.util.List;

/**
 * @description: 场景service
 * @author: qy
 **/
public interface ComSceneService {

    /**
     * 查询场景 指定日期是否为节假日
     *
     * @param sceneId 场景id
     * @param date
     * @return
     */
    boolean isHoliday(Long sceneId, Date date);

    List<ComSceneHoliday> getSceneHolidayInfo(Long sceneId, Date date);


    List<CompanyPreApprovalSetting> getPreApprovalSettingBySceneIdAndParaCode(Long companyId, Long sceneId, String number);
}
