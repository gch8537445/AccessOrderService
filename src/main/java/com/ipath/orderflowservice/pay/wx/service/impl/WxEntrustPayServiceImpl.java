package com.ipath.orderflowservice.pay.wx.service.impl;


import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import com.ipath.common.bean.BusinessException;
import com.ipath.common.util.SnowFlakeUtil;
import com.ipath.orderflowservice.feignclient.dto.UserBaseInfoDto;
import com.ipath.orderflowservice.order.dao.OrderSourceMapper;
import com.ipath.orderflowservice.order.dao.UserBaseMapper;
import com.ipath.orderflowservice.order.dao.bean.OrderSource;
import com.ipath.orderflowservice.order.dao.bean.UserBase;
import com.ipath.orderflowservice.order.service.UserService;
import com.ipath.orderflowservice.pay.wx.bean.wxentrustpay.*;
import com.ipath.orderflowservice.pay.wx.constant.WxConstant;
import com.ipath.orderflowservice.pay.wx.service.WxEntrustPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;


/**
 * 微信免密支付 serviceImpl
 */
@Slf4j
@Service
public class WxEntrustPayServiceImpl implements WxEntrustPayService {

    @Value("${pay.url}")
    private String payuUrl;
    @Value("${pay.appId}")
    private String appId;
    @Value("${pay.configId}")
    private String configId;
    @Value("${pay.templeteId}")
    private String templeteId;
    @Value("${pay.key}")
    private String key;

    @Autowired
    private SnowFlakeUtil snowFlakeUtil;
    @Autowired
    private UserBaseMapper userBaseMapper;
    @Autowired
    private OrderSourceMapper orderSourceMapper;

    @Autowired
    private UserService userService;

//    /**
//     * 小程序申请免密支付请求
//     *
//     * @param requestappletpayReq
//     * @throws Exception
//     */
//    @Override
//    public void requestappletpay(RequestappletpayReq requestappletpayReq, Long userId, Long companyId) throws Exception {
//        UserBase userBase = userBaseMapper.selectByPrimaryKey(userId);
//        String id = snowFlakeUtil.getNextId().toString();
//        requestappletpayReq.setRequestSerial(id);
//        requestappletpayReq.setUserName(userBase.getNameCn());
//        requestappletpayReq.setAppId(appId);
//        requestappletpayReq.setConfigId(configId);
//        requestappletpayReq.setTempleteId(templeteId);
//        String result = this.post(WxConstant.WX_ENTRUST_PAY_REQUESTAPPLETPAY, JsonUtil.toJson(requestappletpayReq));
//        Response response = JSONUtil.toBean(result, Response.class);
//        if (null == response) {
//            throw new Exception("申请失败，请稍后再试。");
//        }
//        if (response.getState() != 0) {
//            throw new Exception(response.getTxt());
//        }
//
//        userBaseMapper.updateByPrimaryKeySelective(userBase);
//    }

