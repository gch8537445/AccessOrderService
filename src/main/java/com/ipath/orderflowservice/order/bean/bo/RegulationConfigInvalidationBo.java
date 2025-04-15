package com.ipath.orderflowservice.order.bean.bo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 缓存失效配置
 */
@Data
public class RegulationConfigInvalidationBo {


    /**
     * 触发失效订单状态
     */
    @JSONField(name="status")
    private List<Short> status;

    /**
     * 失效提示语
     */
    @JSONField(name="prompt")
    private String prompt;
}
