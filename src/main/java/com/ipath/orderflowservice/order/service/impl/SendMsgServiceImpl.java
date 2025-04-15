package com.ipath.orderflowservice.order.service.impl;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.feignclient.MessageFeign;
import com.ipath.orderflowservice.feignclient.dto.SendMailDto;
import com.ipath.orderflowservice.order.service.SendMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SendMsgServiceImpl implements SendMsgService {

	@Autowired
	private MessageFeign messageFeign;

    /**
     * 发送邮件
     * @param subject
     * @param text
     * @param to
     * @param comId
     */
    @Override
    public void sendMail(String subject, String text, String to, Long comId){
        SendMailDto mimeMessage = new SendMailDto();
        mimeMessage.setToEmail(to);
        mimeMessage.setTitle(subject);
        mimeMessage.setBodyInfo(text);
        mimeMessage.setCompanyId(comId);
        try {
            BaseResponse br = messageFeign.sendByEmail(mimeMessage);
            if(br.getCode() == 0) {
                log.info("sendMail ===> 成功");
            }else {
                log.error("sendMail ===> 失败:{},"+br.getMessage());
            }

        }catch(Exception e){
            log.error("sendMail ===> 异常:{},"+e.getMessage());
        }
    }
}
