package com.komandda.repository;

import com.komandda.entity.Equipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * @author Yevhen_Larikov
 */
public interface EquipmentRepository extends MongoRepository<Equipment, String> {

}
