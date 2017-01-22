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
public class LocationDatasetBuilder extends DatasetBuilder{

    public Dashboard.Dataset build(List<Event> events) {
        Map<String, Long> equipment = events.stream()
                .filter(event -> Objects.nonNull(event.getLocation()))
                .collect(Collectors.groupingBy(event -> event.getLocation().getId(), Collectors.counting()));

        List<String> keys = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for(Map.Entry<String, Long> entry : equipment.entrySet()) {
            keys.add(entry.getKey());
            values.add(Double.valueOf(entry.getValue()));
        }
        return new Dashboard.Dataset("locations", keys, values);
    }
}
