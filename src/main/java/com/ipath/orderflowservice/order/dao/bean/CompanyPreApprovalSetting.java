package com.ipath.orderflowservice.order.dao.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 行前审批规则
 * @author: qy
 * @create: 2024-04-03 17:20
 **/
@Data
public class CompanyPreApprovalSetting {


    private Long id;

    private Long companyId;

    private Long accountId;

    private Long sceneId;

    private Long scenePublishId;

    private String paraCode;

    private String paraName;

    private String paraValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validTo;

    private Boolean enabled;

    private Boolean isDelete;

    private Long deleteUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deleteTime;


}
