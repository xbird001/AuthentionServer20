package com.dse.security.config.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 本机内存模式下登录用户信息，格式如下：
 *  username:password:role1,role2[,....]
 */
public class DseUserInMemeroy{

    private Logger logger = LoggerFactory.getLogger(DseUserInMemeroy.class);

    private List<String> users = new ArrayList<>();

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

}
