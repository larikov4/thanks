package com.komandda.service.email.template;

import com.komandda.entity.Equipment;
import com.komandda.entity.Event;
import com.komandda.entity.Location;
import com.komandda.entity.User;

import static org.springframework.util.StringUtils.isEmpty;


public class EventUpdatingEmailTemplate extends EmailTemplate {

    public static final String EMPTY_VALUE = "nothing";
    private Event prevEvent;

    public EventUpdatingEmailTemplate(Event event, User author, Event prevEvent) {
        super(event, author);
        this.prevEvent = prevEvent;
    }

    @Override
    public String resolveSubject() {
        return SUBJECT_PREFIX + getEvent().getTitle() + " (updated by " + getAuthor().getName() + ")";
    }

    @Override
    public String resolveBody() {
        return "Event was changed by " + getAuthor().getName() + LINE_SEPARATOR +
                generateDiffBetweenPreviousAndCurrentEvent();
    }

    private String generateDiffBetweenPreviousAndCurrentEvent() {
        return generateTitleDiff()
                + generateDescriptionDiff()
                + generateStartDateDiff()
                + generateEndDateDiff()
                + generateLocationDiff()
                + generateUsersDiff()
                + generateEquipmentDiff();
    }
    
    private String generateTitleDiff(){
        if (!prevEvent.getTitle().equals(getEvent().getTitle())) {
            return "Title changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    prevEvent.getTitle() + LINE_SEPARATOR +
                    "\tto " +
                    getEvent().getTitle() + LINE_SEPARATOR;
        }
        return "";
    }

    private String generateDescriptionDiff(){
        String prevEventDescription = isEmpty(prevEvent.getDescription()) ? EMPTY_VALUE : prevEvent.getDescription();
        String currentEventDescription = isEmpty(getEvent().getDescription()) ? EMPTY_VALUE : getEvent().getDescription();
        if (!prevEventDescription.equals(currentEventDescription)) {
            return "Description changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    prevEventDescription + LINE_SEPARATOR +
                    "\tto " +
                    currentEventDescription + LINE_SEPARATOR;
        }
        return "";
    }

    private String generateStartDateDiff(){
        if (!prevEvent.getStart().equals(getEvent().getStart())) {
            return "Event start changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    format(prevEvent.getStart()) + LINE_SEPARATOR +
                    "\tto " +
                    format(getEvent().getStart()) + LINE_SEPARATOR;
        }
        return "";
    }

    private String generateEndDateDiff(){
        if (!prevEvent.getEnd().equals(getEvent().getEnd())) {
            return "Event end changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    format(prevEvent.getEnd()) + LINE_SEPARATOR +
                    "\tto " +
                    format(getEvent().getEnd()) + LINE_SEPARATOR;
        }
        return "";
    }

    private String generateLocationDiff(){
        Location prevLocation = prevEvent.getLocation();
        Location currentLocation = getEvent().getLocation();
        if ((prevLocation != null && !prevLocation.equals(currentLocation))
                || (currentLocation != null && !currentLocation.equals(prevLocation))) {
            String prevLocationName = prevLocation == null ? EMPTY_VALUE : prevLocation.getName();
            String currentLocationName = currentLocation == null ? EMPTY_VALUE : currentLocation.getName();
            return "Event location changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    prevLocationName + LINE_SEPARATOR +
                    "\tto " +
                    currentLocationName + LINE_SEPARATOR;
        }
        return "";
    }

    private String generateUsersDiff(){
        StringBuilder diff = new StringBuilder();
        if (!prevEvent.getUsers().equals(getEvent().getUsers())) {
            for(User user : prevEvent.getUsers()) {
                if(!getEvent().getUsers().contains(user)) {
                    diff.append(user.getName())
                            .append(" has been removed")
                            .append(LINE_SEPARATOR);
                }
            }
            for(User user : getEvent().getUsers()) {
                if(!prevEvent.getUsers().contains(user)) {
                    diff.append(user.getName())
                            .append(" has been added")
                            .append(LINE_SEPARATOR);
                }
            }
        }
        return diff.toString();
    }

    private String generateEquipmentDiff(){
        StringBuilder diff = new StringBuilder();
        if (!prevEvent.getEquipment().equals(getEvent().getEquipment())) {
            for (Equipment equipment : prevEvent.getEquipment()) {
                if (!getEvent().getEquipment().contains(equipment)) {
                    diff.append(equipment.getName())
                            .append(" has been removed")
                            .append(LINE_SEPARATOR);
                }
            }
            for (Equipment equipment : getEvent().getEquipment()) {
                if (!prevEvent.getEquipment().contains(equipment)) {
                    diff.append(equipment.getName())
                            .append(" has been added")
                            .append(LINE_SEPARATOR);
                }
            }
        }
        return diff.toString();
    }
}