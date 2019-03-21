package com.dse.security.config.properties;

/**
 * 构建的Session类型
 */
public class SessionType {

    /**
     * token对应的value信息（可理解为session信息）
     * 提供两种类型：
     *  0：默认的session信息 用户名、用户id、用户类型、资源id、角色id、部门id、组织结构id、行政区划id(只包含当前节点，不包含子节点)
     *  1：深圳水资源特用的session信息 用户名、用户id、用户类型、资源id、角色id、部门id、组织结构id、行政区划id(角色对应的能看能看到的所有行政区划)、水资源分区(角色对应的能看能看到的所有行政区划)
     */
    private String type = "0";

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
