package com.dse.security.config.properties;

/**
 * 登录信息的保存方式
 *  LOCAL_MEMORY：本机内存保存：适用于开发环境
 *  DB：数据库：适用于生产环境
 *
 */
public class UserRegistryType {

    /**
     * 系统默认提供两种方式配置：
     *  LOCAL_MEMORY：内存模式：用户信息通过配置文件读取到本机内存，适用于开发环境
     *  DB：数据库模式：用户信息保存在数据库中，系统默认实现，适用于生产环境
     *  该属性是通过配置文件来赋值，用户可自行扩展具体需求，配合具体的实现方式，具体参见接口：LoadUserByUserNameService
     */
    private String type = "LOCAL_MEMORY";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
