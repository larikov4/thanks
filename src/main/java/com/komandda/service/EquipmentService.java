package com.komandda.service;

import com.komandda.entity.Equipment;
import com.komandda.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by yevhen on 28.06.16.
 */
@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository repository;

    @Autowired
    private FreeEntitiesService freeEntitiesService;

    public List<Equipment> findAll() {
        return repository.findAll();
    }

    public Equipment findOne(String id) {
        return repository.findOne(id);
    }

    public Equipment insert(Equipment equipment){
        return repository.insert(equipment);
    }

    public Equipment save(Equipment equipment){
        return repository.save(equipment);
    }

    public void delete(Equipment equipment){
        repository.delete(equipment);
    }

    public List<Equipment> getFree(Date start, Date end, String id){
        return freeEntitiesService.getFreeEquipment(start, end, id);
    }
}
