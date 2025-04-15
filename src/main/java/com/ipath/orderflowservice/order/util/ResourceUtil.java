package com.ipath.orderflowservice.order.util;

import com.ipath.common.util.RedisUtil;
import com.ipath.orderflowservice.order.bean.constant.CacheConsts;
import com.ipath.orderflowservice.order.bean.vo.CacheUserInfo;
import com.ipath.orderflowservice.order.service.CacheService;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResourceUtil {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CacheService cacheService;

    /**
     * 获取资源文件
     * 暂时不做企业区分
     * 
     * @param userId      用户主键
     * @param companyId   公司主键
     * @param lanaguage   语言
     * @param resourceKey 资源key
     * @return 资源字符串
     */
    public String getResource(Long userId, Long companyId, int lanaguage, String resourceKey) {
        try {
            if (lanaguage == 0) {
                CacheUserInfo userInfo = cacheService.getUserInfo(companyId, userId);
                if (null == userInfo || null == userInfo.getLanguage() || 0 == userInfo.getLanguage()) {
                    lanaguage = 1;
                } else {
                    lanaguage = userInfo.getLanguage();
                }
            }
            lanaguage = lanaguage == 0 ? 1 : lanaguage;

            if (!redisUtil.hashHasKey(CacheConsts.REDIS_KEY_COMMON_RESOUCE + resourceKey, String.valueOf(lanaguage)))
                return null;

            return (String) redisUtil.hashGet(CacheConsts.REDIS_KEY_COMMON_RESOUCE + resourceKey,
                    String.valueOf(lanaguage));
        } catch (Exception ex) {
            log.error(
                    "获取资源文件出现异常【ResourceUtil->getResource,companyId:{},userId:{},resourceKey:{}】", companyId, userId,
                    resourceKey, ex);
        }

        return resourceKey;
    }
}
