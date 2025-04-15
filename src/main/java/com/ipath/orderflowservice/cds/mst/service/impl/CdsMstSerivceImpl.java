package com.ipath.orderflowservice.cds.mst.service.impl;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.bean.CustomException;
import com.ipath.orderflowservice.cds.mst.service.CdsMstSerice;
import com.ipath.orderflowservice.core.mdm.service.PoiService;
import com.ipath.orderflowservice.core.tencent.bean.TrafficInfo;
import com.ipath.orderflowservice.core.tencent.service.CoreTencentService;
import com.ipath.orderflowservice.feignclient.CdsMstFeign;
import com.ipath.orderflowservice.feignclient.dto.CdsMstDto;
import com.ipath.orderflowservice.log.bean.Log;
import com.ipath.orderflowservice.log.service.LogService;
import com.ipath.orderflowservice.order.bean.RedisCpol;
import com.ipath.orderflowservice.order.bean.constant.OrderConstant;
import com.ipath.orderflowservice.order.bean.param.CreateOrderParam;
import com.ipath.orderflowservice.order.dao.OrderBaseMapper;
import com.ipath.orderflowservice.order.dao.OrderSourceMapper;
import com.ipath.orderflowservice.order.service.OrderLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class CdsMstSerivceImpl implements CdsMstSerice {

    @Autowired
    private CdsMstFeign cdsMstFeign;
    @Autowired
    private OrderLimitService orderLimitService;
    @Autowired
    private CoreTencentService coreTencentService;
    @Override
    public BaseResponse filterCarType(CdsMstDto req, CreateOrderParam orderParam) throws Exception {
        BaseResponse response = null;
        //日志(创建参数)
        //Log log2 = this.getFilterCarTypeLog(orderParam.getCompanyId(), orderParam.getUserId(), orderParam.getOrderId());
        //日志(请求参数)
        //log2.setBody(JSONUtil.toJsonStr(req));
        try {
//            String departStationType = poiService.getStationTypeByTpoiId(orderParam.getDepartPoi());
//            String destStationType = poiService.getStationTypeByTpoiId(orderParam.getDestPoi());
//            req.getOrderParam().setDepartStationType(departStationType == null ? "0": departStationType);
//            req.getOrderParam().setDestStationType(destStationType == null ? "0": destStationType);
            response = cdsMstFeign.filterCarType(req);
        } catch (Exception e) {
            //日志(保存错误)
            //logService.saveErrorLogAsync(log2, e, new Date());
            log.error("filterCarType ===> {}", e);
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }
        if (response.getCode() != 0) {
            //日志(保存警告)
            //logService.saveWarningLogAsync(log2, JSONUtil.toJsonStr(response), new Date());
            throw new BusinessException(response.getMessage());
        }
        //日志(保存正常)
        //logService.saveLogAsync(log2, JSONUtil.toJsonStr(response), new Date());
        return response;
    }

    private Log getFilterCarTypeLog(Long companyId, Long userId, Long orderId) {
        Log log = new Log();
        log.setCompanyId(companyId);
        log.setUserId(userId);
        log.setOrderId(orderId);
        log.setInterfaceName("预估");
        log.setLogName("【请求mst服务】车型过滤");
        log.setExternalSystemInterfaceName("mst车型过滤");
        log.setExternalSystemInterfacePaht("ipathCall/regulation/filterCarType");
        log.setType("3");
        log.setMethod("Feign");
        log.setCreatedTime(new Date());
        return log;
    }

    @Override
    public BaseResponse createOrder(CdsMstDto req, CreateOrderParam orderParam) throws Exception {
        BaseResponse response = null;
        //日志(创建参数)
        //Log log1 = this.getCreateOrderLog(orderParam.getCompanyId(), orderParam.getUserId(), orderParam.getOrderId());
        //日志(请求参数)
        //log1.setBody(JSONUtil.toJsonStr(req));
        try {
            RedisCpol redisCpol = orderLimitService.getRedisCpol(orderParam.getCompanyId(), orderParam.getUserId());
            JSONObject customInfo = redisCpol.getCustomInfo();
            log.info("createOrder==>{}", customInfo.toString());
            if (null != customInfo
                    && null != customInfo.getStr("tripSceneType")
                    && (StringUtils.equals(customInfo.getStr("tripSceneType"), "5") || StringUtils.equals(customInfo.getStr("tripSceneType"), "6"))
                    ) {
                TrafficInfo trafficInfoByPoint1 = coreTencentService.getTrafficInfoByPoint(orderParam.getDepartLat(), orderParam.getDepartLng());
                String code1 = trafficInfoByPoint1.getCode();
                if (StringUtils.equals(code1, "1") || StringUtils.equals(code1, "2")) {
                    req.getOrderParam().setDepartStationType(code1);
                } else {
                    req.getOrderParam().setDepartStationType("0");
                }
                TrafficInfo trafficInfoByPoint2 = coreTencentService.getTrafficInfoByPoint(orderParam.getDestLat(), orderParam.getDestLng());
                String code2 = trafficInfoByPoint2.getCode();
                if (StringUtils.equals(code2, "1") || StringUtils.equals(code2, "2")) {
                    req.getOrderParam().setDestStationType(code2);
                } else {
                    req.getOrderParam().setDestStationType("0");
                }
            }


            response = cdsMstFeign.createOrder(req);
        } catch (Exception e) {
            //日志(保存错误)
            //logService.saveErrorLogAsync(log1, e, new Date());
            throw new BusinessException(OrderConstant.ERORR_SYSTEM_BUSY);
        }
        if (response.getCode() != 0) {
            if (response.getCode() == -21001) {
                throw new CustomException(response.getCode(), response.getMessage(),response.getData());
            }
            //日志(保存警告)
            //logService.saveWarningLogAsync(log1, JSONUtil.toJsonStr(response), new Date());
            throw new BusinessException(response.getMessage());
        }

        JSONObject msgJSONObject = new JSONObject(response.getData());
        String supplyOrderId = msgJSONObject.getStr("supplyOrderId");

        if(StringUtils.isEmpty(supplyOrderId)){
            String jumpUrl = "";
            JSONObject blockWindowDTO = msgJSONObject.getJSONObject("blockWindowDTO");
            JSONArray buttonShowList = blockWindowDTO.getJSONArray("buttonShowList");
            String text = blockWindowDTO.getStr("text");
            for (int i = 0; i < buttonShowList.size(); i++) {
                JSONObject jsonObject = JSONUtil.parseObj(buttonShowList.get(i));
                Boolean needJump = jsonObject.getBool("needJump");
                if(needJump){
                    jumpUrl = jsonObject.getStr("jumpUrl");
                    break;
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("forwardUrl",jumpUrl);
            jsonObject.set("fprwardBtnText",text);
            throw new BusinessException(text);
            //throw new CustomException(15654, text, jsonObject);

        }



        //日志(保存正常)
        //logService.saveLogAsync(log1, JSONUtil.toJsonStr(response), new Date());
        return response;
    }

    private Log getCreateOrderLog(Long companyId, Long userId, Long orderId) {
        Log log = new Log();
        log.setCompanyId(companyId);
        log.setUserId(userId);
        log.setOrderId(orderId);
        log.setInterfaceName("下单");
        log.setLogName("【请求mst服务】提单管控校验");
        log.setExternalSystemInterfaceName("mst提单管控校验");
        log.setExternalSystemInterfacePaht("ipathCall/regulation/createOrder");
        log.setType("3");
        log.setMethod("Feign");
        log.setCreatedTime(new Date());
        return log;
    }

    @Override
    public BaseResponse getPayDetail(CdsMstDto cdsMstDto) throws Exception  {
        BaseResponse response = cdsMstFeign.getPayDetail(cdsMstDto);
        return response;
    }

    @Override
    public BaseResponse ordePay(CdsMstDto cdsMstDto) throws Exception  {
        BaseResponse response = null;
        try {
            Long orderId = cdsMstDto.getOrderId();
//            OrderBase orderBase = orderBaseMapper.selectByPrimaryKey(orderId);
//            OrderSource orderSource = orderSourceMapper.selectByOrderId(orderId);
//            if (orderBase.getCustomInfo() != null) {
//                JSONObject customJSONObject = new JSONObject(orderBase.getCustomInfo());
//                List<SelectedCar> dataBaseSelectedCars = null;
//                if (customJSONObject != null) {
//                    if (customJSONObject.containsKey("selectedCars")) {
//                        dataBaseSelectedCars = customJSONObject.getBeanList("selectedCars", SelectedCar.class);
//                    }
//                }
//
//                List<SelectedCar> collect = dataBaseSelectedCars.stream().filter(o -> o.getCarLevel() == orderSource.getCarLevel() && StringUtils.equals(o.getCarSourceId() + "", orderSource.getSourceCode())).collect(Collectors.toList());
//
//            }
            response = cdsMstFeign.ordePay(cdsMstDto);
        }catch (Exception e) {
            response.setCode(-1);
            response.setMessage("调用mst服务ordePay异常");
        }
        return response;
    }

    @Override
    public BaseResponse ordeQuery(CdsMstDto cdsMstDto) throws Exception {
        BaseResponse response = null;
        try {
            response = cdsMstFeign.ordeQuery(cdsMstDto);
        }catch (Exception e) {
            response.setCode(-1);
            response.setMessage("调用mst服务ordeQuery异常");
        }
        return response;
    }
}
