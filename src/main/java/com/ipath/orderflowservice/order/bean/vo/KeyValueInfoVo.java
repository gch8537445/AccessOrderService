package com.ipath.orderflowservice.order.bean.vo;

import lombok.Data;

@Data
public class KeyValueInfoVo {

    private String txt;
    private String color;

    public KeyValueInfoVo() {
    }

    public KeyValueInfoVo(String txt, String color) {
        this.txt = txt;
        this.color = color;
    }
}
