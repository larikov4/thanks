package com.komandda.service.converter;

import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by Yevhen_Larikov on 09-Nov-16.
 */
@Service
public class DateConverter {

    public Date asDate(LocalDateTime localDateTime) {
        return new Date(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
