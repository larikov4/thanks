package com.komandda.service.email.service;

import com.komandda.entity.Event;
import com.komandda.entity.User;

import java.util.*;

/**
 * Created by yevhen on 30.12.16.
 */
public abstract class EmailSenderService {

    public abstract void sendCreationEventEmail(Event event, User author);

    public abstract void sendUpdatingEventEmail(Event event, User author, Event prevEvent);

    public abstract void sendDeletingEventEmail(Event event, User author);

    /**
     * Add users, event author but removes author of change.
     */
    protected Collection<User> provideEmailReceivers(Event event, User author) {
        Collection<User> users = new ArrayList<>(event.getUsers());
        if(!users.contains(event.getAuthor())) {
            users.add(event.getAuthor());
        }
        users.remove(author);
        return users;
    }

    /**
     * Add users from both events, event author but removes author of change.
     */
    protected Collection<User> provideEmailReceivers(Event event, User author, Event prevEvent) {
        Collection<User> users = new HashSet<>(event.getUsers());
        users.addAll(prevEvent.getUsers());
        if(!users.contains(event.getAuthor())) {
            users.add(event.getAuthor());
        }
        users.remove(author);
        return users;
    }

    /**
     * Add users from both events, event author but removes author of change if he is alone.
     */
    protected Collection<User> provideEmailReceivers(Event event, Set<User> authors, Event prevEvent) {
        Collection<User> users = new HashSet<>(event.getUsers());
        users.addAll(prevEvent.getUsers());
        if(!users.contains(event.getAuthor())) {
            users.add(event.getAuthor());
        }
        if(authors.size() == 1) {
            users.remove(authors.iterator().next());
        } else {
            users.addAll(authors);
        }
        return users;
    }
}
