package com.komandda.service.dashboard.dataset.builder;

import com.komandda.entity.Event;
import com.komandda.service.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by Yevhen on 22.01.2017.
 */
public abstract class DatasetBuilder {
    protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM");

    @Autowired
    protected DateHelper dateHelper;

    protected List<String> getWeekDays() {
        List<String> days = new ArrayList<>();
        Date date = dateHelper.getWeekBeginning(new Date());
        for(int i=0;i<7;i++) {
            days.add(DATE_FORMATTER.format(date));
            date = dateHelper.plusDays(date, 1);
        }
        return days;
    }

    protected Stream<Event> splitEventsWithLongDuration(Event longDurationEvent) {
        if(dateHelper.getDateDiffInHours(longDurationEvent.getStart(), longDurationEvent.getEnd()) <= 24){
            return Collections.singletonList(longDurationEvent).stream();
        }
        List<Event> events = new ArrayList<>();
        Date startDate = longDurationEvent.getStart();
        Date endOfDay = dateHelper.getEndOfDay(longDurationEvent.getStart());
        while(dateHelper.isBefore(endOfDay, longDurationEvent.getEnd())) {
            Event event = new Event();
            event.setStart(startDate);
            event.setEnd(endOfDay);
            events.add(event);
            startDate = dateHelper.getStartOfNextDay(endOfDay);
            endOfDay = dateHelper.getEndOfDay(startDate);
        }
        if(dateHelper.getDateDiffInHours(startDate, longDurationEvent.getEnd()) > 0) {
            Event event = new Event();
            event.setStart(startDate);
            event.setEnd(longDurationEvent.getEnd());
            events.add(event);
        }
        return events.stream();
    }
}
