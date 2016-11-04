package com.komandda.service.email.sender;

import com.komandda.entity.User;
import com.komandda.service.email.template.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;

@Service
public class EmailSenderImpl implements EmailSender {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    @Override
    public void send(Collection<User> users, EmailTemplate template) {
        for(User user : users) {
            if(!StringUtils.isEmpty(user.getEmail())) {
                String emailBody = addGreetingsToBody(template.resolveBody(), user);
                sendMail(user.getEmail(), template.resolveSubject(), emailBody);
            }
        }
    }

    private String addGreetingsToBody(String body, User receiver) {
        return "Dear " + receiver.getName() + ",\n" + body;
    }

    private void sendMail(String receiver, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
