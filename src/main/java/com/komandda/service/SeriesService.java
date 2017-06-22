package com.komandda.service;

import com.komandda.entity.Event;
import com.komandda.entity.EventChangeItem;
import com.komandda.entity.Series;
import com.komandda.entity.User;
import com.komandda.entity.comparator.EventComparatorByDate;
import com.komandda.exception.UsingBusyResourcesException;
import com.komandda.repository.SeriesRepository;
import com.komandda.service.email.service.SeriesEventEmailSenderService;
import com.komandda.service.helper.DateHelper;
import com.komandda.validator.FreeEntitiesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeriesService {
    public static final int WEEKS_AMOUNT_IN_SERIES = 8;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventChangelogService eventChangelogService;

    @Autowired
    private DateHelper dateHelper;

    @Autowired
    private SeriesEventEmailSenderService emailSender;

    @Autowired
    private FreeEntitiesValidator validator;

    public List<Series> findAll() {
        return seriesRepository.findAll();
    }

    public List<Event> insert(Event event, User author) {
        setEmptyEquipmentListIfAbsent(event);
        if(validator.isUsingBusyResources(event)) {
            throw new UsingBusyResourcesException();
        }
        Series series = new Series(event, dateHelper.plusWeeks(event.getStart(), WEEKS_AMOUNT_IN_SERIES));
        Series seriesWithId = seriesRepository.insert(series);
        event.setSeriesId(seriesWithId.getId());
        emailSender.sendCreationEventEmail(event, author);
        return insertSeriesEvents(event, author);
    }

    public List<Event> save(Event updatingEvent, User author) {
        setEmptyEquipmentListIfAbsent(updatingEvent);
        if(validator.isUsingBusyResources(updatingEvent)) {
            throw new UsingBusyResourcesException();
        }
        Event prevEvent = eventService.findOne(updatingEvent.getId());
        emailSender.sendUpdatingEventEmail(updatingEvent, author, prevEvent);
        long startDateDiff = dateHelper.getDurationFromWeekBeginning(updatingEvent.getStart());
        long endDateDiff = dateHelper.getDurationFromWeekBeginning(updatingEvent.getEnd());
        return eventService.findBySeriesId(updatingEvent.getSeriesId()).stream()
                .filter(currentEvent -> updatingEvent.getStart().before(currentEvent.getStart())
                        || updatingEvent.getId().equals(currentEvent.getId()))
                .map(currentEvent -> {
                    Event newEvent = new Event(updatingEvent);
                    newEvent.setId(currentEvent.getId());
                    newEvent.setStart(dateHelper.addDifference(currentEvent.getStart(), startDateDiff));
                    newEvent.setEnd(dateHelper.addDifference(currentEvent.getEnd(), endDateDiff));
                    return eventService.save(newEvent, author);
                })
                .collect(Collectors.toList());
    }

    public List<Event> delete(Event event, User author) {
        emailSender.sendDeletingEventEmail(event, author);
        String seriesId = event.getSeriesId();
        seriesRepository.delete(seriesId);
        return eventService.findBySeriesId(seriesId).stream()
                .filter(e -> event.getStart().before(e.getStart()) || event.getStart().equals(e.getStart()))
                .peek(e -> eventService.delete(e, author))
                .collect(Collectors.toList());
    }

    private List<Event> insertSeriesEvents(Event seriesEvent, User author) {
        List<Event> seriesEvents = new ArrayList<>();
        for (int i = 0; i < WEEKS_AMOUNT_IN_SERIES; i++) {
            Event event = new Event(seriesEvent);
            event.setStart(dateHelper.plusWeeks(event.getStart(), i));
            event.setEnd(dateHelper.plusWeeks(event.getEnd(), i));
            event.setId(null);
            seriesEvents.add(eventService.insert(event, author));
        }
        return seriesEvents;
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void addSeriesEvents() {
        Date threeWeeksLater = dateHelper.asDate(LocalDateTime.now().plusWeeks(WEEKS_AMOUNT_IN_SERIES - 1));
        seriesRepository.findAll().stream()
                .filter(series -> series.getEnd().before(threeWeeksLater))
                .map(Series::getId)
                .map(this::getLatestEvent)
                .forEach(this::continueExpiredSeries);
    }

    private Event getLatestEvent(String seriesId) {
        return eventService.findBySeriesId(seriesId).stream()
                .sorted(new EventComparatorByDate().reversed())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no event with specified seriesId"));
    }

    private void continueExpiredSeries(Event event) {
        String expiredEventId = event.getId();
        Date severalWeeksLater = dateHelper.asDate(LocalDateTime.now().plusWeeks(WEEKS_AMOUNT_IN_SERIES - 1));
        Date lastSeriesEventStart = null;
        while (event.getStart().before(severalWeeksLater)) {
            lastSeriesEventStart = dateHelper.plusWeeks(event.getStart(), 1);
            event = new Event(event);
            event.setStart(lastSeriesEventStart);
            event.setEnd(dateHelper.plusWeeks(event.getEnd(), 1));
            event.setId(null);
            eventService.insert(event, retrieveAuthorOfLastChange(expiredEventId));
        }
        refreshSeriesEnd(event.getSeriesId(), lastSeriesEventStart);
    }

    private void refreshSeriesEnd(String seriesId, Date seriesEnd) {
        if (seriesEnd != null) {
            Series series = seriesRepository.findOne(seriesId);
            series.setEnd(seriesEnd);
            seriesRepository.save(series);
        }
    }

    private User retrieveAuthorOfLastChange(String eventId) {
        List<EventChangeItem> diffs = eventChangelogService.findByDiffItemId(eventId);
        return diffs.get(diffs.size() - 1).getAuthor();
    }

    private void setEmptyEquipmentListIfAbsent(Event event) {
        if(event.getEquipment() == null) {
            event.setEquipment(Collections.emptyList());
        }
    }
}
