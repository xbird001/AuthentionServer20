package com.dse.security.config;

import com.dse.security.extend.service.user.DseUserDetails;
import org.apache.commons.collections.MapUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Map;

public class DseAccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map<String, ?> rs4P = super.convertAccessToken(token, authentication);
        Map<String, Object> rs = MapUtils.multiValueMap(rs4P);
        Class clazz = authentication.getPrincipal().getClass();
        if (DseUserDetails.class.isAssignableFrom(clazz)) {
            DseUserDetails dseUserDetails = (DseUserDetails) authentication.getPrincipal();
            rs.put("additional", dseUserDetails.getAdditional());
        }
        return rs;
    }

}
