package com.komandda.entity.comparator;

import com.komandda.entity.Event;

import java.util.Comparator;

/**
 * Created by Yevhen_Larikov on 10-Nov-16.
 */
public class EventComparatorByDate implements Comparator<Event> {
    @Override
    public int compare(Event event1, Event event2) {
        return (int) (event1.getStart().getTime() - event2.getStart().getTime());
    }
}
