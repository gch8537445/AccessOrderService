package com.ipath.orderflowservice.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ipath.orderflowservice.feignclient.dto.TaxFeeModel;
import com.ipath.orderflowservice.order.bean.vo.CacheCompanyInfo;
import com.ipath.orderflowservice.order.bean.vo.CacheUserInfo;

import cn.hutool.json.JSONArray;

public interface CacheService {
    /**
     * 获取企业配置的税费
     * 
     * @param companyId 企业id
     * @return 税费相关信息
     */
    List<TaxFeeModel> getTaxFee(Long companyId);

    /**
     * 判断企业是否开启了免费升舱
     * 
     * @param companyId 企业id
     * @return true-开启 false-未开启
     */
    boolean isOpenUpgrade(Long companyId);

    /**
     * 获取免费升舱延时设置
     * @param companyId
     * @return
     */
    Map<String, Object> getUpgradeDelaySetting(Long companyId);

    /**
     * 判断企业是否开启大额预付
     * 
     * @param companyId 企业id
     * @return true-开启 false-未开启
     */
    boolean isPrepay(Long companyId);

    /**
     * 获取大额预付金额阈值
     * 
     * @param companyId
     * @return
     */
    BigDecimal getPrepayAmount(Long companyId);

    /**
     * 判断企业是否开启全平台握手
     * 
     * @param companyId 企业id
     * @return true-开启 false-未开启
     */
    boolean isOpenHandshake(Long companyId);

    /**
     * 获取企业可用的运力
     * 
     * @param companyId 企业id
     * @return 可用运力集合
     */
    List<Integer> getCarSource(Long companyId);

    /**
     * 获取企业预估价格折扣配置
     * 
     * @param companyId 企业id
     * @return 预估价折扣配置
     */
    String getEstimatePriceRule(Long companyId);

    /**
     * 获取企业派车模式配置
     * 
     * @param companyId 企业id
     * @return
     */
    String getDispatchMode(Long companyId);

    /**
     * 获取企业自动投诉配置
     * 
     * @param companyId 企业id
     * @return
     */
    boolean getAutoComplaint(Long companyId);

    /**
     * 获取企业取消费用自动投诉标识
     * 
     * @param companyId 企业id
     * @return
     */
    String getAutoComplaintConfig(Long companyId);

    /**
     * 是否开启接单距离
     * @param companyId
     * @return
     */
    boolean isOpenTakeDistance(Long companyId);

    /**
     * 获取企业配置费用
     * 
     * @param companyId 企业id
     * @return true-自动投诉 false-不投诉
     */
    boolean getExpenseFeeConfig(Long companyId);

    /**
     * 获取可用的增值服务
     * 
     * @return 增值服务列表
     */
    JSONArray getExtralService();

    /** 获取优选运力 */
    List<Integer> getPriorSource();

    /**获取多司机接单门槛时间 */
    int getThresholdTimeOfMultiDriverTakeOrder(Long companyId);

    /**
     * 是否开启io mapping
     * @param companyId
     * @return
     */
    boolean isOpenIO(Long companyId);

    /**
     * 是否有对应的io
     * @param companyId
     * @param costCenter
     * @param legalEntity
     * @return
     */
    boolean hasIO(Long companyId, String costCenter, String legalEntity);

    /**
     * 接口是否上报日志
     * @param interfacePath
     * @return
     */
    boolean isReportingLog(String interfacePath);

    /**
     * 获取用户信息
     * 
     * @param companyId 公司id
     * @param userId    用户id
     * @return
     */
    CacheUserInfo getUserInfo(Long companyId, Long userId);

    /**
     * 获取公司信息
     * 
     * @param companyId
     * @return
     */
    CacheCompanyInfo getCompanyInfo(Long companyId);

    /**
     * 获取运力名称
     * @param carSourceId
     * @param language
     * @return
     */
    String getSourceName(Integer carSourceId, int language);
}
