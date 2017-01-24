package com.komandda.service.email.template.event;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.email.template.EmailTemplate;

import java.time.LocalDateTime;

import static org.springframework.util.StringUtils.isEmpty;


public class EventCreationEmailTemplate extends EmailTemplate {
    private User author;

    public EventCreationEmailTemplate(Event event, User author) {
        super(event);
        this.author = author;
    }

    @Override
    public String resolveSubject() {
        return SUBJECT_PREFIX + getEvent().getTitle() + " (created by " + getAuthor().getName() + ")";
    }

    @Override
    public String resolveBody() {
        StringBuilder emailBodyBuilder = new StringBuilder();
        appendIntroduction(emailBodyBuilder);
        appendTitle(emailBodyBuilder);
        appendDescription(emailBodyBuilder);
        appendProject(emailBodyBuilder);
        appendDuration(emailBodyBuilder);
        appendAuthor(emailBodyBuilder);
        appendLocation(emailBodyBuilder);
        appendUsers(emailBodyBuilder);
        appendEquipment(emailBodyBuilder);
        return emailBodyBuilder.toString();
    }

    public User getAuthor() {
        return author;
    }

    protected StringBuilder appendIntroduction(StringBuilder emailBodyBuilder) {
        return emailBodyBuilder
                .append("You were assigned for a new event.").append(LINE_SEPARATOR);
    }

    private StringBuilder appendTitle(StringBuilder emailBodyBuilder) {
        return emailBodyBuilder.append("Title: ").append(getEvent().getTitle()).append(LINE_SEPARATOR);
    }

    private void appendProject(StringBuilder emailBodyBuilder) {
        if(getEvent().getProject() != null ) {
            emailBodyBuilder.append("Project: ").append(getEvent().getProject().getName()).append(LINE_SEPARATOR);
        }
    }

    private void appendDescription(StringBuilder emailBodyBuilder) {
        if(!isEmpty(getEvent().getDescription())) {
            emailBodyBuilder.append("Description: ").append(getEvent().getDescription()).append(LINE_SEPARATOR);
        }
    }

    protected void appendDuration(StringBuilder emailBodyBuilder) {
        emailBodyBuilder
                .append("Duration: from ").append(format(getEvent().getStart()))
                .append(" to ").append(format(getEvent().getEnd())).append(LINE_SEPARATOR);
    }

    private StringBuilder appendAuthor(StringBuilder emailBodyBuilder) {
        return emailBodyBuilder.append("Author: ").append(getEvent().getAuthor().getName()).append(LINE_SEPARATOR);
    }

    private void appendLocation(StringBuilder emailBodyBuilder) {
        if(getEvent().getLocation()!=null){
            emailBodyBuilder.append("Location: ").append(getEvent().getLocation().getName()).append(LINE_SEPARATOR);
        }
    }

    private void appendUsers(StringBuilder emailBodyBuilder) {
        if(getEvent().getUsers().size() == 1) {
            emailBodyBuilder.append("User: ").append(getEvent().getUsers().get(0).getName()).append(LINE_SEPARATOR);
        } else {
            emailBodyBuilder.append("Users: ").append(LINE_SEPARATOR);
            for(User user : getEvent().getUsers()) {
                emailBodyBuilder.append("\t").append(user.getName()).append(LINE_SEPARATOR);
            }
        }
    }

    private void appendEquipment(StringBuilder emailBodyBuilder) {
        if(!getEvent().getEquipment().isEmpty()) {
            emailBodyBuilder.append("Equipment: ").append(LINE_SEPARATOR);
        }
        for(Equipment equipment : getEvent().getEquipment()) {
            emailBodyBuilder.append("\t").append(equipment.getName()).append(LINE_SEPARATOR);
        }
    }

}
