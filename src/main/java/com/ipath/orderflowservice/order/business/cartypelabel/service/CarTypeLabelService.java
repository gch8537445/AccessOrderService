package com.ipath.orderflowservice.order.business.cartypelabel.service;

import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.bean.vo.CacheCarTypeLabelOrder;
import com.ipath.orderflowservice.order.bean.vo.CacheEstimateResult;
import com.ipath.orderflowservice.order.bean.vo.EstimateCar;
import com.ipath.orderflowservice.order.bean.vo.EstimatePriceResult;
import com.ipath.orderflowservice.order.business.cartypelabel.bean.vo.*;

import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @author: qy
 * @create: 2024-10-15 14:39
 **/
public interface CarTypeLabelService {

    /**
     * 获取符合标签车型预估条件的用户画像信息
     * @param userId 用户id
     * @return
     */
    List<TemplateVo> getUserCarTypeLabelEstimate(Long userId);

    List<TemplateVo> getUserCarTypeLabelEstimateNew(Long userId, Long companyId);

    List<CacheCarTypeLabelOrder> getUserCarTypeLabelFinish(Long userId);

    List<CacheCarTypeLabelOrder> getUserCarTypeLabelFinishNew(Long userId, Long companyId);



    /**
     * 获取标签车型预估列表
     * @param orderParam 预估参数
     * @param estimateResponses 模板信息
     * @param carList 预估运力列表
     * @return
     */
    Set<CarTypeLabelEstimateVo> getEstimateResponseNew(CreateOrderParam orderParam,List<CarTypeLabelVo> carTypeLabelEnable, List<TemplateVo> estimateResponses, List<CacheEstimateResult> carList);

    List<CarBaseVo> getBaseInfo();

    List<CarSourceVo> getSourceInfo();

    List<CarLevelVo> getCarLevelInfo();

    List<CarTypeLabelCityMapping> getCityCarTypeLabelEstimate(Long companyId);

    boolean checkCityMapping(CreateOrderParam orderParam, EstimatePriceResult estimateCar, List<CarTypeLabelCityMapping> cityMapping);

    List<CarTypeLabelVo> getCarTypeLabelEnable(Long companyId);
}
