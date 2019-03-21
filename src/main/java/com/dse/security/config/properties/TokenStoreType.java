package com.dse.security.config.properties;

/**
 * TOKEN的保存类型
 *  LOCAL_MEMORY:本地内存，适用于单机开发环境
 *  REDIS:redis缓存，适用于生产集群环境
 */
public enum TokenStoreType {

    LOCAL_MEMORY,

    REDIS
}