    /**
     * 公众号拉起免密支付页面
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public RedirectView mmpay(Long userId) throws Exception {
        UserBaseInfoDto user = userService.getUserBaseInfoDtoByUserId(userId);
        String wxEntrustPayId = user.getWxEntrustPayId();
        Integer wxEntrustPayState = user.getWxEntrustPayState();
        //客户免密支付签约状态。0：已签约 1：未签约  9：签约进行中
        if(StringUtils.isNotBlank(wxEntrustPayId) && wxEntrustPayState == 0){
            throw new BusinessException("已签约");
        }
        if(StringUtils.isNotBlank(wxEntrustPayId) && wxEntrustPayState == 9){
            throw new BusinessException("签约进行中");
        }
        Long requestSerial = snowFlakeUtil.getNextId();
        UserBase userBase = new UserBase();
        userBase.setId(user.getId());
        userBase.setWxEntrustpayId(requestSerial.toString());
        String phone = user.getPhone();
        Long id = user.getId();
        userBaseMapper.updateByPrimaryKeySelective(userBase);

        String url = payuUrl + WxConstant.WX_ENTRUST_PAY_MMPAY;
        return new RedirectView(url +
                "?requestSerial=" + requestSerial +
                "&userName=" + (StringUtils.isBlank(phone) ? id : phone) +
                "&appid=" + appId +
                "&configID=" + configId +
                "&templeteID=" + templeteId +
                "&attachInfo=" + user.getId()
        );
    }

    /**
     * 申请解约免密支付
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public boolean deletecontract(Long userId) throws Exception {
        UserBaseInfoDto user = userService.getUserBaseInfoDtoByUserId(userId);
        String wxEntrustPayId = user.getWxEntrustPayId();
        Integer wxEntrustPayState = user.getWxEntrustPayState();

        if(StringUtils.isBlank(wxEntrustPayId)){
            throw new BusinessException("未开通免密支付。");
        }
        if(StringUtils.isNotBlank(wxEntrustPayId) && null != wxEntrustPayState && wxEntrustPayState == 1){
            throw new BusinessException("已关闭免密支付。");
        }

        WxentrustpayRequest request = new WxentrustpayRequest();
        request.setRequestSerial(wxEntrustPayId);
        request.setAppId(appId);
        request.setConfigId(configId);
        String singStr = "appid=" + appId + "&configID=" + configId + "&requestSerial=" + wxEntrustPayId;
        String sign = this.getSign(singStr);
        request.setSign(sign);
        log.info("申请解约免密支付 ===> url:{},参数：{}",payuUrl + WxConstant.WX_ENTRUST_PAY_DELETECONTRACT,JSONObject.toJSONString(request));
        String result = this.post(WxConstant.WX_ENTRUST_PAY_DELETECONTRACT, JSONObject.toJSONString(request));
        WxentrustpayResponse response = JSONUtil.toBean(result, WxentrustpayResponse.class);
        if (null == response) {
            throw new BusinessException("操作失败，请稍后再试。");
        }
        if (response.getState() != 0) {
            throw new BusinessException(response.getTxt());
        }

        UserBase userBase = new UserBase();
        userBase.setId(user.getId());
        userBase.setWxEntrustpayState(1);
        userBaseMapper.updateByPrimaryKeySelective(userBase);

        return true;
    }


    /**
     * 查询微信免密签约信息
     * @param userId
     * @return
     * @throws Exception
     */
    @Override
    public QuerycontractResData querycontract(Long userId) throws Exception {
        UserBaseInfoDto user = userService.getUserBaseInfoDtoByUserId(userId);
        String wxEntrustPayId = user.getWxEntrustPayId();
        WxentrustpayRequest request = new WxentrustpayRequest();
        request.setRequestSerial(wxEntrustPayId);
        request.setAppId(appId);
        request.setConfigId(configId);
        String singStr = "appid=" + appId + "&configID=" + configId + "&requestSerial=" + wxEntrustPayId;
        String sign = this.getSign(singStr);
        request.setSign(sign);
        log.info("查询微信免密签约信息 ===> url:{},参数：{}",payuUrl + WxConstant.WX_ENTRUST_PAY_QUERYCONTRACT,JSONObject.toJSONString(request));
        String result = this.post(WxConstant.WX_ENTRUST_PAY_QUERYCONTRACT, JSONObject.toJSONString(request));
        QuerycontractRes response = JSONUtil.toBean(result, QuerycontractRes.class);
        if (null == response) {
            throw new BusinessException("查询失败，请稍后再试。");
        }
        if (response.getState() != 0) {
            throw new BusinessException(response.getTxt());
        }
        return response.getData();
    }

