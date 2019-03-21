package com.dse.security.config;

import com.dse.security.config.properties.ResourceServerProperties;
import com.dse.security.extend.service.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

@Configuration
public class DseBeanConfig {

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Autowired
    private DseUserStore dseUserStore;

    @Autowired
    private List<LoadUserByUserNameService> loadUserByUserNameServiceList;


    /**
     * com.dse.as.sessionType.type = 1 返回深圳水资源所需的session信息
     * @param jdbcTemplate
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "com.dse.as.sessionType",name = "type", havingValue = "1")
    public DseUserDetailsAdditionalService dseUserDetailsAdditionalService(JdbcTemplate jdbcTemplate) {
        return new DseSZYUserDetailsAdditionalService(jdbcTemplate);
    }

    /**
     * com.dse.as.sessionType.type = 0 返回默认的session信息
     * @param jdbcTemplate
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "com.dse.as.sessionType",name = "type", havingValue = "0",matchIfMissing = true)
    public DseUserDetailsAdditionalService defaultDseUserDetailsAdditionalService(JdbcTemplate jdbcTemplate) {
        return new DefaultDseUserDetailsAdditionalService(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "dseUserDetailsService")
    public UserDetailsService dseUserDetailsService(DseUserDetailsAdditionalService dseUserDetailsAdditionalService) {
        DseUserDetailsService dseUserDetailsService = new DseUserDetailsService(resourceServerProperties, dseUserStore, dseUserDetailsAdditionalService/* == null ? new DseSZYUserDetailsAdditionalService(jdbcTemplate) : dseUserDetailsAdditionalService*/);
        dseUserDetailsService.setLoadUserByUserNameServiceList(loadUserByUserNameServiceList);
        return dseUserDetailsService;
    }



}
