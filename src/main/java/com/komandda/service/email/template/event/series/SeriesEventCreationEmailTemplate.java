package com.komandda.service.email.template.event.series;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.email.template.event.EventCreationEmailTemplate;

import java.time.LocalDateTime;


public class SeriesEventCreationEmailTemplate extends EventCreationEmailTemplate {

    public SeriesEventCreationEmailTemplate(Event event, User author) {
        super(event, author);
    }

    @Override
    protected StringBuilder appendIntroduction(StringBuilder emailBodyBuilder) {
        return emailBodyBuilder
                .append("You were assigned for a new series event.").append(LINE_SEPARATOR);
    }

    @Override
    protected void appendDuration(StringBuilder emailBodyBuilder) {
        LocalDateTime localDateTimeStart = DATE_HELPER.asLocalDateTime(getEvent().getStart());
        emailBodyBuilder
                .append("It occurs every ").append(localDateTimeStart.getDayOfWeek().name().toLowerCase())
                    .append('.').append(LINE_SEPARATOR)
                .append("From ").append(formatTime(getEvent().getStart()))
                .append(" to ").append(formatTime(getEvent().getEnd())).append(LINE_SEPARATOR);
    }
}
