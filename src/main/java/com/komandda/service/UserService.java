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

    @Autowired
    private FreeSeriesEntitiesService freeSeriesEntitiesService;

    public List<User> findAll() {
        return hidePassword(repository.findAll());
    }

    public List<User> findByDeletedFalse() {
        return hidePassword(repository.findByDeletedFalse());
    }

    public User findOne(String id) {
        return repository.findOne(id);
    }

    public User findOne(String username, String password){
        return hidePassword(repository.findByUsernameAndPassword(username, password));
    }

    public User insert(User user){
        return hidePassword(repository.insert(user));
    }

    public User save(User user){
        return hidePassword(repository.save(user));
    }

    public void delete(User user){
        user.setDeleted(true);
        repository.save(user);
    }

    public List<User> getFree(Date start, Date end, String id){
        return freeEntitiesService.getFreeUsers(start, end, id);
    }

    public List<User> getSeriesFree(Date start, Date end, String seriesId){
        return freeSeriesEntitiesService.getFreeUsers(start, end, seriesId);
    }

    public List<User> hidePassword(List<User> users) {
        for(User user : users) {
            hidePassword(user);
        }
        return users;
    }

    public User hidePassword(User user) {
        if(user == null || user.getPassword() == null){
            return user;
        }
        String starsInsteadOfPassword = user.getPassword().replaceAll(".", "*");
        user.setPassword(starsInsteadOfPassword);
        return user;
    }
}
