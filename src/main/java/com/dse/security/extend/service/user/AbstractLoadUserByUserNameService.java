package com.dse.security.extend.service.user;

import com.dse.security.config.properties.UserRegistryType;

public abstract class AbstractLoadUserByUserNameService implements LoadUserByUserNameService{

    private String type;

    public AbstractLoadUserByUserNameService(String type) {
        this.type = type;
    }

    @Override
    public boolean isSupport(UserRegistryType userRegistryType) {
        return type.equals(userRegistryType.getType());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
