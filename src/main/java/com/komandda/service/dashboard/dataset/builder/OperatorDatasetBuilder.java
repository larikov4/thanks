package com.komandda.service.dashboard.dataset.builder;

import com.komandda.entity.Dashboard;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Yevhen on 22.01.2017.
 */
@Service
public class OperatorDatasetBuilder extends DatasetBuilder{

    public Dashboard.Dataset build(List<Event> events, String userId) {
        Map<String, Double> filteredEvents =
                events.stream()
                .filter(event -> containsUser(event.getUsers(), userId))
                .collect(Collectors.groupingBy(
                            event -> DATE_FORMATTER.format(event.getStart()),
                            Collectors.summingDouble(event -> dateHelper.getDateDiffInHours(event.getStart(), event.getEnd()))));

        List<String> weekDays = getWeekDays();
        List<String> keys = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for(String date : weekDays) {
            Double eventsAmount = filteredEvents.get(date);
            keys.add(date);
            values.add(eventsAmount == null ? 0 : eventsAmount);
        }
        return new Dashboard.Dataset(userId, keys, values);
    }

    private boolean containsUser(List<User> users, String userId) {
        return users.stream().anyMatch(user -> user.getId().equals(userId));
    }
}
