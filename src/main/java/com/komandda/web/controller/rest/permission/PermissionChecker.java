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
                !isSelfEditing(event, user)) {
            throw new MissingPermissionException();
        }
    }

    private boolean hasSelfEditEventPermission(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("self_event_edit"::equals);
    }

    private boolean isSelfEditing(Event event, User user) {
        return user.getId().equals(event.getAuthor().getId())
                || event.getUsers().stream().map(User::getId).anyMatch(user.getId()::equals);
    }
}
