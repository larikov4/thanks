package com.komandda.service.helper;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yevhen_Larikov on 09-Nov-16.
 */
@Service
public class DateHelper {

    public Date asDate(LocalDateTime localDateTime) {
        return new Date(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public Date plusWeeks(Date date, int weeksAmount) {
        LocalDateTime shiftedLocalDateTime = asLocalDateTime(date).plusWeeks(weeksAmount);
        return asDate(shiftedLocalDateTime);
    }

    public long getDurationFromWeekBeginning(Date date) {
        return date.getTime() - getWeekBeginning(date).getTime();
    }

    public Date addDifference(Date date, long difference) {
        return new Date(getWeekBeginning(date).getTime() + difference);
    }

    private Date getWeekBeginning(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return new Date(cal.getTimeInMillis());
    }
}
