package com.ipath.orderflowservice.order.bean.constant;

/**
 * 公司redis配置
 */
public class CompanyConstant {

    /**
     * 开关
     */
    //开启地址加密公司列表
    public static final String COMPANY_ONOFF_ADDRESS_CIPHER = "company:onoff:is:open:address:cipher";


    /**
     * 配置
     */
    //平台取消重新派单公司配置
    public static final String COMPANY_CONFIG_REPLACE_ORDER_AFTER_CORE_CANCEL = "company:config:replace:order:after:core:cancel";
    //公司节假日配置
    public static final String COMPANY_CONFIG_HOLIDAYS = "company:config:holidays";


    /**
     * 类型
     */
    //平台取消重新派单公司配置类型
    public static final String COMPANY_CONFIG_REPLACE_ORDER_AFTER_CORE_CANCEL_TYPE = "replaceOrderAfterCoreCancel";
    //开启地址加密公司类型
    public static final String COMPANY_ONOFF_ADDRESS_CIPHER_TYPE = "isOpenAddressCipher";

}
