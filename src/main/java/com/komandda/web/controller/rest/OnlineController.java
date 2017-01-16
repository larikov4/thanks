package com.komandda.web.controller.rest;

import com.komandda.entity.User;
import com.komandda.service.OnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Created by Yevhen on 16.01.2017.
 */
@RestController
@RequestMapping("/rest/online")
public class OnlineController {
    @Autowired
    private OnlineService service;

    @RequestMapping(method = RequestMethod.GET)
    public Collection<String> findAll(@AuthenticationPrincipal User user) {
        service.markOnline(user);
        return service.getOnlineUsers();
    }
}
