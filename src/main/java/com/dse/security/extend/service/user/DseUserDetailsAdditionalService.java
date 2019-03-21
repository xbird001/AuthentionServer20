package com.dse.security.extend.service.user;

import java.util.Map;

public interface DseUserDetailsAdditionalService {

    /**
     * 获取登录用户的扩展信息
     *  用户可通过扩展该接口
     * @param username
     * @return
     */
    Map<String,Object> getAdditional(String username);

}
