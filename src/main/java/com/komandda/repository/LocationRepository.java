package com.komandda.repository;

import com.komandda.entity.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * @author Yevhen_Larikov
 */
public interface LocationRepository extends MongoRepository<Location, String> {

}
