package com.komandda.service.dashboard.dataset.builder;

import com.komandda.entity.Dashboard;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yevhen on 22.01.2017.
 */
@Service
public class FreeSlotOperatorDatasetBuilder extends DatasetBuilder {
    private static final int WORKING_DAY_START_HOURS = 11;
    private static final int WORKING_DAY_END_HOURS = 20;

    public Dashboard.Dataset build(List<Event> events, String userId) {
        Map<String, Double> filteredEvents =
                events.stream()
                        .filter(event -> containsUser(event.getUsers(), userId))
                        .flatMap(this::splitEventsWithLongDuration)
                        .collect(Collectors.groupingBy(
                                event -> DATE_FORMATTER.format(event.getStart()),
                                Collectors.summingDouble(this::calculateBusyHours)));


        List<String> weekDays = getWeekDays();
        List<String> keys = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (String date : weekDays) {
            Double busyTime = filteredEvents.get(date);
            keys.add(date);
            values.add(busyTime == null ? calculateFreeSlot(0) : calculateFreeSlot(busyTime));
        }

//        for(Map.Entry<String, Double> entry : filteredEvents.entrySet()) {
//            entry.setValue(calculateFreeSlot(entry.getValue()));
//        }

        return new Dashboard.Dataset(userId, keys, values);
    }

    private boolean containsUser(List<User> users, String userId) {
        return users.stream().anyMatch(user -> user.getId().equals(userId));
    }

    private Date withHours(Date date, int hours) {
        return dateHelper.asDate(dateHelper.asLocalDateTime(date)
                .withHour(hours)
                .withMinute(0)
                .withSecond(0));
    }

    private double calculateBusyHours(Event event) {
        return dateHelper.calculateIntersectionInHours(
                withHours(event.getStart(), WORKING_DAY_START_HOURS),
                withHours(event.getStart(), WORKING_DAY_END_HOURS),
                event.getStart(),
                event.getEnd());
    }

    private double calculateFreeSlot(double busyHours) {
        double freeSlotVolume = WORKING_DAY_END_HOURS - WORKING_DAY_START_HOURS - busyHours;
        if (freeSlotVolume > 0) {
            return freeSlotVolume;
        }
        return 0;
    }
}
