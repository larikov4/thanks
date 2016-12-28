package com.komandda.service;

import com.komandda.entity.Location;
import com.komandda.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by yevhen on 28.06.16.
 */
@Service
public class LocationService {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private FreeEntitiesService freeEntitiesService;

    @Autowired
    private FreeSeriesEntitiesService freeSeriesEntitiesService;

    public List<Location> findAll() {
        return repository.findAll();
    }

    public List<Location> findByDeletedFalse() {
        return repository.findByDeletedFalse();
    }

    public Location findOne(String id) {
        return repository.findOne(id);
    }

    public Location insert(Location equipment){
        return repository.insert(equipment);
    }

    public Location save(Location equipment){
        return repository.save(equipment);
    }

    public void delete(Location location){
        location.setDeleted(true);
        repository.save(location);
    }

    public List<Location> getFree(Date start, Date end, String id){
        return freeEntitiesService.getFreeLocations(start, end, id);
    }

    public List<Location> getSeriesFree(Date start, Date end, String seriesId){
        return freeSeriesEntitiesService.getFreeLocations(start, end, seriesId);
    }
}
