package com.komandda.repository;

import com.komandda.entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Yevhen_Larikov
 */
public interface EventRepository extends MongoRepository<Event, String> {

    @Query("{'end' : {$gt : ?0}, 'start':{$lt : ?1}}")
    List<Event> findBetweenTwoDatesQuery(Date start, Date end);

    List<Event> findBySeriesId(String seriesId);

}