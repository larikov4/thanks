package com.komandda.service.email.template;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.helper.DateHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yevhen on 03.11.16.
 */
public abstract class EmailTemplate {
    protected static final String SUBJECT_PREFIX = "[time] ";
    protected static final String LINE_SEPARATOR = "\n";
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");
    protected static final DateHelper DATE_HELPER = new DateHelper();

    private Event event;
    private User author;

    protected EmailTemplate(Event event, User author) {
        this.event = event;
        this.author = author;
    }

    public abstract String resolveSubject();

    public abstract String resolveBody();

    protected Event getEvent() {
        return event;
    }

    protected User getAuthor() {
        return author;
    }

    protected String format(Date date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    protected String formatTime(Date date) {
        return TIME_FORMATTER.format(date);
    }
}
