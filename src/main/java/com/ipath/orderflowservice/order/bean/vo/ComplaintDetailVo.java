package com.ipath.orderflowservice.order.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ComplaintDetailVo{
    /**
         * 投诉级别
         */
        private Short level;

        /**
         * 投诉标签
         */
        private List<String> ComplaintLabels;

        /**
         * 受理状态 未处理=0,处理中=1,处理完成=2
         */
        private int State;

        /**
         * 投诉内容
         */
        private String Feedback;

        /**
         * 投诉时间
         *
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
        private Date feedbackTime;

        /**
         * 客户开始处理时间
         *
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
        private Date startProcessTime;

        /**
         * 处理结束时间
         *
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") // 输出参数格式
        private Date replyTime;

        /**
         * 处理结果
         */
        private String reply;
}
