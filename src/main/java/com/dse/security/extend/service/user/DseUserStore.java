package com.dse.security.extend.service.user;

import com.dse.security.config.properties.ResourceServerProperties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DseUserStore implements InitializingBean {

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Autowired
    private PasswordEncoder dsePasswordEncoder;

    private Logger logger = LoggerFactory.getLogger(DseUserStore.class);

    public List<DseUserDetails> dseUsers = new ArrayList<>();

    public DseUserDetails geDseUser(String username) {
        for(DseUserDetails curUser : dseUsers) {
            if(StringUtils.equalsIgnoreCase(username,curUser.getUsername())) {
                return curUser;
            }
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("默认登录用户账号：xbird:123456:admin,commone,ROLE_USER");
        dseUsers.add(new DseUserDetails("xbird", dsePasswordEncoder.encode("123456"), true, true, true, true, AuthorityUtils.commaSeparatedStringToAuthorityList("admin,commone,ROLE_USER")));
        for(String curS : resourceServerProperties.getUsersInMemory().getUsers()) {
            String[] userInfoA = StringUtils.split(curS,":");
            if(userInfoA == null || userInfoA.length < 3) {
                throw new RuntimeException("用户登录信息配置有误，请参照：username:password:role1,role2[,....]");
            }
            dseUsers.add(new DseUserDetails(userInfoA[0], dsePasswordEncoder.encode(userInfoA[1]), true, true, true, true, AuthorityUtils.commaSeparatedStringToAuthorityList(userInfoA[2])));
        }
        logger.info("系统内存所有登录用户账号信息：" + dseUsers.toString());

    }
}
