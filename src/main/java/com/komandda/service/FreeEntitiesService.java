package com.komandda.service;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.Location;
import com.komandda.entity.User;
import com.komandda.repository.EquipmentRepository;
import com.komandda.repository.EventRepository;
import com.komandda.repository.LocationRepository;
import com.komandda.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yevhen on 04.05.2016.
 */
@Service
public class FreeEntitiesService {
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(FreeEntitiesService.class);

//    private final int fourHours = 1000 * 60 * 60 * 4;

    private List<Event> getActiveEvents(Date start, Date end, String id) {
        List<Event> activeEvents = new ArrayList<>();

        List<Event> all = eventRepository.findAll();
        logger.info("All:" + all);

        for(Event event : all){
            if(event.getStart().getTime() < start.getTime() && event.getEnd().getTime() <= start.getTime()){
                continue;
            }
            if(event.getStart().getTime() >= end.getTime() && event.getEnd().getTime() > end.getTime()){
                continue;
            }
            if(event.getId().equals(id)){
                continue;
            }
            activeEvents.add(event);
        }
        logger.info("Active events:" + activeEvents.toString());
        return activeEvents;
    }

//    private List<Event> getEvents() {
//        List<Event> all = eventRepository.findAll();
//        for(Event event : all){
//            event.setStart(new Date(event.getStart().getTime() + fourHours));
//            event.setEnd(new Date(event.getEnd().getTime() + fourHours));
//        }
//        return all;
//    }

    public List<Location> getFreeLocations(final Date start, final Date end, String id){
        List<Location> locations = locationRepository.findAll();
        List<Event> activeEvents = getActiveEvents(start, end, id);
        for(Event event : activeEvents) {
            Location location = event.getLocation();
            if(location != null && !location.isParallel()) {
                locations.remove(location);
            }
        }
        return locations;
    }

    public List<User> getFreeUsers(Date start, Date end, String id){
        List<User> users = userRepository.findAll();
        List<Event> activeEvents = getActiveEvents(start, end, id);
        for(Event event : activeEvents) {
            users.removeAll(event.getUsers());
        }
        return users;
    }

    public List<Equipment> getFreeEquipment(Date start, Date end, String id){
        List<Equipment> equipments = equipmentRepository.findAll();
        List<Event> activeEvents = getActiveEvents(start, end, id);
        for(Event event : activeEvents) {
            equipments.removeAll(event.getEquipment());
        }
        return equipments;
    }


}
