package com.dse.security.extend.service.client;

import com.dse.security.config.properties.ClientRetistryType;
import com.dse.security.config.properties.ResourceServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

@Configuration
public class ClientDetailsServiceConfig {

    @Autowired
    private DseClientStore dseClientStore;

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    @Autowired(required = false)
    private DataSource dataSource;

    @Bean
    @ConditionalOnMissingBean(name="dseClientDetailsService")
    public ClientDetailsService dseClientDetailsService() {
        if(ClientRetistryType.LOCAL_MEMORY.equals(resourceServerProperties.getClientRetistryType())) {
            InMemoryClientDetailsService inMemoryClientDetailsService = new InMemoryClientDetailsService();
            inMemoryClientDetailsService.setClientDetailsStore(dseClientStore.getDseClient());
            return inMemoryClientDetailsService;
        } else {
            return new JdbcClientDetailsService(dataSource);
        }
    }

}
