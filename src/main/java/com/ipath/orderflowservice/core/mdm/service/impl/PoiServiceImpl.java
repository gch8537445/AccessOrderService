package com.ipath.orderflowservice.core.mdm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ipath.common.bean.BaseResponse;
import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.core.mdm.bean.PoiInfo;
import com.ipath.orderflowservice.core.mdm.service.PoiService;
import com.ipath.orderflowservice.feignclient.RemoteCallFeign;
import com.ipath.orderflowservice.feignclient.dto.*;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.service.SendMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
public class PoiServiceImpl implements PoiService {

    public static final String SERVERNAME = "mdm-service";
    public static final String PATH_GETSTATIONPOILIST = "/api/v2/mdm/car/getstationpoilist";

    public static final String REDIS_GORDER_MDM_POI_TPOIID = "order:mdm:poi:tpoiid";

    @Value("${spring.profiles.active}环境 mdm-service 服务异常")
    private String subject;
    @Value("${sendMsg.mail.to}")
    private String to;

    @Autowired
    private SendMsgService sendMsgService;
    @Autowired
    private RemoteCallFeign remoteCallFeign;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public BaseResponse getStationPoiList() {

        try {
            RemoteCallDto remoteCallDto = new RemoteCallDto();
            JSONObject req = new JSONObject();
            remoteCallDto.setContent(req.toString());
            remoteCallDto.setPath(PATH_GETSTATIONPOILIST);
            BaseResponse response = remoteCallFeign.call(remoteCallDto);
            if (response.getCode() != 0) {
                log.info("getStationPoiList ==> serverName:{}, path:{}, retrun failed message:{}。", SERVERNAME, PATH_GETSTATIONPOILIST, response.getMessage());
                sendMsgService.sendMail(subject, PATH_GETSTATIONPOILIST + "返回错误信息：" + response.getMessage(), to, null);
                return null;
            }
            return response;
        } catch (Exception e) {
            log.info("getStationPoiList ==> serverName:{}, path:{}, exception message:{}。", SERVERNAME, PATH_GETSTATIONPOILIST, e);
            sendMsgService.sendMail(subject, PATH_GETSTATIONPOILIST + "异常：" + e.toString(), to, null);
        }
        return null;

    }

    @Override
    public Map<Object, Object> reOrderRedisStationPoi() {

        try {
            BaseResponse stationPoiList = this.getStationPoiList();
            if(null == stationPoiList){
                return null;
            }
            Object data = stationPoiList.getData();
            JSONArray array = JSONUtil.parseArray(data);
            JSONObject jsonObject = new JSONObject();

            for (Object o : array){
                PoiInfo poiInfo = BeanUtil.copyProperties(o, PoiInfo.class);
                jsonObject.set(poiInfo.getTPoiId(),poiInfo);
            }
            redisUtil.hashPutAll(REDIS_GORDER_MDM_POI_TPOIID, jsonObject, CacheConsts.STABLE_CACHE_EXPIRE_TIME);
            Map<Object, Object> map = redisUtil.hashGetAll(REDIS_GORDER_MDM_POI_TPOIID);
            return map;
        } catch (Exception e) {
            log.info("reOrderRedisStationPoi ==> exception message:{}。", e);
        }
        return null;

    }

    @Override
    public String getStationTypeByTpoiId(String tPoiId) {

        try {
            if(redisUtil.hashHasKey(REDIS_GORDER_MDM_POI_TPOIID,tPoiId)){
                Object o = redisUtil.hashGet(REDIS_GORDER_MDM_POI_TPOIID, tPoiId);
                PoiInfo poiInfo = BeanUtil.copyProperties(o, PoiInfo.class);
                return poiInfo.getStationType();
            }
        } catch (Exception e) {
            log.info("getStationTypeByTpoiId ==> exception message:{}。", e);
        }
        return null;

    }


}
