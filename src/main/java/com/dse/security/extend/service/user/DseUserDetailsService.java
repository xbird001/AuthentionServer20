package com.dse.security.extend.service.user;

import com.dse.security.config.properties.ResourceServerProperties;

import java.util.List;


/**
 * 默认的用户信息存储方式，默认支持两种方式：LOCAL_MEMORY、DB可通过配置文件动态调整
 *
 */
public class DseUserDetailsService extends AbstractDseUserDetailsService {

    private ResourceServerProperties resourceServerProperties;

    private DseUserStore dseUserStore;

    private List<LoadUserByUserNameService> loadUserByUserNameServiceList;

    public DseUserDetailsService() {

    }

    public DseUserDetailsService(ResourceServerProperties resourceServerProperties, DseUserStore dseUserStore, DseUserDetailsAdditionalService dseUserDetailsAdditionalService) {
        super(dseUserDetailsAdditionalService);
        this.resourceServerProperties = resourceServerProperties;
        this.dseUserStore = dseUserStore;
    }

    @Override
    protected DseUserDetails doLoadUserByUsername(String username) {
        for(LoadUserByUserNameService curLoad : loadUserByUserNameServiceList) {
            if(!curLoad.isSupport(resourceServerProperties.getUserRegistryType())) {
                continue;
            }
            return curLoad.doLoadUserByUsername(username);
        }
        return null;
    }

    public ResourceServerProperties getResourceServerProperties() {
        return resourceServerProperties;
    }

    public void setResourceServerProperties(ResourceServerProperties resourceServerProperties) {
        this.resourceServerProperties = resourceServerProperties;
    }

    public DseUserStore getDseUserStore() {
        return dseUserStore;
    }

    public void setDseUserStore(DseUserStore dseUserStore) {
        this.dseUserStore = dseUserStore;
    }

    public List<LoadUserByUserNameService> getLoadUserByUserNameServiceList() {
        return loadUserByUserNameServiceList;
    }

    public void setLoadUserByUserNameServiceList(List<LoadUserByUserNameService> loadUserByUserNameServiceList) {
        this.loadUserByUserNameServiceList = loadUserByUserNameServiceList;
    }
}
