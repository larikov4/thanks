package com.komandda.service.email.template.event;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.email.template.EmailTemplate;


public class EventDeletingEmailTemplate extends EmailTemplate {
    private User author;

    public EventDeletingEmailTemplate(Event event, User author) {
        super(event);
        this.author = author;
    }

    @Override
    public String resolveSubject() {
        return SUBJECT_PREFIX + getEvent().getTitle() + " (deleted by " + getAuthor().getName() + ")";
    }

    @Override
    public String resolveBody() {
        return "Event with title \"" + getEvent().getTitle() + "\" was deleted by " + getAuthor().getName() + ".";
    }

    public User getAuthor() {
        return author;
    }
}
