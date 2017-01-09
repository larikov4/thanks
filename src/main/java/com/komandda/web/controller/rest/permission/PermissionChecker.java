package com.komandda.web.controller.rest.permission;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.web.controller.rest.permission.exception.MissingPermissionException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class PermissionChecker {
    public void checkSelfEditPermission(Event event, User user) {
        if(hasSelfEditEventPermission(user) &&
                !user.getId().equals(event.getAuthor().getId())) {
            throw new MissingPermissionException();
        }
    }

    private boolean hasSelfEditEventPermission(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("self_event_edit"::equals);
    }
}
