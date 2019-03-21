package com.dse.security.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(ResourceServerProperties.class)
@Configuration
public class ResourceServerPropertiesConfig {

}
