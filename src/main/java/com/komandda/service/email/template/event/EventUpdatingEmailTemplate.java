package com.komandda.service.email.template.event;

import com.komandda.entity.*;
import com.komandda.service.email.template.EmailTemplate;

import static org.springframework.util.StringUtils.isEmpty;


public class EventUpdatingEmailTemplate extends EmailTemplate {

    private static final String EMPTY_VALUE = "nothing";
    protected Event prevEvent;

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
                + generateProjectDiff()
                + generateStartDateDiff()
                + generateEndDateDiff()
                + generateLocationDiff()
                + generateUsersDiff()
                + generateEquipmentDiff();
    }

    protected String generateTitleDiff(){
        if (!prevEvent.getTitle().equals(getEvent().getTitle())) {
            return "Title was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    prevEvent.getTitle() + LINE_SEPARATOR +
                    "\tto " +
                    getEvent().getTitle() + LINE_SEPARATOR;
        }
        return "";
    }

    protected String generateDescriptionDiff(){
        String prevEventDescription = valueOrEmptyStab(prevEvent.getDescription());
        String currentEventDescription = valueOrEmptyStab(getEvent().getDescription());
        if (!prevEventDescription.equals(currentEventDescription)) {
            return "Description was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    prevEventDescription + LINE_SEPARATOR +
                    "\tto " +
                    currentEventDescription + LINE_SEPARATOR;
        }
        return "";
    }

    private String valueOrEmptyStab(String value) {
        return isEmpty(value) ? EMPTY_VALUE : value;
    }

    protected String generateProjectDiff(){
        String prevEventProject = projectNameOrEmptyStab(prevEvent.getProject());
        String currentEventProject = projectNameOrEmptyStab(getEvent().getProject());
        if (!prevEventProject.equals(currentEventProject)) {
            return "Project was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    prevEventProject + LINE_SEPARATOR +
                    "\tto " +
                    currentEventProject + LINE_SEPARATOR;
        }
        return "";
    }

    private String projectNameOrEmptyStab(Project project) {
        if(project != null && !isEmpty(project.getName())){
            return project.getName();
        }
        return EMPTY_VALUE;
    }

    protected String generateStartDateDiff(){
        if (!prevEvent.getStart().equals(getEvent().getStart())) {
            return "Event start was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    format(prevEvent.getStart()) + LINE_SEPARATOR +
                    "\tto " +
                    format(getEvent().getStart()) + LINE_SEPARATOR;
        }
        return "";
    }

    protected String generateEndDateDiff(){
        if (!prevEvent.getEnd().equals(getEvent().getEnd())) {
            return "Event end was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    format(prevEvent.getEnd()) + LINE_SEPARATOR +
                    "\tto " +
                    format(getEvent().getEnd()) + LINE_SEPARATOR;
        }
        return "";
    }

    protected String generateLocationDiff(){
        Location prevLocation = prevEvent.getLocation();
        Location currentLocation = getEvent().getLocation();
        if ((prevLocation != null && !prevLocation.equals(currentLocation))
                || (currentLocation != null && !currentLocation.equals(prevLocation))) {
            String prevLocationName = prevLocation == null ? EMPTY_VALUE : prevLocation.getName();
            String currentLocationName = currentLocation == null ? EMPTY_VALUE : currentLocation.getName();
            return "Event location was changed" + LINE_SEPARATOR +
                    "\tfrom " +
                    prevLocationName + LINE_SEPARATOR +
                    "\tto " +
                    currentLocationName + LINE_SEPARATOR;
        }
        return "";
    }

    protected String generateUsersDiff(){
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

    protected String generateEquipmentDiff(){
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