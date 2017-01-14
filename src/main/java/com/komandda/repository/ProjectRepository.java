package com.komandda.repository;

import com.komandda.entity.Location;
import com.komandda.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Yevhen_Larikov
 */
public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByDeletedFalse();
}
