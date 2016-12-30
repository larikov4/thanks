package com.komandda.service;

import com.komandda.entity.Equipment;
import com.komandda.entity.EventChangeItem;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.repository.EventRepository;
import com.komandda.service.email.facade.EventEmailSenderFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private EventEmailSenderFacade emailSender;


    public List<Event> findAll() {
        return hidePassword(repository.findAll());
    }

    public Event findOne(String id) {
        return hidePassword(repository.findOne(id));
    }

    public Event insert(Event event, User author) {
        setEmptyEquipmentListIfAbsent(event);
        if(event.getSeriesId()==null){
            emailSender.sendCreationEventEmail(event, author);
        }

        event.setCreated(new Date());
        Event eventWithId = repository.insert(event);

        hidePassword(eventWithId);
        userService.hidePassword(author);

        changelog.insert(new EventChangeItem(author, new Date(), eventWithId));
        return eventWithId;
    }

    public Event save(Event event, User author) {
        Event prevEvent = repository.findOne(event.getId());
        if(event.getSeriesId()==null) {
            emailSender.sendUpdatingEventEmail(event, author, prevEvent);
        }
        setEmptyEquipmentListIfAbsent(event);
        Event savedEvent = repository.save(event);

        hidePassword(savedEvent);
        userService.hidePassword(author);

        changelog.insert(new EventChangeItem(author, new Date(), event));
        return savedEvent;
    }

    public Event delete(Event event, User author) {
        repository.delete(event);
        if(event.getSeriesId()==null) {
            emailSender.sendDeletingEventEmail(event, author);
        }
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
}
