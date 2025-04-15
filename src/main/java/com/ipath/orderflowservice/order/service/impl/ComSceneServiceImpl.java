package com.ipath.orderflowservice.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import com.ipath.orderflowservice.order.dao.ComSceneHolidayMapper;
import com.ipath.orderflowservice.order.dao.CompanyPreApprovalSettingMapper;
import com.ipath.orderflowservice.order.dao.bean.ComSceneHoliday;
import com.ipath.orderflowservice.order.dao.bean.CompanyPreApprovalSetting;
import com.ipath.orderflowservice.order.service.ComSceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 场景service
 * @author: qy
 **/
@Service
@Slf4j
public class ComSceneServiceImpl implements ComSceneService {


    @Resource
    private ComSceneHolidayMapper comSceneHolidayMapper;

    @Resource
    private RemoteCallFeign remoteCallFeign;

    @Resource
    private CompanyPreApprovalSettingMapper companyPreApprovalSettingMapper;




    @Override
    public boolean isHoliday(Long sceneId, Date date) {

        // 默认 周六周日 休息
        boolean result = DateUtil.isWeekend(date);

        // 获取 运营端修改过的企业日期
        RemoteCallDto dto = new RemoteCallDto();
        dto.setPath("/api/v2/systemcore/Holiday/GetHolidays");
        JSONObject param = new JSONObject();
        param.put("beginYear", DateUtil.year(date));
        dto.setContent(param.toJSONString());
        BaseResponse call = remoteCallFeign.call(dto);
        if (call.getCode() != 0) {
            log.error("获取企业holiday失败 => {},  param: {}", call, dto);
        }else {
            Object callData = call.getData();
            List<ComSceneHoliday> list = JSONUtil.toList(JSONUtil.parseArray(callData), ComSceneHoliday.class);
            List<ComSceneHoliday> sameDayList = list.stream().filter(item -> DateUtil.isSameDay(date, item.getDay())).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(sameDayList)) {
                long holidayCount = sameDayList.stream().filter(item -> item.getDayType() == 1).count();
                long workCount = sameDayList.stream().filter(item -> item.getDayType() == 2).count();
                if (workCount > 0){
                    result = false;
                }

                if (holidayCount > 0){
                    result = true;
                }
            }
        }

        List<ComSceneHoliday> list = getSceneHolidayInfo(sceneId, date);
        List<ComSceneHoliday> sameDayList = list.stream().filter(item -> DateUtil.isSameDay(date, item.getDay())).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(sameDayList)) {
            long holidayCount = sameDayList.stream().filter(item -> item.getDayType() == 1).count();
            long workCount = sameDayList.stream().filter(item -> item.getDayType() == 2).count();
            if (workCount > 0){
                result = false;
            }

            if (holidayCount > 0){
                result = true;
            }
        }
        return result;
    }

    @Override
    public List<ComSceneHoliday> getSceneHolidayInfo(Long sceneId, Date date) {
        ComSceneHoliday comSceneHoliday = new ComSceneHoliday();
        comSceneHoliday.setSceneId(sceneId);
        comSceneHoliday.setYear(DateUtil.year(date));
        return comSceneHolidayMapper.list(comSceneHoliday);
    }

    @Override
    public List<CompanyPreApprovalSetting> getPreApprovalSettingBySceneIdAndParaCode(Long companyId, Long sceneId, String number) {
        return companyPreApprovalSettingMapper.getPreApprovalSettingBySceneIdAndParaCode(companyId,sceneId,number);
    }


}
