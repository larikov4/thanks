package com.komandda.service;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.Location;
import com.komandda.entity.User;
import com.komandda.service.helper.DateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.komandda.service.SeriesService.WEEKS_AMOUNT_IN_SERIES;

/**
 * Created by Yevhen on 04.05.2016.
 */
@Service
public class FreeSeriesEntitiesService {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EventService eventService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private DateHelper dateHelper;

    private final Logger logger = LoggerFactory.getLogger(FreeSeriesEntitiesService.class);

    private List<Event> getActiveEvents(Date start, Date end, String seriesId) {
        List<Event> activeEvents = new ArrayList<>();

        List<Event> all = eventService.findAll();
        logger.info("All:" + all);

        for (Event event : all) {
            for (int i = 0; i < WEEKS_AMOUNT_IN_SERIES; i++) {
                long currentStart = dateHelper.plusWeeks(start, i).getTime();
                long currentEnd = dateHelper.plusWeeks(end, i).getTime();

                if (event.getStart().getTime() < currentStart && event.getEnd().getTime() <= currentStart) {
                    continue;
                }
                if (event.getStart().getTime() >= currentEnd && event.getEnd().getTime() > currentEnd) {
                    continue;
                }
                if (seriesId.equals(event.getSeriesId())) {
                    continue;
                }
                activeEvents.add(event);
            }
        }
        logger.info("Active events:" + activeEvents.toString());
        return activeEvents;
    }

    public List<Location> getFreeLocations(final Date start, final Date end, String seriesId) {
        List<Location> locations = locationService.findByDeletedFalse();
        List<Event> activeEvents = getActiveEvents(start, end, seriesId);
        for (Event event : activeEvents) {
            Location location = event.getLocation();
            if (location != null && !location.isParallel()) {
                locations.remove(location);
            }
        }
        return locations;
    }

    public List<User> getFreeUsers(Date start, Date end, String seriesId) {
        List<User> users = userService.findByDeletedFalse();
        List<Event> activeEvents = getActiveEvents(start, end, seriesId);
        for (Event event : activeEvents) {
            users.removeAll(event.getUsers());
        }
        return users;
    }

    public List<Equipment> getFreeEquipment(Date start, Date end, String seriesId) {
        List<Equipment> equipments = equipmentService.findByDeletedFalse();
        List<Event> activeEvents = getActiveEvents(start, end, seriesId);
        for (Event event : activeEvents) {
            equipments.removeAll(event.getEquipment());
        }
        return equipments;
    }
}
