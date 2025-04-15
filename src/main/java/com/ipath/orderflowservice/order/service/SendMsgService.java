package com.ipath.orderflowservice.order.service;

import java.util.List;

public interface SendMsgService {

    /**
     * 发送邮件
     * @param subject
     * @param text
     * @param to
     * @param comId
     */
    void sendMail(String subject, String text, String to, Long comId);
}
