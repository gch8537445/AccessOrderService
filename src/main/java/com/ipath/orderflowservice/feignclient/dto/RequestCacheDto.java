package com.ipath.orderflowservice.feignclient.dto;

import lombok.Data;

/**
 * 获取缓存dto
 */
@Data
public class RequestCacheDto {
    /**
     * 缓存key
     * 是否必传：是
     */
    private String key;

    /**
     * key参数值， 替换key中的 “{}”
     * 例如：配置表中key定义icar:order:usually:location:{}，此参数替换key中的 “{}”
     * 是否必传：否
     */
    private String keyParameterValue;

    /**
     * sql参数值， 多个使用“,“分割，替换sql中的 “{#0}”，sql第一个参数值{#0}，按顺序依次增加
     * 是否必传：否
     */
    private String sqlParameterValue;

    /**
     * type = hash类型时传，当hash类型没有传则返回全部
     * 是否必传：否
     */
    private String item;

    /**
     * 过期时间
     */
    private String timeout;

    /**
     * 保存的数据
     */
    private Object data;
}
