package com.komandda.service.email.template.event.series;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.email.template.event.EventDeletingEmailTemplate;


public class SeriesEventDeletingEmailTemplate extends EventDeletingEmailTemplate {

    public SeriesEventDeletingEmailTemplate(Event event, User author) {
        super(event, author);
    }


    @Override
    public String resolveBody() {
        return "Series event with title \"" + getEvent().getTitle() + "\" was deleted by " + getAuthor().getName() + ".";
    }
}
