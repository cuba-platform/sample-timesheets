/*
 * Copyright (c) 2015 com.haulmont.timesheets.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.Date;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import javax.persistence.ManyToOne;

/**
 * @author gorelov
 */
@MetaClass(name = "ts$WeeklyReportEntry")
public class WeeklyReportEntry extends AbstractNotPersistentEntity {
    private static final long serialVersionUID = -3857876540680481596L;

    @MetaProperty(mandatory = true)
    protected Project project;

    @MetaProperty(mandatory = true)
    protected Task task;

    @MetaProperty
    protected TimeEntry monday;

    @MetaProperty
    protected TimeEntry tuesday;

    @MetaProperty
    protected TimeEntry wednesday;

    @MetaProperty
    protected TimeEntry thursday;

    @MetaProperty
    protected TimeEntry friday;

    @MetaProperty
    protected TimeEntry saturday;

    @MetaProperty
    protected TimeEntry sunday;

    @MetaProperty
    protected Date total;

    public Date getTotal() {
        return total;
    }

    public void setTotal(Date total) {
        this.total = total;
    }



    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setMonday(TimeEntry monday) {
        this.monday = monday;
    }

    public TimeEntry getMonday() {
        return monday;
    }

    public void setTuesday(TimeEntry tuesday) {
        this.tuesday = tuesday;
    }

    public TimeEntry getTuesday() {
        return tuesday;
    }

    public void setWednesday(TimeEntry wednesday) {
        this.wednesday = wednesday;
    }

    public TimeEntry getWednesday() {
        return wednesday;
    }

    public void setThursday(TimeEntry thursday) {
        this.thursday = thursday;
    }

    public TimeEntry getThursday() {
        return thursday;
    }

    public void setFriday(TimeEntry friday) {
        this.friday = friday;
    }

    public TimeEntry getFriday() {
        return friday;
    }

    public void setSaturday(TimeEntry saturday) {
        this.saturday = saturday;
    }

    public TimeEntry getSaturday() {
        return saturday;
    }

    public void setSunday(TimeEntry sunday) {
        this.sunday = sunday;
    }

    public TimeEntry getSunday() {
        return sunday;
    }


}