package com.komandda.service;

import com.komandda.entity.Location;
import com.komandda.entity.Project;
import com.komandda.repository.LocationRepository;
import com.komandda.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by yevhen on 28.06.16.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    public List<Project> findAll() {
        return repository.findAll();
    }

    public List<Project> findByDeletedFalse() {
        return repository.findByDeletedFalse();
    }

    public Project findOne(String id) {
        return repository.findOne(id);
    }

    public Project insert(Project project){
        return repository.insert(project);
    }

    public Project save(Project project){
        return repository.save(project);
    }

    public void delete(Project project){
        project.setDeleted(true);
        repository.save(project);
    }
}
