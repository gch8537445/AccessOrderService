package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

@Data
public class UserBaseInfoDto {
    private Long id;            // 用户主键
    private String nameCn;      // 用户中文名
    private String phone;       // 用车手机号
    private String emergencyPhone; // 紧急联系人手机号
    private String socialAppUserId;
    private String remark;      // 备注
    private String staffCode; // 工号
    //客户免密支付协议ID
    private String wxEntrustPayId;
    //客户免密支付签约状态。0：已签约1：未签约  9：签约进行中
    private Integer wxEntrustPayState;
}
