package com.komandda.service;

import com.komandda.web.controller.rest.bean.ChangePriorityBean;
import com.komandda.entity.Equipment;
import com.komandda.repository.EquipmentRepository;
import com.komandda.util.ListUtil;
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

    @Autowired
    private FreeSeriesEntitiesService freeSeriesEntitiesService;

    public List<Equipment> findAll() {
        return repository.findAllByOrderByPriority();
    }

    public List<Equipment> findByDeletedFalse() {
        return repository.findByDeletedFalseOrderByPriorityAsc();
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
        equipment.setDeleted(true);
        repository.save(equipment);
    }

    public List<Equipment> getFree(Date start, Date end, String id){
        return freeEntitiesService.getFreeEquipment(start, end, id);
    }

    public List<Equipment> getSeriesFree(Date start, Date end, String seriesId){
        return freeSeriesEntitiesService.getFreeEquipment(start, end, seriesId);
    }

    public List<Equipment> changePriority(ChangePriorityBean priorityBean) {
        List<Equipment> equipment = repository.findAllByOrderByPriority();
        ListUtil.moveElement(equipment, priorityBean.getOldPriority(), priorityBean.getNewPriority());
        for(int i=0;i<equipment.size();i++) {
            equipment.get(i).setPriority(i);
        }
        repository.save(equipment);
        return equipment;
    }
}
