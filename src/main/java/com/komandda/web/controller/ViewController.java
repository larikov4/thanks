package com.komandda.web.controller;

import com.komandda.service.*;
import com.komandda.service.dashboard.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

/**
 * @author Yevhen_Larikov
 */
@Controller
public class ViewController {

    @Autowired
    private UserService userService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EventService eventService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private EventChangelogService eventChangelogService;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("equipment", equipmentService.findAll());
        model.addAttribute("locations", locationService.findAll());
        model.addAttribute("projects", projectService.findAll());
        return "calendar";
    }

    @RequestMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findByDeletedFalse());
        return "users";
    }


    @RequestMapping("/locations")
    public String locations(Model model) {
        model.addAttribute("locations", locationService.findByDeletedFalse());
        return "locations";
    }


    @RequestMapping("/equipment")
    public String equipment(Model model) {
        model.addAttribute("equipment", equipmentService.findByDeletedFalse());
        return "equipment";
    }

    @RequestMapping("/projects")
    public String project(Model model) {
        model.addAttribute("projects", projectService.findByDeletedFalse());
        return "projects";
    }

    @RequestMapping("/deletedEvents")
    public String deletedEvents(Model model) {
        model.addAttribute("eventChangeItems", eventChangelogService.findDeleted());
        return "deletedEvents";
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.findByDeletedFalse());
        model.addAttribute("projects", projectService.findByDeletedFalse());
        model.addAttribute("locations", locationService.findByDeletedFalse());
        model.addAttribute("dashboard", dashboardService.buildDashboard());
        return "dashboard";
    }


//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public @ResponseBody
//    Map<String, String> handleMyRuntimeException(Exception exception) {
//        return Collections.singletonMap("errorMessage",
//                "Some data I want to send back to the client.");
//    }
}
