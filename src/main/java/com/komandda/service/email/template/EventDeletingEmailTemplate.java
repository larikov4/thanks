package com.komandda.service.email.template;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.User;


public class EventDeletingEmailTemplate extends EmailTemplate {

    public EventDeletingEmailTemplate(Event event, User author) {
        super(event, author);
    }

    @Override
    public String resolveSubject() {
        return SUBJECT_PREFIX + getEvent().getTitle() + " (deleted by " + getAuthor().getName() + ")";
    }

    @Override
    public String resolveBody() {
        return "Event with title \"" + getEvent().getTitle() + "\" was deleted by " + getAuthor().getName() + ".";
    }
}
