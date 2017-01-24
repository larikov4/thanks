package com.komandda.service.email.service;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.email.pool.UpdatingEmailsPool;
import com.komandda.service.email.sender.EmailSender;
import com.komandda.service.email.template.*;
import com.komandda.service.email.template.event.series.SeriesEventCreationEmailTemplate;
import com.komandda.service.email.template.event.series.SeriesEventDeletingEmailTemplate;
import com.komandda.service.email.template.event.series.SeriesEventUpdatingEmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Created by yevhen on 30.12.16.
 */
@Service
public class SeriesEventEmailSenderService extends EmailSenderService {
    public static final int DELAY = 60 * 1000;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private UpdatingEmailsPool updatingEmailsPool;

    public void sendCreationEventEmail(Event event, User author) {
        EmailTemplate template = new SeriesEventCreationEmailTemplate(event, author);
        emailSender.send(provideEmailReceivers(event, author), template);
    }

    public void sendUpdatingEventEmail(Event event, User author, Event prevEvent) {
        updatingEmailsPool.add(event, author, prevEvent);
    }

    public void sendDeletingEventEmail(Event event, User author) {
        EmailTemplate template = new SeriesEventDeletingEmailTemplate(event, author);
        emailSender.send(provideEmailReceivers(event, author), template);
    }

    @Scheduled(fixedDelay = DELAY)
    public void sendExpiredElementsFromPool(){
        Collection<UpdatingEmailsPool.Element> expired = updatingEmailsPool.getAndRemoveExpired();
        expired.forEach(element -> {
            EmailTemplate template = new SeriesEventUpdatingEmailTemplate(element.getEvent(),
                    element.getAuthors(), element.getPrevEvent());
            emailSender.send(provideEmailReceivers(element.getEvent(),
                    element.getAuthors(), element.getPrevEvent()), template);
        });
    }
}
