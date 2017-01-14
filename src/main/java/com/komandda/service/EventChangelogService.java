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
        return repository.insert(eventChangeItem);
    }

    public List<EventChangeItem> findByDiffItemId(String id) {
        return repository.findByDiffItemIdOrderByDateAsc(id);
    }

    public List<EventChangeItem> deleteByDiffItemId(String id) {
        return repository.deleteByDiffItemId(id);
    }
}
