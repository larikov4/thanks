package com.komandda.repository;

import com.komandda.entity.EventChangeItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by yevhen on 28.06.16.
 */
public interface EventChangelogRepository extends MongoRepository<EventChangeItem, String> {

    List<EventChangeItem> findByDiffItemIdOrderByDateAsc(String id);

    List<EventChangeItem> deleteByDiffItemId(String id);
}
