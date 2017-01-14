package com.komandda.web.controller.rest;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.EventService;
import com.komandda.service.filter.EventFilterDto;
import com.komandda.web.controller.rest.permission.PermissionChecker;
import com.komandda.web.controller.rest.permission.exception.MissingPermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * Created by Yevhn on 29.05.2016.
 */
@RestController
@RequestMapping("/rest/events")
public class EventController {

    @Autowired
    private EventService service;

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private PermissionChecker permissionChecker;

    @RequestMapping(method = RequestMethod.GET)
    public List<Event> findAll() {
        return service.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event findOne(@PathVariable("id") String id) {
        return service.findOne(id);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public List<Event> findBy(@RequestParam(required = false, name = "location")String locationId,
                              @RequestParam(required = false, name = "projects[]") List<String> projectIds,
                              @RequestParam(required = false, name = "users[]") List<String> userIds,
                              @RequestParam(required = false, name = "equipment[]") List<String> equipmentIds) {
        EventFilterDto dto = new EventFilterDto(locationId, projectIds, userIds, equipmentIds);
        return service.findBy(dto);
    }

    @PreAuthorize("hasAnyAuthority('event_edit', 'self_event_edit')")
    @RequestMapping(method = RequestMethod.POST)
    public void add(@RequestBody Event event, @AuthenticationPrincipal User user) {
        webSocket.convertAndSend("/event/create", service.insert(event, user));
    }

    @PreAuthorize("hasAnyAuthority('event_edit', 'self_event_edit')")
    @RequestMapping(method = RequestMethod.PUT)
    public void update(@RequestBody Event event, @AuthenticationPrincipal User user) {
        permissionChecker.checkSelfEditPermission(event, user);
        webSocket.convertAndSend("/event/update", service.save(event, user));
    }

    @PreAuthorize("hasAnyAuthority('event_edit', 'self_event_edit')")
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody Event event, @AuthenticationPrincipal User user) {
        permissionChecker.checkSelfEditPermission(event, user);
        webSocket.convertAndSend("/event/delete", service.delete(event, user));
    }

}
