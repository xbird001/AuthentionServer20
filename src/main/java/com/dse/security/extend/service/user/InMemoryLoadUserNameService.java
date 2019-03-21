package com.dse.security.extend.service.user;

import com.dse.security.config.properties.ResourceServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 本地内存方式保存用户登录信息
 */
@Component
public class InMemoryLoadUserNameService extends AbstractLoadUserByUserNameService {

    @Autowired
    private DseUserStore dseUserStore;

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    public InMemoryLoadUserNameService() {
        super("LOCAL_MEMORY");
    }

    @Override
    public DseUserDetails doLoadUserByUsername(String username) {
        return dseUserStore.geDseUser(username);
    }

}
