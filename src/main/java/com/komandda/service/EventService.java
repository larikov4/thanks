package com.komandda.service;

import com.komandda.entity.*;
import com.komandda.exception.UsingBusyResourcesException;
import com.komandda.repository.EventRepository;
import com.komandda.service.email.service.EventEmailSenderService;
import com.komandda.service.filter.EventFilterDto;
import com.komandda.service.helper.DateHelper;
import com.komandda.validator.FreeEntitiesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

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
    private EventEmailSenderService emailSender;

    @Autowired
    private FreeEntitiesValidator validator;

    @Autowired
    private DateHelper dateHelper;

    public List<Event> findOnCurrentWeek() {
        return hidePassword(repository.findBetweenTwoDatesQuery(dateHelper.getWeekBeginning(new Date()),
                dateHelper.getNextWeekBeggining(new Date())));
    }

    public List<Event> findBetween(Date start, Date end) {
        return hidePassword(repository.findBetweenTwoDatesQuery(start, end));
    }

    public Event findOne(String id) {
        return hidePassword(repository.findOne(id));
    }

    public List<Event> findBySeriesId(String seriesId) {
        return hidePassword(repository.findBySeriesId(seriesId));
    }

    public Event insert(Event event, User author) {
        setEmptyEquipmentListIfAbsent(event);
        if(validator.isUsingBusyResources(event)) {
            throw new UsingBusyResourcesException();
        }
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
        setEmptyEquipmentListIfAbsent(event);
        if(validator.isUsingBusyResources(event)) {
            throw new UsingBusyResourcesException();
        }
        Event prevEvent = repository.findOne(event.getId());
        if(event.getSeriesId()==null) {
            emailSender.sendUpdatingEventEmail(event, author, prevEvent);
        }
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
        EventChangeItem eventChangeItem = new EventChangeItem(author, new Date(), event);
        eventChangeItem.setDeletedEvent(true);
        changelog.insert(eventChangeItem);
        return hidePassword(event);
    }

    public List<Event> findBy(final EventFilterDto dto) {
        final String locationId = dto.getLocationId();
        final List<String> names = dto.getNames();
        final List<String> projectsIds = dto.getProjects();
        final List<String> equipmentIds = dto.getEquipmentIds();

        List<String> safeNames = Optional.ofNullable(names).orElse(Collections.emptyList());
        List<String> safeProjectsIds = Optional.ofNullable(projectsIds).orElse(Collections.emptyList());
        List<String> safeEquipmentIds = Optional.ofNullable(equipmentIds).orElse(Collections.emptyList());
        List<Event> events = findBetween(dto.getStart(), dto.getEnd());
        if(!safeProjectsIds.isEmpty()) {
            events = events.stream()
                    .filter(event -> Objects.nonNull(event.getProject()))
                    .filter(event -> safeProjectsIds.contains(event.getProject().getId()))
                    .collect(toList());
            if(safeNames.isEmpty() && safeEquipmentIds.isEmpty() && isEmpty(locationId)){
                return events;
            }
        }
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
