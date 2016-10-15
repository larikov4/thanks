package com.komandda.repository;

import com.komandda.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

	User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

	List<User> findByDeletedFalse();
}
