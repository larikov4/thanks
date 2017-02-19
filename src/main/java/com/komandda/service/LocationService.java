package com.komandda.service;

import com.komandda.web.controller.rest.bean.ChangePriorityBean;
import com.komandda.entity.Location;
import com.komandda.repository.LocationRepository;
import com.komandda.util.ListUtil;
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
        return repository.findAllByOrderByPriority();
    }

    public List<Location> findByDeletedFalse() {
        return repository.findByDeletedFalseOrderByPriorityAsc();
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

    public List<Location> changePriority(ChangePriorityBean priorityBean) {
        List<Location> locations = repository.findAllByOrderByPriority();
        ListUtil.moveElement(locations, priorityBean.getOldPriority(), priorityBean.getNewPriority());
        for(int i=0;i<locations.size();i++) {
            locations.get(i).setPriority(i);
        }
        repository.save(locations);
        return locations;
    }
}
