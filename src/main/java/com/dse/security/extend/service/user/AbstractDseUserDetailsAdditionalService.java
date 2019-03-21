package com.dse.security.extend.service.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDseUserDetailsAdditionalService implements DseUserDetailsAdditionalService {

    protected JdbcTemplate jdbcTemplate;

    /**
     * 查询用户信息
     */
    private static final String QUERY_USER_INFO = "SELECT T.ID, T.USER_NAME, T.NAME, T.TYPE " +
            "  FROM T_SYS_USER T " +
            " WHERE T.STATUS = '1' " +
            "   AND T.DEL_FLAG = '0' " +
            "   AND T.USER_NAME = ? ";
    /**
     * 查询部门、组织机构、行政分区
     */
    private static final String QUERY_DEPT_ORG_DIV = "SELECT T.DEPT_ID,T2.CODE AS DEPT_CODE,T2.ORGANIZATION_ID,T3.NAME AS ORG_NAME,T3.CODE AS ORG_CODE,T3.DIVISION_ID,T4.CODE AS DIV_CODE  " +
            "  FROM T_SYS_USER_DEP_R T  " +
            "  INNER JOIN T_SYS_DEPARTMENT T2 ON T2.ID = T.DEPT_ID AND T2.DEL_FLAG = '0'  " +
            "  INNER JOIN T_SYS_ORGANIZATION T3 ON T3.ID = T2.ORGANIZATION_ID AND T3.DEL_FLAG = '0'  " +
            "  INNER JOIN T_SYS_DIVISION T4 ON T4.ID = T3.DIVISION_ID AND T4.DEL_FLAG = '0'  " +
            " WHERE T.USER_ID = ?";

    /**
     * 查询用户所有角色
     */
    private static final String QUERY_ROLEID = "SELECT T.ROLE_ID  " +
            "  FROM T_SYS_USER_ROLE_R T  " +
            "  INNER JOIN T_SYS_ROLE T2 ON T.ROLE_ID = T2.ID AND T2.DEL_FLAG = '0'  " +
            " WHERE T.USER_ID = ?";
    /**
     * 查询用户所有资源
     */
    private static final String QUERY_RESOURCE = "SELECT DISTINCT T.RESOURCE_ID  " +
            "  FROM T_SYS_ROLE_RESOURCE_R T  " +
            "  INNER JOIN T_SYS_RESOURCE T2 ON T2.ID = T.RESOURCE_ID AND T2.DEL_FLAG = '0'  " +
            " WHERE T.ROLE_ID IN  " +
            "       (SELECT T.ROLE_ID  " +
            "          FROM T_SYS_USER_ROLE_R T  " +
            "         WHERE T.USER_ID = ?)";


    public AbstractDseUserDetailsAdditionalService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 额外扩展信息
     *
     * @param userInfo
     * @return
     */
    protected abstract Map<String, Object> doAdditional(Map<String,Object> userInfo);

    @Override
    public Map<String, Object> getAdditional(String username) {
        try {
            Map<String, Object> userDetails = new HashMap<>();

            //1、查询用户详细信息
            Map<String, Object> userInfo = jdbcTemplate.queryForMap(QUERY_USER_INFO, username);
            userDetails.putAll(userInfo);

            //2、查询用户所有角色
            List<String> roleInfo = jdbcTemplate.queryForList(QUERY_ROLEID, String.class, userInfo.get("ID").toString());
            StringBuilder roleIds = new StringBuilder();
            if (roleInfo != null && roleInfo.size() > 0) {
                for (String curRole : roleInfo) {
                    roleIds.append(curRole).append(",");
                }
                userDetails.put("ROLE_IDS", roleIds.deleteCharAt(roleIds.length() - 1).toString());
            } else {
                userDetails.put("ROLE_IDS", "");
            }

            //3、查询用户所有资源
            List<Map<String, Object>> resourceInfo = jdbcTemplate.queryForList(QUERY_RESOURCE, userInfo.get("ID").toString());
            StringBuilder resourceIds = new StringBuilder();
            if(resourceInfo != null && resourceInfo.size() > 0) {
            for (Map curResource : resourceInfo) {
                resourceIds.append(curResource.get("RESOURCE_ID").toString()).append(",");
            }
                userDetails.put("RESOURCE_IDS", resourceIds.deleteCharAt(resourceIds.length() - 1).toString());
            } else {
                userDetails.put("RESOURCE_IDS", "");
            }

            //4、查询部门、组织结构、行政区划
            StringBuilder deptIds = new StringBuilder();
            StringBuilder orgIds = new StringBuilder();
            StringBuilder orgCodes = new StringBuilder();
            StringBuilder orgNames = new StringBuilder();
            StringBuilder divCodes = new StringBuilder();

            //查询当前登录人员部门、组织结构、行政区划
            List<Map<String, Object>> deptOrgDivInfo = jdbcTemplate.queryForList(QUERY_DEPT_ORG_DIV, userInfo.get("ID").toString());
            if(deptOrgDivInfo != null && deptOrgDivInfo.size() > 0) {
                for (Map<String, Object> curM : deptOrgDivInfo) {
                    deptIds.append(curM.get("DEPT_ID").toString()).append(",");
                    orgIds.append(curM.get("ORGANIZATION_ID").toString()).append(",");
                    orgCodes.append(curM.get("ORG_CODE").toString()).append(",");
                    orgNames.append(curM.get("ORG_NAME").toString()).append(",");
                    divCodes.append(curM.get("DIV_CODE").toString()).append(",");
                }
            }
            userDetails.put("DEPT_IDS", deptIds.toString().length() > 0 ? deptIds.deleteCharAt(deptIds.length() - 1).toString() : "");
            userDetails.put("ORG_IDS", orgIds.toString().length() > 0 ? orgIds.deleteCharAt(orgIds.length() - 1).toString() : "");
            userDetails.put("ORG_CODES", orgCodes.toString().length() > 0 ? orgCodes.deleteCharAt(orgCodes.length() - 1).toString() : "");
            userDetails.put("ORG_NAMES", orgNames.toString().length() > 0 ? orgNames.deleteCharAt(orgNames.length() - 1).toString() : "");
            userDetails.put("DIV_CODES", divCodes.toString().length() > 0 ? divCodes.deleteCharAt(divCodes.length() - 1).toString() : "");

            return doAdditional(userDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    enum UserType {
        PROVINCE(1, "省"), CITY(2, "市"), AREA(3, "区、县"), WATERSHED(4, "流域"), HYDROLOGY(5, "水文局"), OTHER(9, "其他");
        protected int index;
        protected String description;

        UserType(int index, String description) {
            this.index = index;
            this.description = description;
        }
    }

}
