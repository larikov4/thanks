package com.komandda.repository;

import com.komandda.entity.Location;
import com.komandda.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Yevhen_Larikov
 */
public interface LocationRepository extends MongoRepository<Location, String> {
    List<Location> findAllByOrderByPriority();

    List<Location> findByDeletedFalseOrderByPriorityAsc();
}
