package com.ipath.orderflowservice.order.bean.param;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 金杜参数
 * @author: qy
 * @create: 2024-03-22 10:12
 **/
@ApiModel(value = "金杜project参数")
@Data
public class JinduProjectParam {
    @ApiModelProperty(value = "当前页")
    private Integer pageNum;
    @ApiModelProperty(value = "每页容量")
    private Integer pageSize;

    @ApiModelProperty(value = "企业id")
    private Long companyId;

    @ApiModelProperty(value = "成本中心code")
    private String code;

    @ApiModelProperty(value = "企业代码")
    private Long companyCode;

    @ApiModelProperty(value = "1: 成本中心 2: 案件 默认 1")
    private Integer status;

    public List<String> check(){
        List<String> error = new ArrayList<>();
        if (ObjectUtil.isEmpty(companyId)){
            error.add("企业id不能为空");
        }
        if (ObjectUtil.isEmpty(code)){
            error.add("成本中心code不能为空");
        }
        if (ObjectUtil.isEmpty(companyCode)){
            error.add("企业代码不能为空");
        }
        if (ObjectUtil.isEmpty(status)){
            status = 1;
        }
        if (ObjectUtil.isEmpty(pageNum)){
            pageNum = 1;
        }
        if (ObjectUtil.isEmpty(pageSize)){
            pageSize = 10;
        }
        return error;
    }
}
