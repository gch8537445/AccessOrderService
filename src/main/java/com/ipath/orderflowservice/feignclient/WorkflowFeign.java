package com.ipath.orderflowservice.feignclient;

import cn.hutool.json.JSONObject;
import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.dto.RequestApproveDto;
import com.ipath.orderflowservice.feignclient.dto.RequestProcessPathDto;
import com.ipath.orderflowservice.feignclient.dto.RequestSubmitApplyDto;
import com.ipath.orderflowservice.feignclient.dto.RequestGetApproveListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "workflow-service")
public interface WorkflowFeign {
    
    /**
     * 开始审核流程
     */
    @RequestMapping(value = "/api/v2/wf/Process/Submit", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse Submit(@RequestBody RequestSubmitApplyDto requestSumbitApplyDto);

    /**
     * 通过审核
     */
    @RequestMapping(value = "/api/v2/wf/Process/Approve", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse Approve(@RequestBody RequestApproveDto requestApproveDto);
    
    /**
     * 拒绝申请
     */
    @RequestMapping(value = "/api/v2/wf/Process/Reject", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse Reject(@RequestBody RequestApproveDto requestApproveDto);

    /**
     * 查询审批记录
     */
    @RequestMapping(value = "/api/v2/wf/Process/getapprovalflowinfolist", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse GetApprovalList(@RequestBody RequestGetApproveListDto requestGetApproveListDto );

    /**
     * 查询审批路径
     */
    @RequestMapping(value = "/api/v2/wf/Process/getprocessdefinepath", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse getProcessPath(@RequestBody RequestProcessPathDto requestProcessPathDto );

    /**
     * 获取订单审批记录
     * key:orderId
     */
    @RequestMapping(value = "/wf/process/records", method = RequestMethod.POST, headers = {"content-type=application/json"}, consumes = "application/json")
    BaseResponse getProcessRecords(@RequestBody JSONObject jsonObject);
}
