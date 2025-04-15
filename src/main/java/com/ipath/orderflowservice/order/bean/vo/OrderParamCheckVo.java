package com.ipath.orderflowservice.order.bean.vo;


import lombok.Data;

import java.io.Serializable;


@Data
public class OrderParamCheckVo implements Serializable {

    //参数校验编码
    private String code;
    //参数校验名称
    private String name;
    private String remark;
    //提示信息 (暂时不启动)
    private OrderParamCheckMsgVo msg;
    //确定按钮 (暂时不启动)
    private String confirmText;
    //取消按钮 (暂时不启动)
    private String cancelText;
    //是否弹出提示 true:显示  false:不显示
    private boolean show;

}
