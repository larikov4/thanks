package com.komandda.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.komandda.entity.json.DateWithoutTimeZoneJsonDeserializer;
import com.komandda.entity.json.DateWithoutTimeZoneJsonSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
import java.util.List;

/**
 * @author Yevhen_Larikov
 */
@JsonAutoDetect
public class Event {

    @Id
    private String id;

    private String title;

    private Date start;

    private Date end;

    @DBRef
    private User author;

    @DBRef
    private List<Equipment> equipment;

    @DBRef
    private List<User> users;

    @DBRef
    private Location location;

    private Date created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm", timezone = "Europe/Kiev")
    @JsonDeserialize(using = DateWithoutTimeZoneJsonDeserializer.class)
    @JsonSerialize(using = DateWithoutTimeZoneJsonSerializer.class)
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

//    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm", timezone = "Europe/Kiev")
    @JsonDeserialize(using = DateWithoutTimeZoneJsonDeserializer.class)
    @JsonSerialize(using = DateWithoutTimeZoneJsonSerializer.class)
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", author=" + author +
                ", equipment=" + equipment +
                ", users=" + users +
                ", location=" + location +
                '}';
    }
}
