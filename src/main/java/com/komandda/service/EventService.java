package com.komandda.service;

import com.komandda.entity.Equipment;
import com.komandda.entity.EventChangeItem;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.repository.EventRepository;
import com.komandda.service.email.sender.EmailSender;
import com.komandda.service.email.template.EmailTemplate;
import com.komandda.service.email.template.EventCreationEmailTemplate;
import com.komandda.service.email.template.EventDeletingEmailTemplate;
import com.komandda.service.email.template.EventUpdatingEmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by yevhen on 28.06.16.
 */
@Service
public class EventService {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Autowired
    private EventRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventChangelogService changelog;

    @Autowired
    private EmailSender emailSender;


    public List<Event> findAll() {
        return hidePassword(repository.findAll());
    }

    public Event findOne(String id) {
        return hidePassword(repository.findOne(id));
    }

    public Event insert(Event event, User author) {
        setEmptyEquipmentListIfAbsent(event);
        sendCreationEventEmail(event, author);

        event.setCreated(new Date());
        Event eventWithId = repository.insert(event);

        hidePassword(eventWithId);
        userService.hidePassword(author);

        changelog.insert(new EventChangeItem(author, new Date(), eventWithId));
        return eventWithId;
    }

    public Event save(Event event, User author) {
        Event prevEvent = repository.findOne(event.getId());
        sendUpdatingEventEmail(event,author, prevEvent);
        setEmptyEquipmentListIfAbsent(event);
        Event savedEvent = repository.save(event);

        hidePassword(savedEvent);
        userService.hidePassword(author);

        changelog.insert(new EventChangeItem(author, new Date(), event));
        return savedEvent;
    }

    public Event delete(Event event, User author) {
        repository.delete(event);
        sendDeletingEventEmail(event, author);
        return hidePassword(event);
    }

    public List<Event> findBy(final String locationId,final List<String> names,final List<String> equipmentIds) {
        List<String> safeNames = Optional.ofNullable(names).orElse(Collections.emptyList());
        List<String> safeEquipmentIds = Optional.ofNullable(equipmentIds).orElse(Collections.emptyList());
        List<Event> events = findAll();
        Stream<Event> locationFilterStream = events.stream()
                .filter(event -> Objects.nonNull(event.getLocation()))
                .filter(event -> event.getLocation().getId().equals(locationId));
        Stream<Event> userFilterStream = events.stream()
                .filter(event -> event.getUsers().stream()
                        .map(User::getName)
                        .anyMatch(safeNames::contains));
        Stream<Event> equipmentFilterStream = events.stream()
                .filter(event -> event.getEquipment()
                        .stream().map(Equipment::getId)
                        .anyMatch(safeEquipmentIds::contains));
        return Stream.concat(Stream.concat(locationFilterStream, userFilterStream), equipmentFilterStream)
                .distinct()
                .collect(toList());
    }

    private void setEmptyEquipmentListIfAbsent(Event event) {
        if(event.getEquipment() == null) {
            event.setEquipment(Collections.emptyList());
        }
    }

    private List<Event> hidePassword(List<Event> events) {
        for(Event event : events) {
            hidePassword(event);
        }
        return events;
    }

    private Event hidePassword(Event event){
        userService.hidePassword(event.getUsers());
        userService.hidePassword(event.getAuthor());
        return event;
    }

    private void sendCreationEventEmail(Event event, User author) {
        EmailTemplate template = new EventCreationEmailTemplate(event, author);
        emailSender.send(provideEmailReceivers(event, author), template);
    }

    private void sendUpdatingEventEmail(Event event, User author, Event prevEvent) {
        EmailTemplate template = new EventUpdatingEmailTemplate(event, author, prevEvent);
        emailSender.send(provideEmailReceivers(event, author, prevEvent), template);
    }

    private void sendDeletingEventEmail(Event event, User author) {
        EmailTemplate template = new EventDeletingEmailTemplate(event, event.getAuthor());
        emailSender.send(provideEmailReceivers(event, author), template);
    }

    private Collection<User> provideEmailReceivers(Event event, User author) {
        Collection<User> users = new ArrayList<>(event.getUsers());
        if(!users.contains(event.getAuthor())) {
            users.add(event.getAuthor());
        }
        users.remove(author);
        return users;
    }

    private Collection<User> provideEmailReceivers(Event event, User author, Event prevEvent) {
        Collection<User> users = new HashSet<>(event.getUsers());
        users.addAll(prevEvent.getUsers());
        if(!users.contains(event.getAuthor())) {
            users.add(event.getAuthor());
        }
        users.remove(author);
        return users;
    }
}
