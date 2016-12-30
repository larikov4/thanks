package com.komandda.service.email.template.event.series;

import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.service.email.template.event.EventUpdatingEmailTemplate;


public class SeriesEventUpdatingEmailTemplate extends EventUpdatingEmailTemplate {

    public SeriesEventUpdatingEmailTemplate(Event event, User author, Event prevEvent) {
        super(event, author, prevEvent);
    }

    @Override
    public String resolveBody() {
        return "Series event was changed by " + getAuthor().getName() + "." + LINE_SEPARATOR +
                generateDiffBetweenPreviousAndCurrentEvent();
    }

    private String generateDiffBetweenPreviousAndCurrentEvent() {
        return generateTitleDiff()
                + generateDescriptionDiff()
                + generateDayOfWeekDiff()
                + generateStartDateDiff()
                + generateEndDateDiff()
                + generateLocationDiff()
                + generateUsersDiff()
                + generateEquipmentDiff();
    }

    @Override
    protected String generateStartDateDiff(){
        if (!prevEvent.getStart().equals(getEvent().getStart())) {
            return "Event start was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    formatTime(prevEvent.getStart()) + LINE_SEPARATOR +
                    "\tto " +
                    formatTime(getEvent().getStart()) + LINE_SEPARATOR;
        }
        return "";
    }

    @Override
    protected String generateEndDateDiff(){
        if (!prevEvent.getEnd().equals(getEvent().getEnd())) {
            return "Event end was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    formatTime(prevEvent.getEnd()) + LINE_SEPARATOR +
                    "\tto " +
                    formatTime(getEvent().getEnd()) + LINE_SEPARATOR;
        }
        return "";
    }

    private String generateDayOfWeekDiff(){
        String prevEventStartWeekDay = DATE_HELPER.asLocalDateTime(prevEvent.getStart()).getDayOfWeek().name();
        String eventStartWeekDay = DATE_HELPER.asLocalDateTime(getEvent().getStart()).getDayOfWeek().name();
        if (!prevEventStartWeekDay.equals(eventStartWeekDay)) {
            return "Day of a week was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    prevEventStartWeekDay + LINE_SEPARATOR +
                    "\tto " +
                    eventStartWeekDay + LINE_SEPARATOR;
        }
        return "";
    }
}