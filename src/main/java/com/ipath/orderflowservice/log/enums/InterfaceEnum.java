package com.ipath.orderflowservice.log.enums;

public enum InterfaceEnum {
        ORDER_ESTIMATE(
                        "access_order_estimate",
                        "预估",
                        "order.estimate",
                        "预估_ka",
                        "post",
                        "3"),
        ORDER_CHANGE_DEST_ESTIMATE(
                        "access_order_estimate",
                        "预估",
                        "order.changeDestEstimate",
                        "修改目的地预估_ka",
                        "post",
                        "3"),
        ORDER_GETSCENEINFO(
                        "access_order_getSceneInfo",
                        "查询场景详情",
                        "configuration.scene.getSceneInfo",
                        "查询场景详情",
                        "post",
                        "3"),
        ORDER_PLACEORDER(
                        "access_order_placeOrder",
                        "下单",
                        "order.placeOrder",
                        "下单_ka",
                        "post",
                        "3"),
        ORDER_APPEND_PLACEORDER(
                        "access_order_placeOrder",
                        "下单",
                        "order.appendPlaceOrder",
                        "追加车型下单_ka",
                        "post",
                        "3"),
        ORDER_CHAGE_DEST_PLACEORDER(
                        "access_order_placeOrder",
                        "下单",
                        "order.changeDest",
                        "修改目的地下单_ka",
                        "post",
                        "3"),
        INFO_DISPATCH(
                        "access_order_placeOrder_dispatch",
                        "极速派单消息",
                        "info.dispatch",
                        "极速派单消息",
                        "post",
                        "2"),
        LABEL_COMPLETE_HIT_RESULT(
                        "access_order_hit_label_result",
                        "完单标签命中结果",
                        "order.hitlabelresult",
                        "标签命中结果",
                        "post",
                        "3"),
        CORE_ORDER_ESTIMATE(
                        "access_order_estimate",
                        "预估",
                        "ordercore.estimate",
                        "预估_core",
                        "post",
                        "3"),
        CORE_ORDER_CHANGE_DEST_ESTIMATE(
                        "access_order_estimate",
                        "预估",
                        "ordercore.changeDestEstimate",
                        "修改目的地预估_core",
                        "post",
                        "3"),
        CORE_ORDER_PLACEORDER(
                        "access_order_placeOrder",
                        "下单",
                        "ordercore.placeOrder",
                        "下单_core",
                        "post",
                        "3"),
        CORE_ORDER_CHANGEDEST(
                        "access_order_placeOrder",
                        "下单",
                        "ordercore.changeDest",
                        "修改目的地下单_core",
                        "post",
                        "3"),
        CORE_ORDER_APPEND_PLACEORDER(
                        "access_order_placeOrder",
                        "下单",
                        "ordercore.appendPlaceOrder",
                        "追加车型下单_core",
                        "post",
                        "3"),
        RECEIVE_CORE_ORDER_STATUS(
                        "KA_ORDER_REC_CORE_ORDER",
                        "接收中台状态",
                        "order.notifyOrderStatus",
                        "中台状态:",
                        "post",
                        "1"),
        RECEIVE_APP_CANCELORDER(
                        "KA_ORDER_REC_APP_CANCELORDER",
                        "取消订单",
                        "order.cancelOrder",
                        "取消订单_ka",
                        "post",
                        "3"),
        RECEIVE_APP_CANCELORDERFOROPE(
                        "KA_ORDER_REC_APP_CANCELORDER",
                        "取消订单",
                        "order.cancelOrderForOpe",
                        "取消订单_运营",
                        "post",
                        "1"),
        RECEIVE_BILL_PAY(
                        "KA_ORDER_REC_PAY_NOTIFY",
                        "支付中心回调",
                        "order.orderPayNotify",
                        "支付中心回调",
                        "post",
                        "3"),
        RECEIVE_BILL_SETTLEMENT(
                        "KA_ORDER_REC_SETTLE_NOTIFY",
                        "结算回调",
                        "order.settle.callback",
                        "结算回调",
                        "post",
                        "1"),
        ORDER_CORE_CANCEL_ORDER(
                        "ka_order_cancelOrder",
                        "取消订单",
                        "ordercore.cancelOrder",
                        "取消订单_core",
                        "post",
                        "3"),
        COUPON_GET_COUPONS_OF_USER(
                        "order_couponconsume_getcouponsofuser",
                        "获取用户优惠券",
                        "couponconsume.getcouponsofuser",
                        "获取用户优惠券",
                        "post",
                        "3"),
        BOOKING_CALLBACK(
                        "order_booking_callback",
                        "预约管家通知",
                        "order.booking.callback",
                        "预约管家通知",
                        "post",
                        "1"),
        CORE_ORDER_SELECT_TAKE_ORDER(
                        "core_order_select_take_order",
                        "双选司机确认",
                        "ordercore.selectTakeOrder",
                        "通知中台前端选中的司机",
                        "post",
                        "3"),
        ORDER_CONFIGURATION_CHECK_ORDER(
                        "check_order",
                        "下单校验",
                        "configuration.common.checkOrder",
                        "下单校验",
                        "post",
                        "3"),
        ORDER_BILL_GET_DEFAULT_ACCOUNT(
                        "bill_getcompanydefaultaccount",
                        "获取默认账户",
                        "bill.getcompanydefaultaccount",
                        "获取默认账户",
                        "post",
                        "3"),
        ORDER_BILL_SAVE_ACCOUNT_USER(
                        "bill_getcompanydefaultaccount",
                        "保存用户和账户关系",
                        "bill.companyaccount.saveaccountuser",
                        "保存用户和账户关系",
                        "post",
                        "3"),
        ORDER_SYSTEM_GET_UPGRADE(
                        "get_upgrade",
                        "获取用户免费升舱信息",
                        "system.freeupgrade.getuserupgrade",
                        "获取用户免费升舱信息",
                        "post",
                        "3"),
        ORDER_WARNING_COMMON_MESSAGE(
                        "order.warning.querydb",
                        "查询数据库警告",
                        "order.warning.querydb",
                        "查询数据库警告",
                        "post",
                        "1"),
        CHANGE_COUPON_STATUS(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "couponconsume.ChangeCouponStatus",
                        "更改优惠券使用状态",
                        "post",
                        "3"),
        NOTIFY_BOOKING_START_SERVICE(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "booking.StartService",
                        "通知预约管家开始服务",
                        "post",
                        "3"),
        NOTIFY_BOOKING_CANCEL_FEE(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "booking.ChangeCancleFee",
                        "通知预约管家取消费",
                        "post",
                        "3"),
        NOTIFY_BOOKING_CANCEL_ORDER(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "booking。CancleService",
                        "通知预约管家取消订单",
                        "post",
                        "3"),

