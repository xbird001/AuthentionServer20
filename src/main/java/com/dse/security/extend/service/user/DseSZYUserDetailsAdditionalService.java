package com.dse.security.extend.service.user;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 深圳水资源Session信息
 */
public class DseSZYUserDetailsAdditionalService extends AbstractDseUserDetailsAdditionalService {

    private Logger logger = LoggerFactory.getLogger(DseSZYUserDetailsAdditionalService.class);

    public DseSZYUserDetailsAdditionalService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }
    /**
     * 流域局、水文局用户能查询的行政区划
     */
    private static final String ORG_BUSINESS_DIVISION_CODE = "ZZJGYWZQ";
    /**
     * 流域局、水文局用户能查询的水资源分区
     */
    private static final String ORG_BUSINESS_WATERRESOURCESZONE_CODE = "ZZJGYWSZYFQ";
    /**
     * 查询流域局用户、水文局用户下辖水资源分区
     */
    private static final String QUERY_WRZ_SPECIAL = "SELECT T3.VALUE AS WRZ_CODE " +
                                                        "  FROM T_SYS_DICTIONARY T " +
                                                        " INNER JOIN T_SYS_DICTIONARY T2 " +
                                                        "    ON T2.PID = T.ID AND T2.VALUE = ? AND T2.DEL_FLAG = '0' " +
                                                        " INNER JOIN T_SYS_DICTIONARY T3 ON T3.PID = T2.ID AND T3.DEL_FLAG = '0' " +
                                                        " WHERE T.CODE = 'ZZJGYWSZYFQ'";
    /**
     * 查询流域局用户、水文局用户下辖行政区划(当前所处的行政区划不包括在内)
     */
    private static final String QUERY_DIV_SPECIAL = "SELECT T3.VALUE AS DIV_ID,T4.CODE AS DIV_CODE  " +
                                                        "  FROM T_SYS_DICTIONARY T  " +
                                                        " INNER JOIN T_SYS_DICTIONARY T2  " +
                                                        "    ON T2.PID = T.ID AND T2.VALUE = ? AND T2.DEL_FLAG = '0'  " +
                                                        " INNER JOIN T_SYS_DICTIONARY T3 ON T3.PID = T2.ID AND T3.DEL_FLAG = '0'  " +
                                                        " INNER JOIN T_SYS_DIVISION T4 ON T4.ID = T3.VALUE  " +
                                                        " WHERE T.CODE = 'ZZJGYWZQ'";
    /**
     * 查询下辖的所有行政分区、水资源分区
     */
    private static final String QUERY_SUB_DIV_WRZ = "SELECT T.AD_CD,T.WRZ_CD FROM WR_WRCS_M T WHERE T.AD_CD LIKE ?";

    @Override
    protected Map<String, Object> doAdditional(Map<String, Object> userDetails) {

        StringBuilder orgCodes = new StringBuilder(userDetails.get("ORG_CODES").toString());
        StringBuilder divCodes = new StringBuilder(userDetails.get("DIV_CODES").toString());
        StringBuilder wrzIds = new StringBuilder();

        if (UserType.WATERSHED.index == Integer.valueOf(userDetails.get("TYPE") != null ? userDetails.get("TYPE").toString() : String.valueOf(UserType.OTHER.index)) ||
                (UserType.HYDROLOGY.index == Integer.valueOf(userDetails.get("TYPE") != null ? userDetails.get("TYPE").toString() : String.valueOf(UserType.OTHER.index)))) {
            //流域局、水文局用户

            List<String> orgCodes4L = Arrays.asList(orgCodes.toString());
            divCodes.delete(0,divCodes.length()-1);
            for (String curOrgCode : orgCodes4L) {
                if (StringUtils.isNotEmpty(curOrgCode)) {
                    //行政区划
                    List<Map<String, Object>> divCodes4D = jdbcTemplate.queryForList(QUERY_DIV_SPECIAL, curOrgCode);

                    for (Map<String, Object> curM : divCodes4D) {
                        divCodes.append(curM.get("DIV_CODE").toString()).append(",");
                    }
                    //水资源分区
                    List<Map<String, Object>> wrzCodes4D = jdbcTemplate.queryForList(QUERY_WRZ_SPECIAL, curOrgCode);
                    for (Map<String, Object> curM : wrzCodes4D) {
                        wrzIds.append(curM.get("WRZ_CODE").toString()).append(",");
                    }
                }
            }
        } else {
            //非 流域局、水文局用户

            List<String> devCodeA = Arrays.asList(divCodes.toString());
            divCodes.append(",");
            for (String curDevCode : devCodeA) {
                if (StringUtils.isNotEmpty(curDevCode)) {
                    //**非** 流域局、水文局用户
                    List<Map<String, Object>> subDivCodes = jdbcTemplate.queryForList(QUERY_SUB_DIV_WRZ, curDevCode.replace("0", "") + "%");
                    for (Map curSubM : subDivCodes) {
                        divCodes.append(curSubM.get("AD_CD").toString()).append(",");
                        wrzIds.append(curSubM.get("WRZ_CD").toString()).append(",");
                    }
                }

            }
        }
        userDetails.put("DIV_CODES",divCodes.length() > 0 ? divCodes.deleteCharAt(divCodes.length()-1).toString() : "");
        userDetails.put("WRZ_IDS",wrzIds.length() > 0 ? wrzIds.deleteCharAt(wrzIds.length()-1).toString() : "");
        return userDetails;
    }
}
