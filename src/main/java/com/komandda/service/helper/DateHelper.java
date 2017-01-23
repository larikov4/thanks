package com.komandda.service.helper;

import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    public Date plusDays(Date date, int daysAmount) {
        LocalDateTime shiftedLocalDateTime = asLocalDateTime(date).plusDays(daysAmount);
        return asDate(shiftedLocalDateTime);
    }

    public Date plusWeeks(Date date, int weeksAmount) {
        LocalDateTime shiftedLocalDateTime = asLocalDateTime(date).plusWeeks(weeksAmount);
        return asDate(shiftedLocalDateTime);
    }

    public Date minusMonths(Date date, int monthsAmount) {
        LocalDateTime shiftedLocalDateTime = asLocalDateTime(date).minusMonths(monthsAmount);
        return asDate(shiftedLocalDateTime);
    }

    public Date minusSeconds(Date date, int secondsAmount) {
        LocalDateTime shiftedLocalDateTime = asLocalDateTime(date).minusSeconds(secondsAmount);
        return asDate(shiftedLocalDateTime);
    }

    public boolean isBefore(Date first, Date second) {
        return asLocalDateTime(first).isBefore(asLocalDateTime(second));
    }

    public double getDateDiffInHours(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MINUTES.convert(diffInMillies,TimeUnit.MILLISECONDS) / 60.;
    }

    public double calculateIntersectionInHours(Date firstStart, Date firstEnd, Date secondStart, Date secondEnd) {
        if(isBefore(firstEnd, secondStart) || isBefore(secondEnd, firstStart)) {
            return 0;
        }
        Date intersectionStart;
        Date intersectionEnd;
        if(isBefore(firstStart, secondStart)) {
            intersectionStart = secondStart;
        } else {
            intersectionStart = firstStart;
        }
        if(isBefore(firstEnd, secondEnd)) {
            intersectionEnd = firstEnd;
        } else {
            intersectionEnd = secondEnd;
        }
        return getDateDiffInHours(intersectionStart, intersectionEnd);
    }

    public long getDurationFromWeekBeginning(Date date) {
        return date.getTime() - getWeekBeginning(date).getTime();
    }

    public Date addDifference(Date date, long difference) {
        return new Date(getWeekBeginning(date).getTime() + difference);
    }

    public Date getStartOfNextDay(Date date) {
        date = asDate(asLocalDateTime(date).plusDays(1));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

    public Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return new Date(cal.getTimeInMillis());
    }

    public Date getWeekBeginning(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return new Date(cal.getTimeInMillis());
    }

    public Date getNextWeekBeggining(Date startDate) {
        return getWeekBeginning(plusDays(startDate, 7));
    }
}
