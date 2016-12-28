package com.komandda.entity;

import com.google.common.base.Objects;
import org.springframework.data.annotation.Id;

import java.util.Date;

public class Series {
    @Id
    private String id;
    private Event event;
    private Date end;

    public Series(Event event, Date end) {
        this.event = event;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Series series = (Series) o;
        return Objects.equal(id, series.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
