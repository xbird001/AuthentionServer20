package com.dse.security.extend.service.user;


/**
 *
 */
public interface DseUserDetailsDao {

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    DseUserDetails loadUserByUsername(String username);
}
