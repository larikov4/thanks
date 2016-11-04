package com.komandda.service.email.template;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.User;

import static org.springframework.util.StringUtils.isEmpty;


public class EventCreationEmailTemplate extends EmailTemplate {

    public EventCreationEmailTemplate(Event event, User author) {
        super(event, author);
    }

    @Override
    public String resolveSubject() {
        return SUBJECT_PREFIX + getEvent().getTitle() + " (created by " + getAuthor().getName() + ")";
    }

    @Override
    public String resolveBody() {
        StringBuilder emailBodyBuilder = new StringBuilder();
        emailBodyBuilder
                .append("You were assigned for a new event.").append(LINE_SEPARATOR)
                .append("Title: ").append(getEvent().getTitle()).append(LINE_SEPARATOR);
        if(!isEmpty(getEvent().getDescription())) {
            emailBodyBuilder.append("Description: ").append(getEvent().getDescription()).append(LINE_SEPARATOR);
        }
        emailBodyBuilder
                .append("Duration: from ").append(format(getEvent().getStart()))
                .append(" to ").append(format(getEvent().getEnd())).append(LINE_SEPARATOR)
                .append("Author: ").append(getEvent().getAuthor().getName()).append(LINE_SEPARATOR);
        if(getEvent().getLocation()!=null){
            emailBodyBuilder.append("Location: ").append(getEvent().getLocation().getName()).append(LINE_SEPARATOR);
        }
        if(getEvent().getUsers().size() == 1) {
            emailBodyBuilder.append("User: ").append(getEvent().getUsers().get(0).getName()).append(LINE_SEPARATOR);
        } else {
            emailBodyBuilder.append("Users: ").append(LINE_SEPARATOR);
            for(User user : getEvent().getUsers()) {
                emailBodyBuilder.append("\t").append(user.getName()).append(LINE_SEPARATOR);
            }
        }

        if(!getEvent().getEquipment().isEmpty()) {
            emailBodyBuilder.append("Equipment: ").append(LINE_SEPARATOR);
        }
        for(Equipment equipment : getEvent().getEquipment()) {
            emailBodyBuilder.append("\t").append(equipment.getName()).append(LINE_SEPARATOR);
        }

        return emailBodyBuilder.toString();
    }
}
