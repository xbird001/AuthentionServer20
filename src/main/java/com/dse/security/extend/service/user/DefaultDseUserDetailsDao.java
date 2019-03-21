package com.dse.security.extend.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DefaultDseUserDetailsDao implements DseUserDetailsDao {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    private static final String BASE_FIND_STATEMENT = "select t.user_name,t.password from t_sys_user t ";

    private static final String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + "where t.user_name = ? and t.status = 1 and t.del_flag = '0' ";

    private String selectUserDetailsSql = DEFAULT_SELECT_STATEMENT;

    public DefaultDseUserDetailsDao() {
    }

    public DefaultDseUserDetailsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DseUserDetails loadUserByUsername(String username) {
        DseUserDetails userDetails = null;
        try {
            userDetails = jdbcTemplate.queryForObject(selectUserDetailsSql, new UserDetailsRowMapper(), username);
        }
        catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }

        return userDetails;
    }

    private static class UserDetailsRowMapper implements RowMapper<DseUserDetails> {

        public DseUserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DseUserDetails(rs.getString(1),rs.getString(2),true,true,true,true, AuthorityUtils.commaSeparatedStringToAuthorityList("xx,xx"));
        }

    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
