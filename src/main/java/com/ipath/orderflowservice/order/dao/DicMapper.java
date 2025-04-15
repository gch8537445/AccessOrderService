package com.ipath.orderflowservice.order.dao;

import com.ipath.orderflowservice.order.dao.bean.Dic;

import java.util.List;

public interface DicMapper {
    List<Dic> selectByCategory(String category);
}