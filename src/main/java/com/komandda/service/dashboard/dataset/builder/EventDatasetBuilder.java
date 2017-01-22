package com.komandda.service.dashboard.dataset.builder;

import com.komandda.entity.Dashboard;
import com.komandda.entity.Event;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Yevhen on 22.01.2017.
 */
@Service
public class EventDatasetBuilder extends DatasetBuilder {

    public Dashboard.Dataset build(List<Event> events) {
        Map<String, Long> filteredEvents = events.stream()
                .collect(Collectors.groupingBy(event -> DATE_FORMATTER.format(event.getStart()), Collectors.counting()));

        List<String> weekDays = getWeekDays();
        List<String> keys = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for(String date : weekDays) {
            Long eventsAmount = filteredEvents.get(date);
            keys.add(date);
            values.add(eventsAmount == null ? 0 : Double.valueOf(eventsAmount));
        }
        return new Dashboard.Dataset("events", keys, values);
    }
}
