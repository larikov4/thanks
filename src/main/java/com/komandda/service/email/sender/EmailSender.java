package com.komandda.service.email.sender;

import com.komandda.entity.User;
import com.komandda.service.email.template.EmailTemplate;

import java.util.Collection;


public interface EmailSender {
    void send(Collection<User> users, EmailTemplate template);
}
