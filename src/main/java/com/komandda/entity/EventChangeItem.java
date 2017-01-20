package com.komandda.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

public class EventChangeItem {

    @Id
    private String id;

    @DBRef
    private User author;

    private Date date;

    private boolean isDeletedEvent;

    private String diffItemId;

    private Event diff;

    public EventChangeItem() {
    }

    public EventChangeItem(User author, Date date, Event diff) {
        this.author = author;
        this.date = date;
        this.diff = diff;
        this.diffItemId = diff.getId();
        isDeletedEvent = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Event getDiff() {
        return diff;
    }

    public void setDiff(Event diff) {
        this.diff = diff;
    }

    public String getDiffItemId() {
        return diffItemId;
    }

    public void setDiffItemId(String diffItemId) {
        this.diffItemId = diffItemId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isDeletedEvent() {
        return isDeletedEvent;
    }

    public void setDeletedEvent(boolean deletedEvent) {
        isDeletedEvent = deletedEvent;
    }

    @Override
    public String toString() {
        return "EventChangeItem{" +
                "id='" + id + '\'' +
                ", author=" + author +
                ", diff=" + diff +
                '}';
    }
}
