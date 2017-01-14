package com.komandda.entity;

import com.google.common.base.Objects;
import org.springframework.data.annotation.Id;

/**
 * @author Yevhen_Larikov
 */
public class Project {

    @Id
    private String id;

    private String name;

    private String color;

    private boolean deleted;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project location = (Project) o;
        return Objects.equal(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
