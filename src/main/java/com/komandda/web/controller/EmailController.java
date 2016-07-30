package com.komandda.web.controller;

import com.komandda.service.mail.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Yevhen_Larikov
 */
@Controller
@RequestMapping("/mail")
public class EmailController {

    @Autowired
    private MailHelper mailHelper;


    @RequestMapping(method = RequestMethod.GET)
    public String getEventById() {
        mailHelper.sendMail("larikov4@gmail.com", "zrikotofuka@gmail.com","test", "success");
        return "email was sent";
    }

}
