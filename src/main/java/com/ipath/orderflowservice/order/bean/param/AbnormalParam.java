package com.ipath.orderflowservice.order.bean.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 合规预警参数
 */
@Data
public class AbnormalParam implements Serializable{
    /**
     * code
     */
    private Long companyId;

    /**
     * 名称中文
     */
    private String nameCn;

    /**
     * 提醒审批人
     */
    private String RemindApproverInfo;

    /**
     * 是否通知员工
     */
    private Boolean NotifyUser;

    /**
     * 关联场景主键列表
     */
    private List<Long> sceneIds;

    /**
     * 合规预警项
     */
    public List<AbnormalItem> AbnoramlItems;
}
