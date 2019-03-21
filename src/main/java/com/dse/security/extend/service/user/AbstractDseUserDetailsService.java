package com.dse.security.extend.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class AbstractDseUserDetailsService implements UserDetailsService {

    private DseUserDetailsAdditionalService dseUserDetailsAdditionalService;

    protected abstract DseUserDetails doLoadUserByUsername(String username);

    public AbstractDseUserDetailsService() {
    }

    public AbstractDseUserDetailsService(DseUserDetailsAdditionalService dseUserDetailsAdditionalService) {
        this.dseUserDetailsAdditionalService = dseUserDetailsAdditionalService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DseUserDetails user = doLoadUserByUsername(username);

        DseUserDetails user4Rs = null;

        if (user == null) {
            throw new UsernameNotFoundException("指定username：" + username + "用户不存在!");
        }

        user4Rs = new DseUserDetails(user.getUsername(), user.getPassword(), true, true, true, true, user.getAuthorities());
        try {
            //捕获全部的异常情况，以免影响后面token的签发
            user4Rs.setAdditional(dseUserDetailsAdditionalService.getAdditional(username));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user4Rs;
    }

    public DseUserDetailsAdditionalService getDseUserDetailsAdditionalService() {
        return dseUserDetailsAdditionalService;
    }

    public void setDseUserDetailsAdditionalService(DseUserDetailsAdditionalService dseUserDetailsAdditionalService) {
        this.dseUserDetailsAdditionalService = dseUserDetailsAdditionalService;
    }
}