    /**申请扣款
     *
     * @param userId
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public boolean vxEntrustApplyPay(Long userId, Long orderId) throws Exception {
        UserBaseInfoDto user = userService.getUserBaseInfoDtoByUserId(userId);
        String wxEntrustPayId = user.getWxEntrustPayId();
        Integer wxEntrustPayState = user.getWxEntrustPayState();
        if(StringUtils.isBlank(wxEntrustPayId)){
            throw new BusinessException("未开通免密支付。");
        }
        if(StringUtils.isNotBlank(wxEntrustPayId) && null != wxEntrustPayState && wxEntrustPayState == 1){
            throw new BusinessException("已关闭免密支付。");
        }
        OrderSource orderSource = orderSourceMapper.selectByOrderId(orderId);
        if(null == orderSource){
            throw new BusinessException("订单不存在。");
        }
        ApplyfordeductionsReq request = new ApplyfordeductionsReq();
        request.setAppId(appId);
        request.setAttachInfo(orderId.toString());
        request.setConfigId(configId);
        request.setGoodsBody("易企出行");
        request.setGoodsDetail("易企出行");
        request.setOutTradeNo(orderId.toString());
        request.setRequestSerial(wxEntrustPayId);
        request.setTotalFee(orderSource.getAmount().multiply(new BigDecimal("100")).intValue());
        String singStr ="appid=" + appId +
                "&attachInfo=" + orderId.toString() +
                "&configID=" + configId +
                "&goodsBody=" + "易企出行" +
                "&goodsDetail=" + "易企出行" +
                "&outTradeNo=" + orderId.toString() +
                "&requestSerial=" + wxEntrustPayId +
                "&total_fee=" + orderSource.getAmount().multiply(new BigDecimal("100")).intValue();
        String sign = this.getSign(singStr);
        request.setSign(sign);
        log.info("申请扣款 ===> url:{},参数：{}",payuUrl + WxConstant.WX_ENTRUST_PAY_APPLYFORDEDUCTIONS,JSONObject.toJSONString(request));
        String result = this.post(WxConstant.WX_ENTRUST_PAY_APPLYFORDEDUCTIONS, JSONObject.toJSONString(request));
        WxentrustpayResponse response = JSONUtil.toBean(result, WxentrustpayResponse.class);
        if (null == response) {
            throw new BusinessException("操作失败，请稍后再试。");
        }
        if (response.getState() != 0) {
            throw new BusinessException(response.getTxt());
        }
        return true;
    }

    /**
     * 查询扣款订单信息
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public PaporderqueryResData vxEntrustaQueryPay(Long userId, Long orderId) throws Exception {

        PaporderqueryReq request = new PaporderqueryReq();
        request.setAppId(appId);
        request.setConfigId(configId);
        request.setOutTradeNo(orderId.toString());
        String singStr = "appid=" + appId + "&configID=" + configId + "&out_trade_no=" + orderId.toString();
        String sign = this.getSign(singStr);
        request.setSign(sign);
        log.info("查询扣款订单信息 ===> url:{},参数：{}",payuUrl + WxConstant.WX_ENTRUST_PAY_PAPORDERQUERY,JSONObject.toJSONString(request));
        String result = this.post(WxConstant.WX_ENTRUST_PAY_PAPORDERQUERY, JSONObject.toJSONString(request));
        PaporderqueryRes response = JSONUtil.toBean(result, PaporderqueryRes.class);
        if (null == response) {
            throw new BusinessException("查询失败，请稍后再试。");
        }
        if (response.getState() != 0) {
            throw new BusinessException(response.getTxt());
        }
        return response.getData();
    }

    /**
     * 微信免密支付-协议授权结果回调
     * @param param
     * @return
     * @throws Exception
     */
    @Override
    public boolean wxEntrustAuthNotify(WxEntrustAuthNotifyReq param) throws Exception {
        Long userId = Long.valueOf(param.getAttachInfo());
        UserBase userBase = new UserBase();
        userBase.setId(userId);
        userBase.setWxEntrustpayState(Integer.valueOf(param.getResultCode()));
        userBaseMapper.updateByPrimaryKeySelective(userBase);
        return true;
    }

    /**
     * 免密支付post请求
     * @param path
     * @param body
     * @return
     */
    private String post(String path, String body) {
        String url = payuUrl + path;
        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .timeout(10000) // 超时，毫秒
                .body(body).execute()
                .body();
        return result;
    }

    /**签名
     *
     * 签名说明
     * 1.按照所有参数的key进行升序排列
     * 2.按照url参数方式拼接字符串
     * 3.将以上得到的字符串连接key。例如temp&key{key-value}
     * 4.将字符串进行MD5加密得到签名
     * 特殊说明
     * 1.默认编码UTF-8
     * 2.传递的参数有中文或特殊字符请自行编码
     * 3.签名参数使用编码前的参数
     *
     * @param bodyStr
     * @return
     */
    private String getSign (String bodyStr) {
        String str = bodyStr + "&key=" + key;
        log.info("加密串：{}", str);
        String result = DigestUtil.md5Hex(str).toUpperCase();
        log.info("加密后：{}", result);
        return result;
    }
}
