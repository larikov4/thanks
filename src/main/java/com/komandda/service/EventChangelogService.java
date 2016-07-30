package com.komandda.service;

import com.komandda.entity.EventChangeItem;
import com.komandda.entity.Event;
import com.komandda.repository.EventChangelogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yevhen on 28.06.16.
 */
@Service
public class EventChangelogService {

    @Autowired
    private EventChangelogRepository repository;

    @Autowired
    private EventService eventService;

    public EventChangeItem insert(EventChangeItem eventChangeItem) {
        String diffItemId = eventChangeItem.getDiffItemId();
        Event prevEvent = eventService.findOne(diffItemId);
        if (!findByDiffItemId(diffItemId).isEmpty()) {
            Event diffEvent = generateDiff(eventChangeItem.getDiff(), prevEvent);
            eventChangeItem.setDiff(diffEvent);
        }
        if (eventChangeItem.getDiff() != null){
            return repository.insert(eventChangeItem);
        }
        return null;
    }

    private Event generateDiff(Event currentEvent, Event prevEvent) {
        Event diff = new Event();
        boolean wasModified = false;
        if (!prevEvent.getTitle().equals(currentEvent.getTitle())) {
            diff.setTitle(currentEvent.getTitle());
            wasModified = true;
        }
        if (!prevEvent.getStart().equals(currentEvent.getStart())) {
            diff.setStart(currentEvent.getStart());
            wasModified = true;
        }
        if (!prevEvent.getEnd().equals(currentEvent.getEnd())) {
            diff.setEnd(currentEvent.getEnd());
            wasModified = true;
        }
        if (!prevEvent.getLocation().equals(currentEvent.getLocation())) {
            diff.setLocation(currentEvent.getLocation());
            wasModified = true;
        }
        if (!prevEvent.getUsers().equals(currentEvent.getUsers())) {
            diff.setUsers(currentEvent.getUsers());
            wasModified = true;
        }
        if (!prevEvent.getEquipment().equals(currentEvent.getEquipment())) {
            diff.setEquipment(currentEvent.getEquipment());
            wasModified = true;
        }
        if (wasModified) {
            return diff;
        }
        return null;
    }

    public List<EventChangeItem> findByDiffItemId(String id) {
        return repository.findByDiffItemIdOrderByDateAsc(id);
    }

    public List<EventChangeItem> deleteByDiffItemId(String id) {
        return repository.deleteByDiffItemId(id);
    }
}
