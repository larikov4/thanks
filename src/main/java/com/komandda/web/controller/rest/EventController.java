package com.komandda.web.controller.rest;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.EquipmentService;
import com.komandda.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

/**
 * Created by Yevhn on 29.05.2016.
 */
@RestController
@RequestMapping("/rest/events")
public class EventController {

    @Autowired
    private EventService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<Event> findAll() {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event findOne(@PathVariable("id") String id) {
        return service.findOne(id);
    }

    @PreAuthorize("hasAuthority('event_edit')")
    @RequestMapping(method = RequestMethod.POST)
    public Event add(@RequestBody Event event, @AuthenticationPrincipal User user) {
        return service.insert(event, user);
    }

    @PreAuthorize("hasAuthority('event_edit')")
    @RequestMapping(method = RequestMethod.PUT)
    public Event update(@RequestBody Event event, @AuthenticationPrincipal User user) {
        return service.save(event, user);
    }

    @PreAuthorize("hasAuthority('event_edit')")
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody Event event, @AuthenticationPrincipal User user) {
        service.delete(event, user);
    }

}
