package com.ipath.orderflowservice.order.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.core.map.service.CoreMapService;
import com.ipath.orderflowservice.core.tencent.service.CoreTencentService;
import com.ipath.orderflowservice.order.bean.RedisCpol;
import com.ipath.orderflowservice.order.bean.RedisCpolRegulationInfoAddressInfoCoordinate;
import com.ipath.orderflowservice.order.bean.bo.*;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.constant.OrderParamCheckConstant;
import com.ipath.orderflowservice.order.bean.param.*;
import com.ipath.orderflowservice.order.bean.vo.*;
import com.ipath.orderflowservice.order.dao.*;
import com.ipath.orderflowservice.order.dao.bean.*;
import com.ipath.orderflowservice.order.enums.CheckPlaceOrderItemsEnum;
import com.ipath.orderflowservice.order.service.*;
import com.ipath.orderflowservice.order.util.CoordinateUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 订单参数校验 ServiceImpl
 */
@Service
@Slf4j
public class OrderCheckServiceImpl implements OrderCheckService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserBaseMapper userBaseMapper;

    @Autowired
    private ComSceneMapper comSceneMapper;

    @Autowired
    private CoreTencentService coreTencentService;

    @Autowired
    private OrderSummaryMapper orderSummaryMapper;

    @Autowired
    private OrderBaseMapper orderBaseMapper;


    @Autowired
    private OrderParamCheckMsgConfigMapper orderParamCheckMsgConfigMapper;


    @Autowired
    private UserService userService;

    @Autowired
    private OrderLimitService orderLimitService;

    @Autowired
    private RegulationService regulationService;

    @Autowired
    private CoreMapService coreMapService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OrderLimitConfigMapper orderLimitConfigMapper;

    @Autowired
    private OrderCheckService orderCheckService;

    @Autowired
    private ComSceneParaMapper comSceneParaMapper;
    /**
     * 参数校验
     *
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public List<OrderParamCheckVo> checkOrderParam(OrderParamCheckParam param) throws Exception {
        List<OrderParamCheckVo> list = new ArrayList<>();
        List<String> checkItems = param.getCheckItems();
        if (CollectionUtils.isEmpty(checkItems)) {
            return list;
        }
        list = checkItems.stream().map(code ->
                {
                    OrderParamCheckVo result = new OrderParamCheckVo();
                    if (StringUtils.equals(code, OrderParamCheckConstant.ORDER_PARAM_CK_CODE_TRAFFICHUB)) {
                        result = this.setOrderParamCheckTrafficHub(param.getCompanyId(), param);
                    } else {
                        result = this.setOrderParamCheck(code, param.getCompanyId(), param);
                    }
                    return result;
                }
        ).collect(Collectors.toList());
        return list;
    }

    /**
     * 参数校验（根据code）
     *
     * @param code
     * @param companyId
     * @return
     */
    @Override
    public OrderParamCheckVo setOrderParamCheck(String code, Long companyId, OrderParamCheckParam param) {
        OrderParamCheckVo orderParamCheckVo = new OrderParamCheckVo();

        //base地限制
        if (StringUtils.equals(code, CheckPlaceOrderItemsEnum.CHECK_1100.getcode())) {
            OrderParamCheckVo orderParamCheckVo1 = new OrderParamCheckVo();
            OrderParamCheckVo orderParamCheckVo2 = new OrderParamCheckVo();
            OrderParamCheckParam param1 = new OrderParamCheckParam();
            BeanUtil.copyProperties(param, param1);
            param1.setDestLng(null);
            param1.setDestLat(null);
            param1.setDestCityCode(null);
            OrderParamCheckParam param2 = new OrderParamCheckParam();
            BeanUtil.copyProperties(param, param2);
            param2.setDepartLng(null);
            param2.setDepartLat(null);
            param2.setDepartCityCode(null);
            orderParamCheckVo1 = this.setOrderParamCheckBase(code, companyId, param1);
            orderParamCheckVo2 = this.setOrderParamCheckBase(code, companyId, param2);
            String msg1 = orderParamCheckVo1.getRemark();
            String msg2 = orderParamCheckVo2.getRemark();
            boolean show1 = orderParamCheckVo1.isShow();
            boolean show2 = orderParamCheckVo2.isShow();
            log.info("setOrderParamCheck ===> userId:{},paramCheck1:{}", param.getUserId(), orderParamCheckVo1.getRemark());
            log.info("setOrderParamCheck ===> userId:{},paramCheck2:{}", param.getUserId(), orderParamCheckVo2.getRemark());
            if (show1 && show2) {
                orderParamCheckVo = this.getOrderParamCheckVo(code, true, "all", null, null);
            } else if (show1 && !show2
                    && !StringUtils.equals(msg2, "isNotBaseCity")
                    && !StringUtils.equals(msg2, "isTrafficHub")) {
                orderParamCheckVo = this.getOrderParamCheckVo(code, true, "depart", null, null);
            } else if (!show1 && show2
                    && !StringUtils.equals(msg1, "isNotBaseCity")
                    && !StringUtils.equals(msg1, "isTrafficHub")) {
                orderParamCheckVo = this.getOrderParamCheckVo(code, true, "dest", null, null);
            } else {
                orderParamCheckVo = this.getOrderParamCheckVo(code, false, "", null, null);
            }
            orderParamCheckVo.setMsg(orderParamCheckVo1.getMsg());
        }

        return orderParamCheckVo;
    }

    /**
     * 参数校验（交通枢纽）
     * @param companyId
     * @param param
     * @return
     */
    @Override
    public OrderParamCheckVo setOrderParamCheckTrafficHub(Long companyId, OrderParamCheckParam param) {
        OrderParamCheckVo orderParamCheckVo = new OrderParamCheckVo();

        OrderParamCheckVo orderParamCheckVo1 = new OrderParamCheckVo();
        OrderParamCheckVo orderParamCheckVo2 = new OrderParamCheckVo();
        OrderParamCheckParam param1 = new OrderParamCheckParam();
        BeanUtil.copyProperties(param, param1);
        param1.setDestLng(null);
        param1.setDestLat(null);
        param1.setDestCityCode(null);
        OrderParamCheckParam param2 = new OrderParamCheckParam();
        BeanUtil.copyProperties(param, param2);
        param2.setDepartLng(null);
        param2.setDepartLat(null);
        param2.setDepartCityCode(null);
        orderParamCheckVo1 = this.setOrderParamCheckBase("", companyId, param1);
        orderParamCheckVo2 = this.setOrderParamCheckBase("", companyId, param2);
        String msg1 = orderParamCheckVo1.getRemark();
        String msg2 = orderParamCheckVo2.getRemark();
        boolean show1 = orderParamCheckVo1.isShow();
        boolean show2 = orderParamCheckVo2.isShow();
        log.info("setOrderParamCheck ===> userId:{},paramCheck1:{}", param.getUserId(), orderParamCheckVo1.getRemark());
        log.info("setOrderParamCheck ===> userId:{},paramCheck2:{}", param.getUserId(), orderParamCheckVo2.getRemark());
        if (show1 && show2) {
            orderParamCheckVo = this.getOrderParamCheckVo(
                    OrderParamCheckConstant.ORDER_PARAM_CK_CODE_TRAFFICHUB,
                    true,
                    "all",
                    null,
                    null);

        }
        orderParamCheckVo.setMsg(orderParamCheckVo1.getMsg());

        return orderParamCheckVo;
    }

    /**
     * 判断是否需要个人支付 （状态5 结算时）
     *
     * @param orderBase
     * @param orderSource
     * @return
     */
    @Override
    public boolean isNeedIndividualPay(OrderBase orderBase, OrderSource orderSource) {

        Boolean isUserPay = orderBase.getIsUserPay();
        if(null != isUserPay){
            return isUserPay;
        }


        /**
         * base地限制 触发个人支付
         */
        OrderParamCheckParam orderParamCheckParam = new OrderParamCheckParam();
        orderParamCheckParam.setUserId(orderBase.getUserId());
        orderParamCheckParam.setCompanyId(orderBase.getCompanyId());
        orderParamCheckParam.setSceneId(orderBase.getSceneId());
        orderParamCheckParam.setDepartCityCode(orderBase.getDepartCityCode());
        orderParamCheckParam.setDestCityCode(orderBase.getDestCityCode());
        orderParamCheckParam.setDepartLat(orderBase.getDepartLat());
        orderParamCheckParam.setDepartLng(orderBase.getDepartLng());
        orderParamCheckParam.setDestLat(orderBase.getDestLat());
        orderParamCheckParam.setDestLng(orderBase.getDestLng());
        OrderParamCheckVo orderParamCheckVo = this.setOrderParamCheck(CheckPlaceOrderItemsEnum.CHECK_1100.getcode(), orderBase.getCompanyId(), orderParamCheckParam);
        boolean show = orderParamCheckVo.isShow();
        if (show) {
            return true;
        }

        return false;
    }

    /**
     * 企业用户自定义管控参数校验
     * @param param
     * @param step
     * @throws Exception
     */
    @Override
    public void checkOrderParamRegulationInfo(CreateOrderParam param, String step) throws Exception {

        //预估前验证
        if (StringUtils.equals(step, "preCheck")) {
          this.checkOrderParamRegulationInfoPreCheck(param);
        //预估校验
        } else if (StringUtils.equals(step, "preCheck2")) {
            //获取校验项目
            Long companyId = param.getCompanyId();
            Long userId = param.getUserId();
            String departLat = param.getDepartLat();
            String departLng = param.getDepartLng();
            String destLat = param.getDestLat();
            String destLng = param.getDestLng();

            //判断校验是否开启
            if (!orderLimitService.isOpenOrderLimitConfig(param.getCompanyId())) {
                log.info("checkOrderParamRegulationInfo ===> companyId:{},企业用户自定义管控参数校验【未开启】",companyId);
                return;
            }

            //获取校验参数
            OrderLimitConfigValueCompanyVo orderLimitConfig = orderLimitService.getOrderLimitConfig(param.getCompanyId());
            if(StringUtils.isBlank(orderLimitConfig.getValue())){
                return;
            }

            JSONObject jsonObject = JSONUtil.parseObj(orderLimitConfig.getValue());
            JSONArray items = jsonObject.getJSONArray("items");
            boolean preCheck = items.contains("preCheck1-1");
            if(!preCheck){
                return;
            }

            List<SelectedCar> cars = param.getCars();
            BigDecimal bigDecimal = cars.stream().map(SelectedCar::getEstimatePrice).collect(Collectors.toList()).stream().max(Comparator.comparing(x -> x)).orElse(null);
            BigDecimal limitedAmount = null;
            RedisCpol redisCpol = orderLimitService.getRedisCpol(companyId, userId);
            try {
                if(null != bigDecimal){
                    bigDecimal = bigDecimal.multiply(new BigDecimal("100"));

                    limitedAmount = redisCpol.getRegulationInfo().getAmount().getLimitedAmount();
                }
            }catch (Exception e){
                log.info("checkOrderParamRegulationInfo ===> companyId:{},userId{} 未获取到单次额度",companyId,userId);
                return;
            }
            Boolean allowExcess = redisCpol.getRegulationInfo().getAllowExcess();
            if(null != limitedAmount && bigDecimal.compareTo(limitedAmount) == 1 && (null == allowExcess || !allowExcess)){
                throw new BusinessException("超出可用额度");
            }
        }
    }

    /**
     * 企业用户自定义管控参数校验 预估前验证
     * @param param
     */
    private void checkOrderParamRegulationInfoPreCheck(CreateOrderParam param){
        //获取校验项目
        Long companyId = param.getCompanyId();
        Long userId = param.getUserId();
        String departLat = param.getDepartLat();
        String departLng = param.getDepartLng();
        String destLat = param.getDestLat();
        String destLng = param.getDestLng();

        //判断校验是否开启
        if (!orderLimitService.isOpenOrderLimitConfig(param.getCompanyId())) {
            log.info("checkOrderParamRegulationInfoPreCheck ===> companyId:{},企业用户自定义管控参数校验【未开启】",companyId);
            return;
        }

        //获取校验参数
        OrderLimitConfigValueCompanyVo orderLimitConfig = orderLimitService.getOrderLimitConfig(param.getCompanyId());
        if(StringUtils.isBlank(orderLimitConfig.getValue())){
            return;
        }
        JSONObject jsonObject = JSONUtil.parseObj(orderLimitConfig.getValue());
        JSONArray items = jsonObject.getJSONArray("items");
        boolean preCheck1 = items.contains("preCheck1");
        if(!preCheck1){
            return;
        }

        RedisCpol redisCpol = orderLimitService.getRedisCpol(companyId, userId);

        List<RedisCpolRegulationInfoAddressInfoCoordinate> departCoordinate = null;
        List<RedisCpolRegulationInfoAddressInfoCoordinate> destCoordinate = null;
        try {
            departCoordinate = redisCpol.getRegulationInfo().getAddress().getOrigin().getCoordinate();
            destCoordinate = redisCpol.getRegulationInfo().getAddress().getDest().getCoordinate();
        }catch (Exception e){
            log.info("checkOrderParamRegulationInfoPreCheck ===> companyId:{},userId{}未获取到管控参数Coordinate",companyId,userId);
            return;
        }

        if(null != departCoordinate){
            //校验起点
            for (RedisCpolRegulationInfoAddressInfoCoordinate ce : departCoordinate) {
                Integer departDistance = CoordinateUtil.getDistanceMeter(
                        Double.valueOf(departLat),
                        Double.valueOf(departLng),
                        Double.valueOf(ce.getLat()),
                        Double.valueOf(ce.getLng()));
                if (departDistance != null && ce.getRange() != null && departDistance > Integer.valueOf(ce.getRange())) {
                    log.info("checkOrderParamRegulationInfoPreCheck ===> companyId:{},userId{}起点超出可用范围",companyId,userId);
                    throw new BusinessException("超出可用范围");
                }
            }
        }

        if(null != destCoordinate){
            //校验终点
            for (RedisCpolRegulationInfoAddressInfoCoordinate ce : destCoordinate) {
                Integer departDistance = CoordinateUtil.getDistanceMeter(
                        Double.valueOf(destLat),
                        Double.valueOf(destLng),
                        Double.valueOf(ce.getLat()),
                        Double.valueOf(ce.getLng()));
                if (departDistance != null && ce.getRange() != null && departDistance > Integer.valueOf(ce.getRange())) {
                    log.info("checkOrderParamRegulationInfoPreCheck ===> companyId:{},userId{}终点超出可用范围",companyId,userId);
                    throw new BusinessException("超出可用范围");
                }
            }
        }

    }


    /**
     * 行前+行后
     *
     */
    @Override
    public void checkSceneApprovalTypePreAndAf(CreateOrderParam param) {
        Long companyId = param.getCompanyId();
        CompanyOrderCheckConfigBo config = orderCheckService.getCompanyOrderCheckConfig();
        if (null == config || (null == config.getSceneApprovalTypePreAndAf())) {
            return;
        }
        //开启行前+行后场景校验
        List<Long> sceneApprovalTypePreAndAfList = config.getSceneApprovalTypePreAndAf().getCompanyList();
        if (CollectionUtil.isNotEmpty(sceneApprovalTypePreAndAfList) && sceneApprovalTypePreAndAfList.contains(companyId)) {
            JSONObject jsonObject = config.getSceneApprovalTypePreAndAf().getConfigMap().get(companyId);
            BigDecimal checkEstimatePrice = jsonObject.getBigDecimal("checkEstimatePrice");
            List<SelectedCar> cars = param.getCars();
            BigDecimal bigDecimal = cars.stream().map(SelectedCar::getEstimatePrice).collect(Collectors.toList()).stream().max(Comparator.comparing(x -> x)).orElse(null);
            if (bigDecimal.compareTo(checkEstimatePrice) > 0) {
                ComScene comScene = comSceneMapper.selectByPrimaryKey(param.getSceneId());
                if (comScene.getApprovalType() != (short) 3) {
                    ComScene selectListByCompanyIdParam = new ComScene();
                    selectListByCompanyIdParam.setCompanyId(companyId);
                    selectListByCompanyIdParam.setApprovalType((short) 3);
                    List<ComScene> comSceneList = comSceneMapper.selectListByCompanyId(selectListByCompanyIdParam);
                    if (CollectionUtil.isEmpty(comSceneList)) {
                        throw new BusinessException("该笔订单预估价超过" + checkEstimatePrice + "，请先配置行前+行后审批场景用车，然后使用该场景用车");
                    }
                    throw new BusinessException("该笔订单预估价超过" + checkEstimatePrice + "，请使用" + comSceneList.get(0).getNameCn() + "场景用车");
                }
            }
        }
    }

    @Override
    public int checkStatementOfAccount(StatementOfAccountParam param) {
        if (param.getExportDimensions().equals(0)) {
            return orderBaseMapper.checkStatementOfAccountByOrderTime(param);
        } else if (param.getExportDimensions().equals(1)) {
            return orderBaseMapper.checkStatementOfAccountByApprovalTime(param);
        } else if (param.getExportDimensions().equals(2)) {
            return orderBaseMapper.checkStatementOfAccountByTravelEndTime(param);
        } else if (param.getExportDimensions().equals(3)) {
            return orderBaseMapper.checkStatementOfAccountByTravelBeginTime(param);
        }
        return 0;
    }

    @Override
    public List<CompanyOrderCountVo> getCompanyCountByCreateTime(CompanyOrderCountParam param) {
        return orderBaseMapper.getCompanyCountByCreateTime(param);
    }

    @Override
    public List<CompanyOrderCountVo> getCompanyStateCountByCreateTime(CompanyOrderCountParam param) {
        return orderBaseMapper.getCompanyStateCountByCreateTime(param);
    }

    @Override
    public List<CompanyOrderCountVo> getCompanyCountByApprovalTime(CompanyOrderCountParam param) {
        return orderBaseMapper.getCompanyCountByApprovalTime(param);
    }

    /**
     * base地限制
     *
     * @param code
     * @return
     */
    private OrderParamCheckVo setOrderParamCheckBase(String code, Long companyId, OrderParamCheckParam param) {
        try {

            boolean resultShow = false;
            //base地限制判断是否开启
            boolean isBase = false;
            boolean isAz = true;
            int baseR = -1;

            OrderParamCheckMsgConfig orderParamCheckMsgConfig = orderParamCheckMsgConfigMapper.selectByCompanyId(companyId);
            boolean isConfig = (null == orderParamCheckMsgConfig);

            boolean existUserCheck = userService.existUserCheck(param.getUserId(), companyId);

            if(!isConfig && StringUtils.equals(orderParamCheckMsgConfig.getScope(),"1") && !existUserCheck){
                return this.getOrderParamCheckVo(code, resultShow, "非校验人", null, null);
            }

            if(!isConfig && StringUtils.equals(orderParamCheckMsgConfig.getScope(),"Az")){
                isAz = true;
            }else {
                isAz = false;
            }

            List<String> paraCodeList = new ArrayList<>();
            paraCodeList.add(code);
            JSONObject companyJsonObject = new JSONObject();
            companyJsonObject.set("companyId", companyId);
            companyJsonObject.set("paraCodeList", paraCodeList);
            BaseResponse baseResponse = systemService.getCompanyCommonCfg(companyJsonObject);
            log.info("setOrderParamCheckBase ===> {}", JSONUtil.toJsonStr(baseResponse));
            if (baseResponse != null && baseResponse.getData() != null) {
                JSONObject msgJSONObject = new JSONObject(baseResponse.getData());
                if (msgJSONObject != null) {
                    if (msgJSONObject.containsKey("paras")) {
                        JSONArray jsonArray = msgJSONObject.getJSONArray("paras");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject paraJSONObject = jsonArray.getJSONObject(i);
                                if (code.equals(paraJSONObject.getStr("paraCode"))) {
                                    JSONObject paraValue = paraJSONObject.getJSONObject("paraValue");
                                    isBase = paraValue.getBool("enabled", false);
                                    if (isBase) {
                                        BigDecimal value = paraValue.getBigDecimal("value", new BigDecimal("-1")).multiply(new BigDecimal("1000"));
                                        baseR = ( value.compareTo(new BigDecimal("-1000")) == 0) ? -1 : value.intValue();
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (!isBase) {
                return this.getOrderParamCheckVo(code, resultShow, "未开启base城市限制", null, null);
            }

            if (isBase && baseR == -1) {
                log.error("setOrderParamCheckBase ===> base地配置错误");
            }
            if (isAz) {
                //base地限制判断是否与用车城市一致
                String departCityCode = param.getDepartCityCode();
                String destCityCode = param.getDestCityCode();
                UserBase userBase = userBaseMapper.selectByPrimaryKey(param.getUserId());
                String backup1 = userBase.getBackupField1();
                String backup2 = userBase.getBackupField2();
                if (StringUtils.isBlank(departCityCode) && StringUtils.isNotBlank(destCityCode)) {
                    if (!StringUtils.equals(backup2, destCityCode)) {
                        return this.getOrderParamCheckVo(code, false, "isNotBaseCity", null, null);
                    }
                } else if (StringUtils.isNotBlank(departCityCode) && StringUtils.isBlank(destCityCode)) {
                    if (!StringUtils.equals(backup2, departCityCode)) {
                        return this.getOrderParamCheckVo(code, false, "isNotBaseCity", null, null);
                    }
                } else if (StringUtils.isNotBlank(departCityCode) && StringUtils.isNotBlank(destCityCode)) {
                    if (!StringUtils.equals(backup2, departCityCode) || !StringUtils.equals(backup2, destCityCode)) {
                        return this.getOrderParamCheckVo(code, false, "isNotBaseCity", null, null);
                    }
                } else {
                    return this.getOrderParamCheckVo(code, false, "出发地与目的地都为空", null, null);
                }

                //base地限制判断用车场景是否出差
                Long sceneId = param.getSceneId();
                ComScene comScene = comSceneMapper.selectByPrimaryKey(sceneId);
                if (!(null != comScene && StringUtils.equals(comScene.getNameCn(), "出差"))) {
                    return this.getOrderParamCheckVo(code, false, "isNotCX", null, null);
                }

                //base地限制判断用车人是否是销售角色
                resultShow = StringUtils.equals(backup1, "1");
                if (!resultShow) {
                    return this.getOrderParamCheckVo(code, false, "isNotXS", null, null);
                }
            }

            String departLat = param.getDepartLat();
            String departLng = param.getDepartLng();
            String destLat = param.getDestLat();
            String destLng = param.getDestLng();

            //base地限制判断半径内有交通枢纽
            boolean trafficHub1 = false;
            boolean trafficHub2 = false;
            if (StringUtils.isNotBlank(departLat) && StringUtils.isNotBlank(departLng)) {
                trafficHub1 = coreTencentService.hasTrafficHub(departLat, departLng, baseR);
            }
            if (StringUtils.isNotBlank(destLat) && StringUtils.isNotBlank(destLng)) {
                trafficHub2 = coreTencentService.hasTrafficHub(destLat, destLng, baseR);
            }

            if (trafficHub1 || trafficHub2) {
                return this.getOrderParamCheckVo(code, false, "isTrafficHub", null, null);
            }
            OrderParamCheckVo result = this.getOrderParamCheckVo(code, true, "提示", null, null);

            if (null != orderParamCheckMsgConfig) {
                OrderParamCheckMsgVo msgVo = new OrderParamCheckMsgVo();
                msgVo.setContent(orderParamCheckMsgConfig.getContent());
                msgVo.setTitle(orderParamCheckMsgConfig.getTitle());
                msgVo.setCancel(new JSONObject(orderParamCheckMsgConfig.getCancel()));
                msgVo.setConfirm(new JSONObject(orderParamCheckMsgConfig.getConfirm()));
                msgVo.setOptions(JSONUtil.toList(orderParamCheckMsgConfig.getOptions(), OrderParamCheckMsgOptionVo.class));
                result.setMsg(msgVo);
            }

            return result;
        } catch (Exception e) {
            return this.getOrderParamCheckVo(code, false, "异常：" + e.toString(), null, null);
        }

    }

    private OrderParamCheckVo getOrderParamCheckVo(String code, boolean resultShow, String resultMsg, String resultCancelText, String resultConfirmText) {
        OrderParamCheckVo orderParamCheckVo = new OrderParamCheckVo();
        orderParamCheckVo.setCode(code);
        orderParamCheckVo.setName(CheckPlaceOrderItemsEnum.getByCode(code).getName());
        orderParamCheckVo.setShow(resultShow);
        orderParamCheckVo.setRemark(resultMsg == null ? "" : resultMsg);
        orderParamCheckVo.setCancelText(resultCancelText == null ? "" : resultCancelText);
        orderParamCheckVo.setConfirmText(resultConfirmText == null ? "" : resultConfirmText);

        return orderParamCheckVo;
    }


    /**
     * 下单参数校验（交通枢纽校验）
     *
     * @param orderParam
     * @param step
     * @return
     * @throws Exception
     */
    @Override
    public CheckOrderParamResult checkOrderParamIncludeTraffichub(CreateOrderParam orderParam, String step) throws Exception {
        if (StringUtils.equals(step, "preCheck")) {
            CompanyOrderCheckConfigBo companyOrderCheckConfig = this.getCompanyOrderCheckConfig();
            CompanyOrderCheckConfigTrafficHubBo trafficHub = companyOrderCheckConfig.getTrafficHub();
            if (null != trafficHub
                    && CollectionUtils.isNotEmpty(trafficHub.getCompanyList())
                    && trafficHub.getCompanyList().contains(orderParam.getCompanyId())) {

                List<CompanyOrderCheckConfigTrafficHubInfoBo> config = companyOrderCheckConfig.getTrafficHub().getConfigList().stream()
                        .filter(o -> o.getCheckSceneNames().contains(orderParam.getSceneNameCn()))
                        .collect(Collectors.toList());
                boolean isCheckScene = config.size() > 0;
                if (isCheckScene) {
                    boolean istraffichubDepart = coreMapService.istraffichub(orderParam.getDepartLng(), orderParam.getDepartLat(),orderParam.getCompanyId());
                    boolean istraffichubgetDest = coreMapService.istraffichub(orderParam.getDestLng(), orderParam.getDestLat(),orderParam.getCompanyId());
                    if (!istraffichubDepart && !istraffichubgetDest) {
                        return CheckOrderParamResult.error(config.get(0).getErrorMsg());
                    }
                }
            }
        }
        return CheckOrderParamResult.success();
    }

    /**
     * 下单参数校验配置
     *
     * @return
     */
    @Override
    public CompanyOrderCheckConfigBo getCompanyOrderCheckConfig() {
        String key = "companyOrderCheckConfig";
        CompanyOrderCheckConfigBo result = new CompanyOrderCheckConfigBo();
        if (redisUtil.hasKey(key)) {
            Object o = redisUtil.get(key);
            result = (CompanyOrderCheckConfigBo) o;
        } else {
            //交通枢纽
            this.setTrafficHub(result);
            //行前+行后
            this.setSceneApprovalTypePreAndAf(result);
            redisUtil.set(key, result, CacheConsts.STABLE_CACHE_EXPIRE_TIME);
        }
        return result;
    }


    /**
     * 下单参数校验配置 交通枢纽
     * @param result
     */
    private void setTrafficHub(CompanyOrderCheckConfigBo result) {
        //交通枢纽校验
        String keyOrderCheckConfigTraffichub = "orderCheckConfigTraffichub";
        List<Long> companyList = this.getCompanyIdList(keyOrderCheckConfigTraffichub);
        CompanyOrderCheckConfigTrafficHubBo companyOrderCheckConfigTrafficHubBo = new CompanyOrderCheckConfigTrafficHubBo();
        companyOrderCheckConfigTrafficHubBo.setCompanyList(companyList);
        List<OrderLimitConfig> companyConfigList = this.getCompanyConfigList(keyOrderCheckConfigTraffichub);
        if (CollectionUtils.isNotEmpty(companyConfigList)) {
            List<CompanyOrderCheckConfigTrafficHubInfoBo> TrafficHubList = new ArrayList<>();
            companyConfigList.stream().forEach(o -> {
                String value = o.getValue();
                if (StringUtils.isNotBlank(value)) {
                    CompanyOrderCheckConfigTrafficHubInfoBo config = JSONUtil.toBean(value, CompanyOrderCheckConfigTrafficHubInfoBo.class);
                    TrafficHubList.add(config);
                }
            });
            companyOrderCheckConfigTrafficHubBo.setConfigList(TrafficHubList);
        }
        result.setTrafficHub(companyOrderCheckConfigTrafficHubBo);
    }


    /**
     * 下单参数校验配置 行前+行后
     * @param result
     */
    private void setSceneApprovalTypePreAndAf(CompanyOrderCheckConfigBo result) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType("sceneApprovalTypePreAndAf");
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        List<Long> list = orderLimitConfigs.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
        CompanyOrderCheckConfigInfoBo companyOrderCheckConfigInfoBo = new CompanyOrderCheckConfigInfoBo();
        companyOrderCheckConfigInfoBo.setCompanyList(list);
        Map<Long, JSONObject> map = orderLimitConfigs.stream().collect(Collectors.toMap(OrderLimitConfig::getCompanyId, OrderLimitConfig::getValue, (v1, v2) -> v2))
                .entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> new JSONObject(e.getValue())));
        companyOrderCheckConfigInfoBo.setConfigMap(map);
        result.setSceneApprovalTypePreAndAf(companyOrderCheckConfigInfoBo);
    }

    @Override
    public CompanyOrderCheckConfigBo reCompanyOrderCheckConfig() {
        String key = "companyOrderCheckConfig";
        redisUtil.delete(key);
        return this.getCompanyOrderCheckConfig();
    }

    /**
     * 查询公司id列表（根据类型）
     * @param type
     * @return
     */
    private List<Long> getCompanyIdList(String type) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType(type);
        List<OrderLimitConfig> orderLimitConfigs = orderLimitConfigMapper.selectList(orderLimitConfig);
        List<Long> result = orderLimitConfigs.stream().map(o -> o.getCompanyId()).collect(Collectors.toList());
        return result;
    }

    /**
     * 查询公司配置列表（根据类型）
     * @param type
     * @return
     */
    public List<OrderLimitConfig> getCompanyConfigList(String type) {
        OrderLimitConfig orderLimitConfig = new OrderLimitConfig();
        orderLimitConfig.setType(type);
        List<OrderLimitConfig> result = orderLimitConfigMapper.selectList(orderLimitConfig);
        return result;
    }


}