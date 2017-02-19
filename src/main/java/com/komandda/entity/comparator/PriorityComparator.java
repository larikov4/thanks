package com.komandda.entity.comparator;

import com.komandda.entity.Prioritable;

import java.util.Comparator;

/**
 * Created by Yevhen on 15.02.2017.
 */
public class PriorityComparator implements Comparator<Prioritable> {

    @Override
    public int compare(Prioritable p1, Prioritable p2) {
        return p2.getPriority() - p1.getPriority();
    }
}
