package com.komandda.web.controller.rest;

import com.komandda.entity.User;
import com.komandda.service.UserService;
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
@RequestMapping("/rest/users")
public class UsersController {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @Autowired
    private UserService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<User> findAll() {
        return service.findByDeletedFalse();
    }

    @PreAuthorize("hasAuthority('user_edit')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User findOne(@PathVariable("id") String id) {
        return service.findOne(id);
    }

    @PreAuthorize("hasAuthority('user_edit')")
    @RequestMapping(method = RequestMethod.POST)
    public User add(@RequestBody User user) {
        return service.insert(user);
    }

    @PreAuthorize("hasAuthority('user_edit')")
    @RequestMapping(method = RequestMethod.PUT)
    public User update(@RequestBody User user) {
        return service.save(user);
    }

    @PreAuthorize("hasAuthority('user_edit')")
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody User user) {
        service.delete(user);
    }

    @RequestMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getFree(String start, String end, String id) throws ParseException {
        return service.getFree(SIMPLE_DATE_FORMAT.parse(start), SIMPLE_DATE_FORMAT.parse(end), id);
    }

    @RequestMapping(value = "/series/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getSeriesFree(String start, String end, String seriesId) throws ParseException {
        return service.getSeriesFree(SIMPLE_DATE_FORMAT.parse(start), SIMPLE_DATE_FORMAT.parse(end), seriesId);
    }
}
