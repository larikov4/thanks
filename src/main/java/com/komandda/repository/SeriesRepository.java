package com.komandda.repository;

import com.komandda.entity.Series;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface SeriesRepository extends MongoRepository<Series, String> {
}
