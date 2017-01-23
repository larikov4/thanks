package com.komandda.service.dashboard.dataset.builder;

import com.komandda.service.helper.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yevhen on 22.01.2017.
 */
public abstract class DatasetBuilder {
    protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM");

    @Autowired
    protected DateHelper dateHelper;

    protected List<String> getWeekDays() {
        List<String> days = new ArrayList<>();
        Date date = dateHelper.getWeekBeginning(new Date());
        for(int i=0;i<7;i++) {
            days.add(DATE_FORMATTER.format(date));
            date = dateHelper.plusDays(date, 1);
        }
        return days;
    }
}
