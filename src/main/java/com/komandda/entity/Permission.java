package com.komandda.entity;

import com.google.common.base.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Yevhn on 13.03.2016.
 */
public class Permission implements GrantedAuthority {

    private String id;

    private String name;

    public Permission() {

    }

    public Permission(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equal(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
