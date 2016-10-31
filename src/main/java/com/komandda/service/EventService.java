package com.komandda.service;

import com.komandda.entity.Equipment;
import com.komandda.entity.EventChangeItem;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.repository.EventRepository;
import com.komandda.service.mail.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private MailHelper mailHelper;


    public List<Event> findAll() {
        return hidePassword(repository.findAll());
    }

    public Event findOne(String id) {
        return hidePassword(repository.findOne(id));
    }

    public Event insert(Event event, User author) {
        event.setCreated(new Date());
        setEmptyEquipmentListIfAbsent(event);
        Event eventWithId = repository.insert(event);

        hidePassword(eventWithId);
        userService.hidePassword(author);

        changelog.insert(new EventChangeItem(author, new Date(), eventWithId));
        return eventWithId;
    }

    public Event save(Event event, User author) {
        Event prevEvent = repository.findOne(event.getId());
        sendUpdatingEventEmail(event, prevEvent, author);
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
            event.setEquipment(Collections.<Equipment>emptyList());
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

    private void sendCreationEventEmail(Event event) {
        StringBuilder eventBuilder = new StringBuilder();
        eventBuilder.append("Title ").append(event.getTitle()).append(System.lineSeparator())
                .append("Description ").append(event.getDescription()).append(System.lineSeparator())
                .append("Date ").append(format(event.getStart())).append(" to ").append(format(event.getEnd())).append(System.lineSeparator())
                .append("Author ").append(event.getAuthor().getName()).append(System.lineSeparator())
                .append("Location ").append(event.getLocation().getName()).append(System.lineSeparator());
        eventBuilder.append("Users: ").append(System.lineSeparator());
        for(User user : event.getUsers()) {
            eventBuilder.append("\t").append(user.getName()).append(System.lineSeparator());
        }

        eventBuilder.append("Equipment: ").append(System.lineSeparator());
        for(Equipment equipment : event.getEquipment()) {
            eventBuilder.append("\t").append(equipment.getName()).append(System.lineSeparator());
        }

        String createdEvent = eventBuilder.toString();

        for(User user : event.getUsers()) {
            String email = user.getEmail();
            if(!StringUtils.isEmpty(email)) {
                String emailBody = new StringBuilder()
                        .append("Hello ").append(user.getName()).append(System.lineSeparator())
                        .append("New event was created.").append(System.lineSeparator())
                        .append(createdEvent).toString();
                mailHelper.sendMail(email, "New event created", emailBody);
            }
        }
    }


    private String generateDiff(Event currentEvent, Event prevEvent) {
        StringBuilder diff = new StringBuilder();
        if (!prevEvent.getTitle().equals(currentEvent.getTitle())) {
            diff.append("Title changed").append(System.lineSeparator())
                .append("\tfrom ")
                .append(prevEvent.getTitle()).append(System.lineSeparator())
                .append("\tto ")
                .append(currentEvent.getTitle()).append(System.lineSeparator());
        }
        if (!prevEvent.getDescription().equals(currentEvent.getDescription())) {
            diff.append("Description changed").append(System.lineSeparator())
                    .append("\tfrom ")
                    .append(prevEvent.getDescription()).append(System.lineSeparator())
                    .append("\tto ")
                    .append(currentEvent.getDescription()).append(System.lineSeparator());
        }
        if (!prevEvent.getStart().equals(currentEvent.getStart())) {
            diff.append("Event start changed").append(System.lineSeparator())
                .append("\tfrom ")
                .append(format(prevEvent.getStart())).append(System.lineSeparator())
                .append("\tto ")
                .append(format(currentEvent.getStart())).append(System.lineSeparator());
        }
        if (!prevEvent.getEnd().equals(currentEvent.getEnd())) {
            diff.append("Event end changed").append(System.lineSeparator())
                .append("\tfrom ")
                .append(format(prevEvent.getEnd())).append(System.lineSeparator())
                .append("\tto ")
                .append(format(currentEvent.getEnd())).append(System.lineSeparator());
        }
        if (prevEvent.getLocation() != null && !prevEvent.getLocation().equals(currentEvent.getLocation())) {
            diff.append("Event location changed").append(System.lineSeparator())
                .append("\tfrom ")
                .append(prevEvent.getLocation().getName()).append(System.lineSeparator())
                .append("\tto ")
                .append(currentEvent.getLocation().getName()).append(System.lineSeparator());
        }
        if (!prevEvent.getUsers().equals(currentEvent.getUsers())) {
            for(User user : prevEvent.getUsers()) {
                if(!currentEvent.getUsers().contains(user)) {
                    diff.append(user.getName())
                        .append(" has been removed")
                        .append(System.lineSeparator());
                }
            }
            for(User user : currentEvent.getUsers()) {
                if(!prevEvent.getUsers().contains(user)) {
                    diff.append(user.getName())
                            .append(" has been added")
                            .append(System.lineSeparator());
                }
            }
        }
        if (!prevEvent.getEquipment().equals(currentEvent.getEquipment())) {
            for (Equipment equipment : prevEvent.getEquipment()) {
                if (!currentEvent.getEquipment().contains(equipment)) {
                    diff.append(equipment.getName())
                            .append(" has been removed")
                            .append(System.lineSeparator());
                }
            }
            for (Equipment equipment : currentEvent.getEquipment()) {
                if (!prevEvent.getEquipment().contains(equipment)) {
                    diff.append(equipment.getName())
                            .append(" has been added")
                            .append(System.lineSeparator());
                }
            }
        }
        return diff.toString();
    }

    private void sendUpdatingEventEmail(final Event event, final Event prevEvent, final User author) {
        new Thread(){
            @Override
            public void run() {
                for(User user : event.getUsers()) {
                    if(!user.equals(author)) {
                        sendUpdatingEventEmail(event, prevEvent, author, user);
                    }
                }
            }
        }.start();
    }

    private void sendUpdatingEventEmail(Event event, Event prevEvent, User author, User receiver) {
        String email = receiver.getEmail();
        if(!StringUtils.isEmpty(email)) {
            String diff = generateDiff(event, prevEvent);
            if(!StringUtils.isEmpty(diff)){
                String emailBody = new StringBuilder()
                        .append("Hello ").append(receiver.getName()).append(System.lineSeparator())
                        .append("Event was changed by ").append(author.getName()).append(System.lineSeparator())
                        .append(diff).toString();
                mailHelper.sendMail(email, "Event was updated", emailBody);
            }
        }
    }

    private void sendDeletingEventEmail(final Event event, final User author) {
        new Thread(){
            @Override
            public void run() {
                for (User user : event.getUsers()) {
                    sendDeletingEventEmail(event, author, user);
                }
                if (!event.getUsers().contains(event.getAuthor())) {
                    sendDeletingEventEmail(event, author, event.getAuthor());
                }
            }
        }.start();
    }

    private void sendDeletingEventEmail(Event event, User author, User receiver) {
        String email = receiver.getEmail();
        if(!StringUtils.isEmpty(email)) {
            String emailBody = new StringBuilder()
                    .append("Hello ").append(receiver.getName()).append(System.lineSeparator())
                    .append("Event was deleted by ").append(author.getName()).append(System.lineSeparator())
                    .append("Its title was ").append(event.getTitle()).toString();
            mailHelper.sendMail(email, "Event was updated", emailBody);
        }
    }

    private String format(Date date) {
        return DATE_FORMATTER.format(date);
    }
}
