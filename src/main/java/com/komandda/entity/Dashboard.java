package com.komandda.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yevhen on 21.01.2017.
 */
public class Dashboard {
    private Dataset locationWeekDataset;
    private Dataset eventWeekDataset;
    private List<Dataset> projectEventsDatasets = new ArrayList<>();
    private List<Dataset> operatorEventsDatasets = new ArrayList<>();
    private List<Dataset> freeOperatorSlotEventsDatasets = new ArrayList<>();

    public List<Dataset> getProjectEventsDatasets() {
        return projectEventsDatasets;
    }

    public void addProjectEventsDataset(Dataset dataset) {
        projectEventsDatasets.add(dataset);
    }

    public List<Dataset> getOperatorEventsDatasets() {
        return operatorEventsDatasets;
    }

    public void addOperatorEventsDataset(Dataset dataset) {
        operatorEventsDatasets.add(dataset);
    }

    public List<Dataset> getFreeOperatorSlotEventsDatasets() {
        return freeOperatorSlotEventsDatasets;
    }

    public void addFreeOperatorSlotEventsDataset(Dataset dataset) {
        freeOperatorSlotEventsDatasets.add(dataset);
    }

    public void setOperatorEventsDatasets(List<Dataset> operatorEventsDatasets) {
        this.operatorEventsDatasets = operatorEventsDatasets;
    }

    public Dataset getLocationWeekDataset() {
        return locationWeekDataset;
    }

    public void setLocationWeekDataset(Dataset locationWeekDataset) {
        this.locationWeekDataset = locationWeekDataset;
    }

    public Dataset getEventWeekDataset() {
        return eventWeekDataset;
    }

    public void setEventWeekDataset(Dataset eventWeekDataset) {
        this.eventWeekDataset = eventWeekDataset;
    }

    public static class Dataset {
        private String title;
        private List<String> keys;
        private List<Double> values;

        public Dataset(String title, List<String> keys, List<Double> values) {
            this.title = title;
            this.keys = keys;
            this.values = values;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getKeys() {
            return keys;
        }

        public List<Double> getValues() {
            return values;
        }
    }
}
