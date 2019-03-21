package com.dse.security.extend.service.client;

import com.dse.security.config.properties.ResourceServerProperties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方APP客户端信息内存模式存储
 */
@Component
public class DseClientStore implements InitializingBean {

    @Autowired
    private ResourceServerProperties resourceServerProperties;

    private Logger logger = LoggerFactory.getLogger(DseClientStore.class);

    public Map<String, ClientDetails> dseClient = new HashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("默认的第三方客户端账号信息：dse1:123456:authorization_code,client_credentials,implicit,refresh_token,password:app:3600:7200");
        BaseClientDetails baseClientDetails = new BaseClientDetails("dse1", null, "app", "authorization_code,client_credentials,implicit,refresh_token,password", null);
        baseClientDetails.setClientSecret("dse123456");
        baseClientDetails.setAccessTokenValiditySeconds(3600);
        baseClientDetails.setRefreshTokenValiditySeconds(baseClientDetails.getAccessTokenValiditySeconds() * 2);
        dseClient.put(baseClientDetails.getClientId(),baseClientDetails);
        for (String curS : resourceServerProperties.getClientInMemeroy().getClients()) {
            String[] userInfoA = StringUtils.split(curS, ":");
            if (userInfoA == null || userInfoA.length < 6) {
                throw new RuntimeException("第三方APP账户信息配置错误，请参照：dse1:123456:authorization_code,client_credentials,implicit,refresh_token,password:app:3600:7200");
            }
            BaseClientDetails baseClientDetails4 = new BaseClientDetails(userInfoA[0], null, userInfoA[3], userInfoA[2], null);
            baseClientDetails4.setClientSecret(userInfoA[1]);
            baseClientDetails4.setAccessTokenValiditySeconds(Integer.valueOf(userInfoA[4]));
            baseClientDetails4.setRefreshTokenValiditySeconds(Integer.valueOf(userInfoA[5]));
            dseClient.put(baseClientDetails4.getClientId(),baseClientDetails4);
        }
        logger.info("系统内存所有登录用户账号信息：" + dseClient.toString());
    }


    @Override
    public String toString() {
        return "DseClientStore{" +
                "resourceServerProperties=" + resourceServerProperties +
                ", dseClient=" + dseClient +
                '}';
    }

    public ResourceServerProperties getResourceServerProperties() {
        return resourceServerProperties;
    }

    public void setResourceServerProperties(ResourceServerProperties resourceServerProperties) {
        this.resourceServerProperties = resourceServerProperties;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Map<String, ClientDetails> getDseClient() {
        return dseClient;
    }

    public void setDseClient(Map<String, ClientDetails> dseClient) {
        this.dseClient = dseClient;
    }
}
