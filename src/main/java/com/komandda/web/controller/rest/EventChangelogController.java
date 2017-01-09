package com.komandda.web.controller.rest;

import com.komandda.entity.EventChangeItem;
import com.komandda.entity.Event;
import com.komandda.service.EventChangelogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Yevhn on 29.05.2016.
 */
@RestController
@RequestMapping("/rest/event/changelog")
public class EventChangelogController {

    @Autowired
    private EventChangelogService service;

    @RequestMapping(value = "/{diffItemId}", method = RequestMethod.GET)
    public List<EventChangeItem> findOne(@PathVariable("diffItemId") String diffItemId) {
        return service.findByDiffItemId(diffItemId);
    }
}
