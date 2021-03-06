package com.komandda.service;

import com.komandda.entity.User;
import com.komandda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by yevhen on 28.06.16.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private FreeEntitiesService freeEntitiesService;

    public List<User> findAll() {
        return hidePassword(repository.findAll());
    }

    public User findOne(String id) {
        return repository.findOne(id);
    }

    public User findOne(String username, String password){
        return hidePassword(repository.findByUsernameAndPassword(username, password));
    }

    public User insert(User equipment){
        return hidePassword(repository.insert(equipment));
    }

    public User save(User equipment){
        return hidePassword(repository.save(equipment));
    }

    public void delete(User user){
        repository.delete(user);
    }

    public List<User> getFree(Date start, Date end, String id){
        return hidePassword(freeEntitiesService.getFreeUsers(start, end, id));
    }

    private List<User> hidePassword(List<User> users) {
        for(User user : users) {
            hidePassword(user);
        }
        return users;
    }

    private User hidePassword(User user) {
        if(user==null){
            return null;
        }
        String starsInsteadOfPassword = user.getPassword().replaceAll(".", "*");
        user.setPassword(starsInsteadOfPassword);
        return user;
    }
}
