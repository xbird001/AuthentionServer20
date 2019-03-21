package com.dse.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.dse.as")
public class ResourceServerProperties {

    /**
     * 构建的session信息类型
     */
    private SessionType sessionType = new SessionType();
    /**
     * token保存策略
     */
     private TokenStoreType tokenStoreType = TokenStoreType.LOCAL_MEMORY;

     /**
     * 用户登录信息保存策略
     */
    private UserRegistryType userRegistryType = new UserRegistryType();

    /**
     * 第三方APP客户端账号信息
     */
    private ClientRetistryType clientRetistryType = ClientRetistryType.LOCAL_MEMORY;

    /**
     * 用户登录信息保存策略是本机内存模式时用于保存用户登录信息
     */
    private DseUserInMemeroy usersInMemory = new DseUserInMemeroy();

    /**
     * 用于保存第三方APP客户端的账号信息
     */
    private DseClientInMemeroy clientInMemeroy = new DseClientInMemeroy();

    public UserRegistryType getUserRegistryType() {
        return userRegistryType;
    }

    public void setUserRegistryType(UserRegistryType userRegistryType) {
        this.userRegistryType = userRegistryType;
    }

    public DseUserInMemeroy getUsersInMemory() {
        return usersInMemory;
    }

    public void setUsersInMemory(DseUserInMemeroy usersInMemory) {
        this.usersInMemory = usersInMemory;
    }

    public TokenStoreType getTokenStoreType() {
        return tokenStoreType;
    }

    public void setTokenStoreType(TokenStoreType tokenStoreType) {
        this.tokenStoreType = tokenStoreType;
    }

    public DseClientInMemeroy getClientInMemeroy() {
        return clientInMemeroy;
    }

    public void setClientInMemeroy(DseClientInMemeroy clientInMemeroy) {
        this.clientInMemeroy = clientInMemeroy;
    }

    public ClientRetistryType getClientRetistryType() {
        return clientRetistryType;
    }

    public void setClientRetistryType(ClientRetistryType clientRetistryType) {
        this.clientRetistryType = clientRetistryType;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }
}
