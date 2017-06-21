package com.komandda.service.filter;

import java.util.List;

/**
 * Created by Yevhen on 15.01.2017.
 */
public class EventFilterDto {
    private String locationId;
    private List<String> projects;
    private List<String> names;
    private List<String> equipmentIds;

    public EventFilterDto(String locationId, List<String> projects, List<String> names, List<String> equipmentIds) {
        this.locationId = locationId;
        this.projects = projects;
        this.names = names;
        this.equipmentIds = equipmentIds;
    }

    public String getLocationId() {
        return locationId;
    }

    public List<String> getProjects() {
        return projects;
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getEquipmentIds() {
        return equipmentIds;
    }
}
