package com.komandda.web.controller.rest.bean;

/**
 * Created by Yevhen on 18.02.2017.
 */
public class ChangePriorityBean {
    private int oldPriority;
    private int newPriority;

    public int getOldPriority() {
        return oldPriority;
    }

    public void setOldPriority(int oldPriority) {
        this.oldPriority = oldPriority;
    }

    public int getNewPriority() {
        return newPriority;
    }

    public void setNewPriority(int newPriority) {
        this.newPriority = newPriority;
    }
}
