package com.dse.security.extend.service.user;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * 默认的构建session信息
 */
public class DefaultDseUserDetailsAdditionalService extends AbstractDseUserDetailsAdditionalService {

    public DefaultDseUserDetailsAdditionalService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected Map<String, Object> doAdditional(Map<String, Object> userInfo) {
        return userInfo;
    }
}
