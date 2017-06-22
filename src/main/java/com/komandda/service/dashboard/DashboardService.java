package com.komandda.service.dashboard;

import com.komandda.entity.*;
import com.komandda.service.EventService;
import com.komandda.service.ProjectService;
import com.komandda.service.UserService;
import com.komandda.service.dashboard.dataset.builder.*;
import com.komandda.service.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by Yevhen on 21.01.2017.
 */
@Service
public class DashboardService {

    @Autowired
    private EventService eventService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private DateHelper dateHelper;

    @Autowired
    private EventDatasetBuilder eventDatasetBuilder;

    @Autowired
    private LocationDatasetBuilder locationDatasetBuilder;

    @Autowired
    private ProjectEventBuilder projectEventBuilder;

    @Autowired
    private OperatorDatasetBuilder operatorDatasetBuilder;

    @Autowired
    private FreeSlotOperatorDatasetBuilder freeSlotOperatorDatasetBuilder;

    public Dashboard buildDashboard() {
        List<Event> events = eventService.findOnCurrentWeek();
        List<Event> thisMonthEvents = getThisMonthEvents(events);
        List<Event> thisWeekEvents = getThisWeekEvents(events);
        Dashboard dashboard = new Dashboard();
        dashboard.setLocationWeekDataset(locationDatasetBuilder.build(thisWeekEvents));
        dashboard.setEventWeekDataset(eventDatasetBuilder.build(thisWeekEvents));
        addWeekProjectEventsDatasets(dashboard, thisWeekEvents);
        addWeekOperatorDatasets(dashboard, thisWeekEvents);
        return dashboard;
    }

    private void addWeekProjectEventsDatasets(Dashboard dashboard, List<Event> thisWeekEvents) {
        projectService.findAll().stream()
                .map(Project::getName)
                .forEach(projectName -> dashboard.addProjectEventsDataset(
                        projectEventBuilder.build(thisWeekEvents, projectName)));
    }

    private void addWeekOperatorDatasets(Dashboard dashboard, List<Event> thisWeekEvents) {
        userService.findByDeletedFalse().stream()
                .filter(User::isOperator)
                .map(User::getId)
                .forEach(userId -> {
                    dashboard.addOperatorEventsDataset(operatorDatasetBuilder.build(thisWeekEvents, userId));
                    dashboard.addFreeOperatorSlotEventsDataset(freeSlotOperatorDatasetBuilder.build(thisWeekEvents, userId));
                });
    }

    private List<Event> getThisMonthEvents(List<Event> events) {
        Date monthAgo = dateHelper.minusMonths(new Date(), 1);
        return events.stream()
                .filter(event -> dateHelper.isBefore(monthAgo, event.getStart()))
                .collect(Collectors.toList());
    }

    private List<Event> getThisWeekEvents(List<Event> events) {
        Date weekStart = dateHelper.minusSeconds(dateHelper.getWeekBeginning(new Date()), 1);
        Date nextWeekStart = dateHelper.getNextWeekBeggining(new Date());
        return events.stream()
                .flatMap(this::splitEventsWithLongDuration)
                .filter(event -> dateHelper.isBefore(weekStart, event.getStart()))
                .filter(event -> dateHelper.isBefore(event.getStart(), nextWeekStart))
                .collect(Collectors.toList());
    }

    private Stream<Event> splitEventsWithLongDuration(Event longDurationEvent) {
        if(dateHelper.getDateDiffInHours(longDurationEvent.getStart(), longDurationEvent.getEnd()) <= 24){
            return Collections.singletonList(longDurationEvent).stream();
        }
        List<Event> events = new ArrayList<>();
        Date startDate = longDurationEvent.getStart();
        Date endOfDay = dateHelper.getEndOfDay(longDurationEvent.getStart());
        while(dateHelper.isBefore(endOfDay, longDurationEvent.getEnd())) {
            Event event = new Event(longDurationEvent);
            event.setStart(startDate);
            event.setEnd(endOfDay);
            events.add(event);
            startDate = dateHelper.getStartOfNextDay(endOfDay);
            endOfDay = dateHelper.getEndOfDay(startDate);
        }
        if(dateHelper.getDateDiffInHours(startDate, longDurationEvent.getEnd()) > 0) {
            Event event = new Event(longDurationEvent);
            event.setStart(startDate);
            event.setEnd(longDurationEvent.getEnd());
            events.add(event);
        }
        return events.stream();
    }
}
