package com.komandda.service.dashboard.dataset.builder;

import com.komandda.entity.Dashboard;
import com.komandda.entity.Event;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Yevhen on 22.01.2017.
 */
@Service
public class ProjectEventBuilder extends DatasetBuilder {

    public Dashboard.Dataset build(List<Event> events, String projectName) {
        Map<String, Long> equipment = events.stream()
                .filter(event -> Objects.nonNull(event.getProject()))
                .filter(event -> event.getProject().getName().equals(projectName))
                .collect(Collectors.groupingBy(event -> DATE_FORMATTER.format(event.getStart()), Collectors.counting()));

        List<String> weekDays = getWeekDays();
        List<String> keys = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for(String date : weekDays) {
            Long eventsAmount = equipment.get(date);
            keys.add(date);
            values.add(eventsAmount == null ? 0 : Double.valueOf(eventsAmount));
        }
        return new Dashboard.Dataset(projectName, keys, values);
    }
}
