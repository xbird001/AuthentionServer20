package com.dse.security.extend.service.user;

import com.dse.security.config.properties.UserRegistryType;

public interface LoadUserByUserNameService {

    /**
     * 获取登录用户信息
     * @param username
     * @return
     */
    DseUserDetails doLoadUserByUsername(String username);

    /**
     * 是否支持指定的登录用户信息的加载方式
     * @return
     */
    boolean isSupport(UserRegistryType userRegistryType);
}
