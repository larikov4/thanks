package com.komandda.util;

import java.util.List;

/**
 * Created by Yevhen on 19.02.2017.
 */
public class ListUtil {

    public static <T> void moveElement(List<T> list, int oldIndex, int newIndex) {
        if(oldIndex < newIndex) {
            moveElementForward(list, oldIndex, newIndex);
        } else {
            moveElementBackward(list, oldIndex, newIndex);
        }
    }

    private static <T> void moveElementForward(List<T> list, int oldIndex, int newIndex) {
        list.add(newIndex + 1, list.get(oldIndex));
        list.remove(oldIndex);
    }

    private static <T> void moveElementBackward(List<T> list, int oldIndex, int newIndex) {
        list.add(newIndex, list.get(oldIndex));
        list.remove(oldIndex + 1);
    }
}
