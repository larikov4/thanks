package com.komandda.web.controller.rest;

import com.komandda.entity.Location;
import com.komandda.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Yevhn on 29.05.2016.
 */
@RestController
@RequestMapping("/rest/locations")
public class LocationController {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @Autowired
    private LocationService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<Location> findAll() {
        return service.findByDeletedFalse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Location findOne(@PathVariable("id") String id) {
        return service.findOne(id);
    }

    @PreAuthorize("hasAuthority('location_edit')")
    @RequestMapping(method = RequestMethod.POST)
    public Location add(@RequestBody Location location) {
        return service.insert(location);
    }

    @PreAuthorize("hasAuthority('location_edit')")
    @RequestMapping(method = RequestMethod.PUT)
    public Location update(@RequestBody Location location) {
        return service.save(location);
    }

    @PreAuthorize("hasAuthority('location_edit')")
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody Location location) {
        service.delete(location);
    }

    @RequestMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Location> getFree(String start, String end, String id) throws ParseException {
        return service.getFree(SIMPLE_DATE_FORMAT.parse(start), SIMPLE_DATE_FORMAT.parse(end), id);
    }

    @RequestMapping(value = "/series/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Location> getSeriesFree(String start, String end, String seriesId) throws ParseException {
        return service.getSeriesFree(SIMPLE_DATE_FORMAT.parse(start), SIMPLE_DATE_FORMAT.parse(end), seriesId);
    }
}
