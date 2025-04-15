package com.ipath.orderflowservice.order.bean.vo;

import com.ipath.orderflowservice.feignclient.dto.ReimModel;
import lombok.Data;

import java.util.List;

@Data
public class SceneInfo {
    private SceneBaseInfo scene;                 // 场景基本信息，要进redis缓存
    private List<ReimModel> reimModel;           // 车型报销策略
    private List<Integer> availableCarLevels;    // 允许的车型列表
    private List<Integer> availableCarSources;   // 允许的平台列表
}
