package com.komandda.service.email.facade;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.email.sender.EmailSender;
import com.komandda.service.email.template.EmailTemplate;
import com.komandda.service.email.template.event.EventCreationEmailTemplate;
import com.komandda.service.email.template.event.EventDeletingEmailTemplate;
import com.komandda.service.email.template.event.EventUpdatingEmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yevhen on 30.12.16.
 */
@Service
public class EventEmailSenderFacade extends EmailSenderFacade{

    @Autowired
    private EmailSender emailSender;

    public void sendCreationEventEmail(Event event, User author) {
        EmailTemplate template = new EventCreationEmailTemplate(event, author);
        emailSender.send(provideEmailReceivers(event, author), template);
    }

    public void sendUpdatingEventEmail(Event event, User author, Event prevEvent) {
        EmailTemplate template = new EventUpdatingEmailTemplate(event, author, prevEvent);
        emailSender.send(provideEmailReceivers(event, author, prevEvent), template);
    }

    public void sendDeletingEventEmail(Event event, User author) {
        EmailTemplate template = new EventDeletingEmailTemplate(event, author);
        emailSender.send(provideEmailReceivers(event, author), template);
    }
}
