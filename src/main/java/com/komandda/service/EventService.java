package com.komandda.service;

import com.komandda.entity.EventChangeItem;
import com.komandda.entity.Event;
import com.komandda.entity.User;
import com.komandda.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by yevhen on 28.06.16.
 */
@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private EventChangelogService changelog;

    public List<Event> findAll() {
        return repository.findAll();
    }

    public Event findOne(String id) {
        return repository.findOne(id);
    }

    public Event insert(Event event, User author) {
        Event eventWithId = repository.insert(event);
        changelog.insert(new EventChangeItem(author, new Date(), eventWithId));
        return eventWithId;
    }

    public Event save(Event event, User author) {
        changelog.insert(new EventChangeItem(author, new Date(), event));
        return repository.save(event);
    }

    public void delete(Event event) {
        repository.delete(event);
    }
}
