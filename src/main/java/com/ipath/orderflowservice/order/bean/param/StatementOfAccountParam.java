package com.ipath.orderflowservice.order.bean.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: qy
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "对账单信息参数")
public class StatementOfAccountParam {

    @ApiModelProperty(value = "账单主体id")
    private Long companyId;

    @ApiModelProperty(value = "开始时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    @ApiModelProperty(value = "结束时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty(value = "导出维度 0 订单时间 , 1 审批时间 , 2 行程结束时间 , 3 行程开始时间")
    private Integer exportDimensions;

}
