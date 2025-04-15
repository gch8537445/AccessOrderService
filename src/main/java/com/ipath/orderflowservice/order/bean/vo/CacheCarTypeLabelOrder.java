package com.ipath.orderflowservice.order.bean.vo;

import java.util.Date;

import lombok.Data;

@Data
public class CacheCarTypeLabelOrder {
    /**
     * 模板id
     */
    private Long id;
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 人员id
     */
    private Long userId;

    /**
     * 审批级别
     */
    private Integer approvalLevel;

    /**
     * 投诉数量
     */
    private Integer complaintCnt;

    /**
     * 完单数量
     */
    private Integer orderCnt;

    /**
     * 运力
     */
    private String carSource;

    /**
     * 车型标签Code
     */
    private String inItem1;

    /**
     * 车型
     */
    private Integer carLevel;

    /**
     * 城市code
     */
    private String cityCode;

    /**
     * 价格区间
     */
    private String orderAmount;

    /**
     * 里程区间
     */
    private String distance;

    /**
     * 时长区间
     */
    private String duration;

    /**
     * 出参1
     */
    private String outItem1;

    /**
     * 出参2
     */
    private String outItem2;

    /**
     * 出参3
     */
    private String outItem3;

    /**
     * 出参4
     */
    private String outItem4;

    /**
     * 出参5
     */
    private String outItem5;

    /**
     * 出参6
     */
    private String outItem6;

    /**
     * 出参7
     */
    private String outItem7;

    /**
     * 出参8
     */
    private String outItem8;

    /**
     * 出参9
     */
    private String outItem9;

    /**
     * 出参10
     */
    private String outItem10;

    /**创建时间 */
    private Date createdTime;
}
