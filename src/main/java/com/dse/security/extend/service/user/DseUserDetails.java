package com.dse.security.extend.service.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class DseUserDetails extends User {

    private Map<String,Object> additional;

    public DseUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public DseUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DseUserDetails that = (DseUserDetails) o;
        return Objects.equals(additional, that.additional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), additional);
    }

    public Map<String, Object> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, Object> additional) {
        this.additional = additional;
    }

    @Override
    public String toString() {
        return super.toString() + "DseUserDetails{" +
                "additional=" + additional +
                '}';
    }
}
