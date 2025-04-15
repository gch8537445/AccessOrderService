package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.ComSceneHoliday;

import java.util.List;

/**
 * @description: 场景节假日
 * @author: qy
 **/

public interface ComSceneHolidayMapper {

    List<ComSceneHoliday> list(ComSceneHoliday param);
}
