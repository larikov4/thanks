package com.komandda.entity;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

/**
 * Created by Yevhn on 13.03.2016.
 */
public class Permission implements GrantedAuthority {

    private String id;

    private String name;

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
}
