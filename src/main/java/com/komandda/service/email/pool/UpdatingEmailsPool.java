package com.komandda.service.email.pool;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Yevhen on 24.01.2017.
 */
@Service
@Scope("prototype")
public class UpdatingEmailsPool {
    public static final int EXPIRATION_PERIOD_IN_MINUTES = 5;

    private Map<String, Element> pool = new HashMap<>();

    public void add(Event event, User author, Event prevEvent) {
        if(pool.containsKey(event.getId())) {
            Element element = pool.get(event.getId());
            Set<User> authors = element.getAuthors();
            authors.add(author);
            pool.put(event.getId(), new Element(event, authors, element.getPrevEvent()));
        } else {
            Set<User> users = new HashSet<>();
            users.add(author);
            pool.put(event.getId(), new Element(event, users, prevEvent));
        }
    }

    public Collection<Element> getAndRemoveExpired() {
        Map<String, Element> expired = new HashMap<>();
        for(Map.Entry<String, Element> entry : pool.entrySet()) {
            Element element = entry.getValue();
            if(isExpired(element)) {
                expired.put(entry.getKey(), element);
            }
        }
        expired.keySet().forEach(key -> pool.remove(key));
        return expired.values();
    }

    private boolean isExpired(Element element) {
        return LocalDateTime.now().minusMinutes(EXPIRATION_PERIOD_IN_MINUTES).isAfter(element.getDate());
    }

    public static class Element {
        private LocalDateTime date;
        private Set<User> authors;
        private Event prevEvent;
        private Event event;

        public Element(Event event, Set<User> authors, Event prevEvent) {
            this.event = event;
            this.authors = authors;
            this.prevEvent = prevEvent;
            date = LocalDateTime.now();
        }

        public LocalDateTime getDate() {
            return date;
        }

        public Set<User> getAuthors() {
            return authors;
        }

        public Event getPrevEvent() {
            return prevEvent;
        }

        public Event getEvent() {
            return event;
        }
    }
}
