package com.dse.security.extend.service.user;

import com.dse.security.config.properties.ResourceServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户登录信息数据库方式保存
 */
@Component
public class DBLoadUserNameService extends AbstractLoadUserByUserNameService {

    @Autowired
    private DseUserDetailsDao dseUserDetailsDao;

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    public DBLoadUserNameService() {
        super("DB");
    }

    @Override
    public DseUserDetails doLoadUserByUsername(String username) {
        return dseUserDetailsDao.loadUserByUsername(username);
    }


}
