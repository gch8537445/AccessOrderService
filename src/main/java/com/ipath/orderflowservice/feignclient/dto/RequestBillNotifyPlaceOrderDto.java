package com.ipath.orderflowservice.feignclient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 下单 通知bill-core-service的消息体
 */
@Data
public class RequestBillNotifyPlaceOrderDto extends RequestBillNotifyBaseDto{
    /**
     * 预估价格
     */
    private BigDecimal estimatePrice;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 账户id
     */
    private Long accountId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 场景发布id
     */
    private Long scenePublishId;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

//    /**
//     * 企业承担金额
//     */
//    private BigDecimal companyBearAmount;
//
//    /**
//     * 个人承担金额
//     */
//    private BigDecimal userBearAmount;

    /**
     * 出发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 输入参数格式
    private Date departTime;

    /**
     * 优惠券id列表
     */
    private List<Long> couponIds;

    /**
     * 服务类型
     */
    private Short serviceType;

    /**
     * 增值服务id列表
     */
    private List<Long> extraServiceIds;
}
