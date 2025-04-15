package com.ipath.orderflowservice.log.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Log {

    private static final long serialVersionUID = 1L;

    /** 日志id */
    private Long id;

    /** 公司id */
    private Long companyId;

    /** 公司名称 */
    private String companyName;

    /** 用户id */
    private Long userId;

    /** 用户名称 */
    private String userName;

    /** 订单id */
    private Long orderId;

    /** 日志编码 */
    private String logCode;

    /** 日志名称 */
    private String logName;

    /** 状态：1-正常 2-警告 3-异常 */
    private Integer state;

    /** 服务编码 */
    private String srevice;

    /** 服务名称 */
    private String sreviceName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    private Date[] createdTimeArr;

    /** 接口路径 */
    private String interfacePath;

    /** 接口名称 */
    private String interfaceName;

    /** 接口路径（外部） */
    private String externalSystemInterfacePaht;

    /** 接口名称（外部） */
    private String externalSystemInterfaceName;
    /** 接口返回code（外部） */
    private String externalSystemInterfaceReturnCode;

    /** 接口请求方法 */
    private String method;

    /** 接口类型：1-请求 2-返回 3-请求+返回 */
    private String type;

    /** 接口请求参数 */
    private String body;
    /** 接口返回参数 */
    private String resBody;

    private String errorMsg;

    /** 接口日志时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date interfaceTime;
    /** 日志开始时间时间戳 毫秒*/
    private Long  endTime = 0L;
    /** 日志结束时间戳 毫秒*/
    private Long  startTime = 0L;
    /** 接口响应毫秒数 */
    private Long resMillsecond;

    /** 映射id，预估时，传预估id即可*/
    private String mappingId;

    /**trace id */
    private String traceId;
}
