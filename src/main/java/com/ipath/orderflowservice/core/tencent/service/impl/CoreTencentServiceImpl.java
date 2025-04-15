package com.ipath.orderflowservice.core.tencent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.core.tencent.bean.LocationReq;
import com.ipath.orderflowservice.core.tencent.bean.LocationResResult;
import com.ipath.orderflowservice.core.tencent.bean.TrafficInfo;
import com.ipath.orderflowservice.core.tencent.constant.tencentPahtConstant;
import com.ipath.orderflowservice.core.tencent.service.CoreTencentService;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.RemoteCallDto;
import com.ipath.orderflowservice.feignclient.dto.TencentMapSearchReqDto;
import com.ipath.orderflowservice.feignclient.dto.TencentMapSearchResData;
import com.ipath.orderflowservice.feignclient.dto.TencentMapSearchResDto;
import com.ipath.orderflowservice.ka.remoteCall.service.RemoteCallService;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * core端腾讯服务 service
 */
@Service
@Slf4j
public class CoreTencentServiceImpl implements CoreTencentService {

    @Autowired
    private RemoteCallFeign remoteCallFeign;
    @Autowired
    private RemoteCallService remoteCallService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 判断点位范围内是否存在交通枢纽
     * @param lat
     * @param lng
     * @param radius 米
     * @return
     */
    @Override
    public boolean hasTrafficHub(String lat, String lng, int radius) {
        DecimalFormat df = new DecimalFormat("0.000000");
        lat = df.format(Double.valueOf(lat));
        lng = df.format(Double.valueOf(lng));
        boolean isTrafficHub = true;
        String key = "CORETENCENT:POINT:R:HASTRAFFICHUB";
        String item = lat + ":" + lng + ":" + radius;
        boolean has = redisUtil.hashHasKey(key, item);
        if (has) {
            isTrafficHub = (boolean) redisUtil.hashGet(key, item);
        } else {
            TencentMapSearchResDto tencentMapSearchResDto = this.tencentMapSearch(lat, lng, radius);
            if(null == tencentMapSearchResDto){
                return isTrafficHub;
            }
            List<TencentMapSearchResData> data =tencentMapSearchResDto.getData();
            if (null != data && data.size() > 0) {
                isTrafficHub = true;
            } else {
                isTrafficHub = false;
            }
            redisUtil.hashPut(key, item, isTrafficHub, CacheConsts.ONE_MONTH);
        }
        return isTrafficHub;
    }

    @Override
    public LocationResResult location(String location) throws Exception {
        try {
            LocationReq locationReq = new LocationReq();
            locationReq.setLocation(location);
            BaseResponse response = remoteCallService.call(JSONUtil.toJsonStr(locationReq), tencentPahtConstant.TENCENT_PATH_LOCATION);
            if(response.getCode() == 0){
                LocationResResult locationResResult = BeanUtil.copyProperties(response.getData(), LocationResResult.class);
                return locationResResult;
            }
        }catch (Exception e){
            log.error("location <===> 异常:{}", e);
        }
        return null;
    }

    @Override
    public TrafficInfo getTrafficInfoByPoint(String lat, String lng) throws Exception {
        DecimalFormat df = new DecimalFormat("0.000000");
        lat = df.format(Double.valueOf(lat));
        lng = df.format(Double.valueOf(lng));
        String key = "TRAFFICINFO:POINT";
        String item = lat + "_" + lng;
        TrafficInfo result = new TrafficInfo();
        result.setCode("0");
        result.setName("普通地点");
        try {

            if (redisUtil.hashHasKey(key, item)) {
                Object o = redisUtil.hashGet(key, item);
                result = JSONUtil.toBean(JSONUtil.parseObj(o),TrafficInfo.class);
                return result;
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("lat", lat);
                jsonObject.set("lng", lng);
                BaseResponse response = remoteCallService.call(jsonObject.toString(), tencentPahtConstant.TENCENT_PATH_GETTRAFFICINFOBYPOINT);
                if (response.getCode() == 0) {
                    result = BeanUtil.copyProperties(response.getData(), TrafficInfo.class);
                    if(!StringUtils.equals(result.getCode(),"0")){
                        redisUtil.hashPut(key, item, JSONUtil.toJsonStr(result) , CacheConsts.ONE_MONTH);
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("getTrafficInfoByPoint <===> 异常:{}", e);
        }
        return result;
    }

    /**
     * 腾讯map地点搜索
     */
    private TencentMapSearchResDto tencentMapSearch(String lat, String lng, int radius){
        //base地限制判断半径内有交通枢纽
        TencentMapSearchReqDto tencentMapSearchReqDto = new TencentMapSearchReqDto();
        tencentMapSearchReqDto.setBoundary("nearby(" + lat + "," + lng + "," + (radius < 1000 ? 1000 : radius) + ",1)");
        //tencentMapSearchReqDto.setFilter("category=飞机场,火车站,港口码头");
        tencentMapSearchReqDto.setFilter("category=飞机场,火车站");
        RemoteCallDto remoteCallDto = new RemoteCallDto();
        remoteCallDto.setPath(tencentPahtConstant.TENCENT_PATH_SEARCH);
        remoteCallDto.setContent(JSONUtil.toJsonStr(tencentMapSearchReqDto));
        BaseResponse response = remoteCallFeign.call(remoteCallDto);
        if (response.getCode() != 0) {
            log.error("setOrderParamCheckBase ===> 异常：", response.getMessage());

        } else {
            TencentMapSearchResDto tencentMapSearchResDataDto = JSONUtil.toBean(JSONUtil.toJsonStr(response.getData()), TencentMapSearchResDto.class);
            List<TencentMapSearchResData> data = tencentMapSearchResDataDto.getData();
            int finalR = radius;
            data = data.stream().filter(o -> o.get_distance() <= finalR).collect(Collectors.toList());
            tencentMapSearchResDataDto.setData(data);
            return tencentMapSearchResDataDto;
        }
        return null;
    }


}