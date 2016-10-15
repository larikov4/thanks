package com.komandda.web.controller;

import com.komandda.service.EquipmentService;
import com.komandda.service.EventService;
import com.komandda.service.LocationService;
import com.komandda.service.UserService;
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
    private EventService eventService;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("events", eventService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("equipment", equipmentService.findAll());
        model.addAttribute("locations", locationService.findAll());
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


//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public @ResponseBody
//    Map<String, String> handleMyRuntimeException(Exception exception) {
//        return Collections.singletonMap("errorMessage",
//                "Some data I want to send back to the client.");
//    }
}