        ORDER_SYSTEM_UPDATE_USAGE_COUNT(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "system.FreeUpgrade.FUpdateUsageCount",
                        "更新免费升舱使用次数",
                        "post",
                        "3"),
        ORDER_SYSTEM_ADD_USAGE_RECORD(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "system.FreeUpgrade.AddUsageRecord",
                        "添加免费升舱使用记录",
                        "post",
                        "3"),
        CORE_BILL_SETTLE_ORDERSETTLE(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "bill.settle.orderSettle",
                        "ka端通知结算",
                        "post",
                        "3"),
        ORDER_NOTIFY_BILL_DELAY_CANCEL(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "bill.settle.orderSettle.delaySettle.cancel",
                        "ka端通知结算取消延时",
                        "post",
                        "3"),
        ORDER_SYSTEM_UPDATE_PRE_DEPART_APPLY_STATE(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "system.preApproval.UpdatePreDepartApplyState",
                        "更新行前申请单状态",
                        "post",
                        "3"),
        ORDER_NOTIFY_CONFIGURATION_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_configuration",
                        "通知配置服务",
                        "post",
                        "1"),
        ORDER_NOTIFY_COUPON_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_coupon",
                        "通知优惠券服务",
                        "post",
                        "1"),
        ORDER_NOTIFY_CDS_STATUS_CHG_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_cds",
                        "通知cds服务",
                        "post",
                        "1"),
        ORDER_BRIDGE_PUBLISH_MESSAGE(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "bridge.publishmessage",
                        "通过桥接服务发消息",
                        "post",
                        "3"),
        ORDER_NOTIFY_REPORT_COMPLETION_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_report_completion_ka",
                        "完单通知报表",
                        "post",
                        "1"),
        ORDER_NOTIFY_REPORT_COMPLETION(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "report.compensation.handleCompletedOrder",
                        "完单通知报表",
                        "post",
                        "3"),
        ORDER_NOTIFY_REPORT_PAID_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_report_pay_ka",
                        "支付通知报表",
                        "post",
                        "1"),
        ORDER_NOTIFY_REPORT_PAID(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "report.compensation.handlePay",
                        "支付通知报表",
                        "post",
                        "3"),
        ORDER_NOTIFY_REPORT_APPEND_PLACE_ORDER_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_report_append_place_order_ka",
                        "追加下单通知报表",
                        "post",
                        "1"),
        ORDER_NOTIFY_REPORT_APPEND_PLACE_ORDER(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "report.compensation.appendPlaceOrder",
                        "追加下单通知报表",
                        "post",
                        "3"),
        ORDER_NOTIFY_REPORT_CANCEL_ORDER_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_report_cancel_order_ka",
                        "取消订单通知报表",
                        "post",
                        "1"),
        ORDER_NOTIFY_REPORT_CANCEL_ORDER(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "report.compensation.cancelOrder",
                        "取消订单通知报表",
                        "post",
                        "3"),
        ORDER_NOTIFY_REPORT_CHANGE_DEST_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_report_change_dest_ka",
                        "修改目的地通知报表",
                        "post",
                        "1"),
        ORDER_NOTIFY_REPORT_CHANGE_DEST(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "report.compensation.changeDest",
                        "修改目的地通知报表",
                        "post",
                        "3"),
        ORDER_NOTIFY_REPORT_COMPLAINT_MQ(
                        "order.notify.mq",
                        "订单对外通知_mq消息",
                        "access_order_notify_report_complaint_ka",
                        "投诉通知报表",
                        "post",
                        "1"),
        ORDER_NOTIFY_REPORT_COMPLAINT(
                        "order.notify.interface",
                        "订单对外通知_接口调用",
                        "report.compensation.handleComplaint",
                        "投诉通知报表",
                        "post",
                        "3"),
        ORDER_ERROR_COMMON_MESSAGE(
                        "order.error.common",
                        "订单执行逻辑错误",
                        "order.error.checkAbnormalOrder",
                        "检查异常订单",
                        "post",
                        "1"),
        ORDER_RUNTIME_EXCEPTION(
                        "order.error.runtime",
                        "运行时异常",
                        "order.error.",
                        "运行时异常:",
                        "post",
                        "1");

        /** 日志编码 */
        private String logCode;
        /** 日志名称 */
        private String logName;
        /** 接口路径 */
        private String interfacePath;
        /** 接口名称 */
        private String interfaceName;
        /** 接口请求方法 */
        private String method;
        /** 接口类型：1-请求 2-返回 3-请求+返回 */
        private String type;

        InterfaceEnum(String logCode, String logName, String interfacePath, String interfaceName, String method,
                        String type) {
                this.type = type;
                this.method = method;
                this.interfaceName = interfaceName;
                this.interfacePath = interfacePath;
                this.logName = logName;
                this.logCode = logCode;
        }

        public String getLogCode() {
                return logCode;
        }

        public void setLogCode(String logCode) {
                this.logCode = logCode;
        }

        public String getLogName() {
                return logName;
        }

        public void setLogName(String logName) {
                this.logName = logName;
        }

        public String getInterfacePath() {
                return interfacePath;
        }

        public void setInterfacePath(String interfacePath) {
                this.interfacePath = interfacePath;
        }

        public String getInterfaceName() {
                return interfaceName;
        }

        public void setInterfaceName(String interfaceName) {
                this.interfaceName = interfaceName;
        }

        public String getMethod() {
                return method;
        }

        public void setMethod(String method) {
                this.method = method;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

}
