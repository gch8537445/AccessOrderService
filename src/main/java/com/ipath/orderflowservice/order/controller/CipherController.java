package com.ipath.orderflowservice.order.controller;

import com.ipath.common.bean.BaseResponse;
import com.ipath.orderflowservice.order.dao.bean.OrderBase;
import com.ipath.orderflowservice.order.service.CipherService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "加解密")
@RestController
@RequestMapping("/cipher")
@Deprecated
public class CipherController {

    @Autowired
    private CipherService cipherService;

    /**
     * 刷新缓存地址 解密与解密 公司
     */
    @GetMapping("/refreshCacheIsOpenAddressCipherCache")
    @Deprecated
    public BaseResponse refreshCacheIsOpenAddressCipherCache() throws Exception {
        List<Long> longs = cipherService.refreshCacheIsOpenAddressCipherCache();
        return BaseResponse.Builder.build(longs);
    }

    /**
     * 刷新缓存地址 解密与解密 公司
     */
    @PostMapping("/getOrderBase")
    @Deprecated
    public BaseResponse refreshCacheIsOpenAddressCipherCache(@RequestBody OrderBase orderBase) throws Exception {
        cipherService.addressDecryptOrderBase(orderBase);
        return BaseResponse.Builder.build(orderBase);
    }



}
