package com.komandda.web.controller.rest;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.LocationService;
import com.komandda.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Yevhen_Larikov on 09-Nov-16.
 */
@RestController
@RequestMapping("/rest/series")
public class SeriesController {

    @Autowired
    private SeriesService service;

    @Autowired
    private SimpMessagingTemplate webSocket;

    @PreAuthorize("hasAuthority('event_edit')")
    @RequestMapping(method = RequestMethod.POST)
    public void add(@RequestBody Event event, @AuthenticationPrincipal User user) {
        webSocket.convertAndSend("/event/create", service.insert(event, user));
    }

    @PreAuthorize("hasAuthority('event_edit')")
    @RequestMapping(method = RequestMethod.PUT)
    public void update(@RequestBody Event event, @AuthenticationPrincipal User user) {
        webSocket.convertAndSend("/event/update", service.save(event, user));
    }

    @PreAuthorize("hasAuthority('event_edit')")
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody Event event, @AuthenticationPrincipal User user) {
        webSocket.convertAndSend("/event/delete", service.delete(event, user));
    }
}
