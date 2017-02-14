package com.komandda.validator;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.Location;
import com.komandda.entity.User;
import com.komandda.service.FreeEntitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Yevhen on 12.02.2017.
 */
@Service
public class FreeEntitiesValidator {

    @Autowired
    private FreeEntitiesService service;

    public boolean isUsingBusyResources(Event event){
        List<Location> freeLocations = service.getFreeLocations(event.getStart(), event.getEnd(), event.getId());
        if(event.getLocation()!= null && !freeLocations.contains(event.getLocation())) {
            return true;
        }
        List<User> freeUsers = service.getFreeUsers(event.getStart(), event.getEnd(), event.getId());
        boolean hasBusyUsers = event.getUsers().stream().anyMatch(user -> !freeUsers.contains(user));
        List<Equipment> freeEquipment = service.getFreeEquipment(event.getStart(), event.getEnd(), event.getId());
        boolean hasBusyEquipment = event.getEquipment().stream().anyMatch(user -> !freeEquipment.contains(user));
        return hasBusyUsers || hasBusyEquipment;
    }
}
