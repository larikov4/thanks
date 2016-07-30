package com.komandda.repository;

import com.komandda.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

public interface UserRepository extends MongoRepository<User, String> {

	User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

}
