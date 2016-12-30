package com.komandda.service.email.facade;

import com.komandda.entity.Event;
import com.komandda.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by yevhen on 30.12.16.
 */
public abstract class EmailSenderFacade {

    public abstract void sendCreationEventEmail(Event event, User author);

    public abstract void sendUpdatingEventEmail(Event event, User author, Event prevEvent);

    public abstract void sendDeletingEventEmail(Event event, User author);

    protected Collection<User> provideEmailReceivers(Event event, User author) {
        Collection<User> users = new ArrayList<>(event.getUsers());
        if(!users.contains(event.getAuthor())) {
            users.add(event.getAuthor());
        }
        users.remove(author);
        return users;
    }

    protected Collection<User> provideEmailReceivers(Event event, User author, Event prevEvent) {
        Collection<User> users = new HashSet<>(event.getUsers());
        users.addAll(prevEvent.getUsers());
        if(!users.contains(event.getAuthor())) {
            users.add(event.getAuthor());
        }
        users.remove(author);
        return users;
    }
}
