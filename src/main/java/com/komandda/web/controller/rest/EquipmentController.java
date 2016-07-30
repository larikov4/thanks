package com.komandda.web.controller.rest;

import com.komandda.entity.Equipment;
import com.komandda.entity.User;
import com.komandda.service.EquipmentService;
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
@RequestMapping("/rest/equipment")
public class EquipmentController {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    @Autowired
    private EquipmentService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<Equipment> findAll() {
        return service.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Equipment findOne(@PathVariable("id") String id) {
        return service.findOne(id);
    }

    @PreAuthorize("hasAuthority('equipment_edit')")
    @RequestMapping(method = RequestMethod.POST)
    public Equipment add(@RequestBody Equipment equipment) {
        return service.insert(equipment);
    }

    @PreAuthorize("hasAuthority('equipment_edit')")
    @RequestMapping(method = RequestMethod.PUT)
    public Equipment update(@RequestBody Equipment equipment) {
        return service.save(equipment);
    }

    @PreAuthorize("hasAuthority('equipment_edit')")
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody Equipment equipment) {
        service.delete(equipment);
    }

    @RequestMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Equipment> getFree(String start, String end, String id) throws ParseException {
        return service.getFree(SIMPLE_DATE_FORMAT.parse(start), SIMPLE_DATE_FORMAT.parse(end), id);
    }
}
