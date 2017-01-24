package com.komandda.service.email.service;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.email.pool.UpdatingEmailsPool;
import com.komandda.service.email.sender.EmailSender;
import com.komandda.service.email.template.EmailTemplate;
import com.komandda.service.email.template.event.EventCreationEmailTemplate;
import com.komandda.service.email.template.event.EventDeletingEmailTemplate;
import com.komandda.service.email.template.event.EventUpdatingEmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Created by yevhen on 30.12.16.
 */
@Service
public class EventEmailSenderService extends EmailSenderService {
    public static final int DELAY = 60 * 1000;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private UpdatingEmailsPool updatingEmailsPool;

    public void sendCreationEventEmail(Event event, User author) {
        EmailTemplate template = new EventCreationEmailTemplate(event, author);
        emailSender.send(provideEmailReceivers(event, author), template);
    }

    public void sendUpdatingEventEmail(Event event, User author, Event prevEvent) {
        updatingEmailsPool.add(event, author, prevEvent);
    }

    public void sendDeletingEventEmail(Event event, User author) {
        EmailTemplate template = new EventDeletingEmailTemplate(event, author);
        emailSender.send(provideEmailReceivers(event, author), template);
    }

    @Scheduled(fixedDelay = DELAY)
    public void sendExpiredElementsFromPool(){
        Collection<UpdatingEmailsPool.Element> expired = updatingEmailsPool.getAndRemoveExpired();
        expired.forEach(element -> {
            EmailTemplate template = new EventUpdatingEmailTemplate(element.getEvent(),
                    element.getAuthors(), element.getPrevEvent());
            emailSender.send(provideEmailReceivers(element.getEvent(),
                    element.getAuthors(), element.getPrevEvent()), template);
        });
    }
}
