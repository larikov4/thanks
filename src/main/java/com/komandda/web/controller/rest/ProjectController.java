package com.komandda.web.controller.rest;

import com.komandda.entity.Location;
import com.komandda.entity.Project;
import com.komandda.service.LocationService;
import com.komandda.service.ProjectService;
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
@RequestMapping("/rest/projects")
public class ProjectController {

    @Autowired
    private ProjectService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<Project> findAll() {
        return service.findByDeletedFalse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Project findOne(@PathVariable("id") String id) {
        return service.findOne(id);
    }

    @PreAuthorize("hasAuthority('project_edit')")
    @RequestMapping(method = RequestMethod.POST)
    public Project add(@RequestBody Project project) {
        return service.insert(project);
    }

    @PreAuthorize("hasAuthority('project_edit')")
    @RequestMapping(method = RequestMethod.PUT)
    public Project update(@RequestBody Project project) {
        return service.save(project);
    }

    @PreAuthorize("hasAuthority('project_edit')")
    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestBody Project project) {
        service.delete(project);
    }
}
