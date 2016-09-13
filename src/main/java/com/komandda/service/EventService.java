package com.komandda.service;

import com.komandda.entity.Equipment;
import com.komandda.entity.EventChangeItem;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.repository.EventRepository;
import com.komandda.service.mail.MailHelper;
import com.komandda.web.controller.EmailController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by yevhen on 28.06.16.
 */
@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private EventChangelogService changelog;

    @Autowired
    private MailHelper mailHelper;

    public List<Event> findAll() {
        return repository.findAll();
    }

    public Event findOne(String id) {
        return repository.findOne(id);
    }

    public Event insert(Event event, User author) {
        event.setCreated(new Date());
        Event eventWithId = repository.insert(event);
        changelog.insert(new EventChangeItem(author, new Date(), eventWithId));
        sendCreationEventEmail(event);
        return eventWithId;
    }

    private void sendCreationEventEmail(Event event) {
        StringBuilder eventBuilder = new StringBuilder();
        eventBuilder.append("Title ").append(event.getTitle()).append(System.lineSeparator())
            .append("Date ").append(event.getStart() + " to " + event.getEnd()).append(System.lineSeparator())
            .append("Author ").append(event.getAuthor().getUsername()).append(System.lineSeparator())
            .append("Location ").append(event.getLocation().getName()).append(System.lineSeparator());
        eventBuilder.append("Users: ").append(System.lineSeparator());
        for(User user : event.getUsers()) {
            eventBuilder.append("\t").append(user.getUsername()).append(System.lineSeparator());
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
                    .append("Hello ").append(user.getUsername()).append(System.lineSeparator())
                    .append("New event was created.").append(System.lineSeparator())
                    .append(createdEvent).toString();
                mailHelper.sendMail(email, "New event created", emailBody);
            }
        }
    }

    public Event save(Event event, User author) {
        changelog.insert(new EventChangeItem(author, new Date(), event));
        sendUpdatingEventEmail(event);
        return repository.save(event);
    }

    public void delete(Event event) {
        repository.delete(event);
    }


    private String generateDiff(Event currentEvent, Event prevEvent) {
        StringBuilder diff = new StringBuilder();
        if (!prevEvent.getTitle().equals(currentEvent.getTitle())) {
            diff.append("Title changed from ")
                .append(prevEvent.getTitle())
                .append(" to ")
                .append(currentEvent.getTitle())
                .append(System.lineSeparator());
        }
        if (!prevEvent.getStart().equals(currentEvent.getStart())) {
            diff.append("Event start changed from ")
                .append(prevEvent.getStart())
                .append(" to ")
                .append(currentEvent.getStart())
                .append(System.lineSeparator());
        }
        if (!prevEvent.getEnd().equals(currentEvent.getEnd())) {
            diff.append("Event end changed from ")
                .append(prevEvent.getEnd())
                .append(" to ")
                .append(currentEvent.getEnd())
                .append(System.lineSeparator());
        }
        if (!prevEvent.getLocation().equals(currentEvent.getLocation())) {
            diff.append("Event location changed from ")
                .append(prevEvent.getLocation())
                .append(" to ")
                .append(currentEvent.getLocation())
                .append(System.lineSeparator());
        }
        if (!prevEvent.getUsers().equals(currentEvent.getUsers())) {
            for(User user : prevEvent.getUsers()) {
                if(!currentEvent.getUsers().contains(user)) {
                    diff.append(user.getUsername())
                        .append(" has been deleted")
                        .append(System.lineSeparator());
                }
            }
            for(User user : currentEvent.getUsers()) {
                if(!prevEvent.getUsers().contains(user)) {
                    diff.append(user.getUsername())
                            .append(" has been added")
                            .append(System.lineSeparator());
                }
            }
        }
        if (!prevEvent.getEquipment().equals(currentEvent.getEquipment())) {
            for (Equipment equipment : prevEvent.getEquipment()) {
                if (!currentEvent.getEquipment().contains(equipment)) {
                    diff.append(equipment.getName())
                            .append(" has been deleted")
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

    private void sendUpdatingEventEmail(Event event) {
        for(User user : event.getUsers()) {
            String email = user.getEmail();
            if(!StringUtils.isEmpty(email)) {
                Event prevEvent = repository.findOne(event.getId());
                String emailBody = new StringBuilder()
                        .append("Hello ").append(user.getUsername()).append(System.lineSeparator())
                        .append("Event was changed.").append(System.lineSeparator())
                        .append(generateDiff(event, prevEvent)).toString();
                mailHelper.sendMail(email, "Event was updated", emailBody);
            }
        }
    }
}
