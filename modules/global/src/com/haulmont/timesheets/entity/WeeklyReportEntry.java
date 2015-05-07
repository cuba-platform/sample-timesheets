/*
 * Copyright (c) 2015 com.haulmont.timesheets.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

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
    protected Date mondayTime;

    @MetaProperty
    protected Date tuesdayTime;

    @MetaProperty
    protected Date wednesdayTime;

    @MetaProperty
    protected Date thursdayTime;

    @MetaProperty
    protected Date fridayTime;

    @MetaProperty
    protected Date saturdayTime;

    @MetaProperty
    protected Date sundayTime;

    public enum DayOfWeek {

        MONDAY("monday"),
        TUESDAY("tuesday"),
        WEDNESDAY("wednesday"),
        THURSDAY("thursday"),
        FRIDAY("friday"),
        SATURDAY("saturday"),
        SUNDAY("sunday");

        private String id;

        DayOfWeek(String value) {
            this.id = value;
        }

        public String getId() {
            return id;
        }

        public static DayOfWeek fromId(String id) {
            for (DayOfWeek at : DayOfWeek.values()) {
                if (at.getId().equals(id)) {
                    return at;
                }
            }
            return null;
        }

        public static int getDayOffset(DayOfWeek day) {
            switch (day) {
                case TUESDAY:
                    return 1;
                case WEDNESDAY:
                    return 2;
                case THURSDAY:
                    return 3;
                case FRIDAY:
                    return 4;
                case SATURDAY:
                    return 5;
                case SUNDAY:
                    return 6;
                default:
                    return 0;
            }
        }
    }

    public String getTotal() {
        int hours = 0;
        int minutes = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            TimeEntry timeEntry = getDayOfWeekTimeEntry(day);
            if (timeEntry != null) {
                Date time = timeEntry.getTime();
                if (time != null) {
                    Calendar calendar = DateUtils.toCalendar(time);
                    hours += calendar.get(Calendar.HOUR_OF_DAY);
                    minutes += calendar.get(Calendar.MINUTE);
                }
            }
        }
        return String.format("%02d:%02d", hours, minutes);
    }

    public void setMondayTime(Date mondayTime) {
        this.mondayTime = mondayTime;
    }

    public Date getMondayTime() {
        return mondayTime;
    }

    public void setTuesdayTime(Date tuesdayTime) {
        this.tuesdayTime = tuesdayTime;
    }

    public Date getTuesdayTime() {
        return tuesdayTime;
    }

    public void setWednesdayTime(Date wednesdayTime) {
        this.wednesdayTime = wednesdayTime;
    }

    public Date getWednesdayTime() {
        return wednesdayTime;
    }

    public void setThursdayTime(Date thursdayTime) {
        this.thursdayTime = thursdayTime;
    }

    public Date getThursdayTime() {
        return thursdayTime;
    }

    public void setFridayTime(Date fridayTime) {
        this.fridayTime = fridayTime;
    }

    public Date getFridayTime() {
        return fridayTime;
    }

    public void setSaturdayTime(Date saturdayTime) {
        this.saturdayTime = saturdayTime;
    }

    public Date getSaturdayTime() {
        return saturdayTime;
    }

    public void setSundayTime(Date sundayTime) {
        this.sundayTime = sundayTime;
    }

    public Date getSundayTime() {
        return sundayTime;
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


    public TimeEntry getDayOfWeekTimeEntry(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return getMonday();
            case TUESDAY:
                return getTuesday();
            case WEDNESDAY:
                return getWednesday();
            case THURSDAY:
                return getThursday();
            case FRIDAY:
                return getFriday();
            case SATURDAY:
                return getSaturday();
            case SUNDAY:
                return getSunday();
            default:
                return null;
        }
    }

    public Date getDayOfWeekTime(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return getMondayTime();
            case TUESDAY:
                return getTuesdayTime();
            case WEDNESDAY:
                return getWednesdayTime();
            case THURSDAY:
                return getThursdayTime();
            case FRIDAY:
                return getFridayTime();
            case SATURDAY:
                return getSaturdayTime();
            case SUNDAY:
                return getSundayTime();
            default:
                return null;
        }
    }

    public void changeDayOfWeekTimeEntry(DayOfWeek day, TimeEntry timeEntry) {
        switch (day) {
            case MONDAY:
                setMonday(timeEntry);
                setMondayTime(null);
                break;
            case TUESDAY:
                setTuesday(timeEntry);
                setTuesdayTime(null);
                break;
            case WEDNESDAY:
                setWednesday(timeEntry);
                setWednesdayTime(null);
                break;
            case THURSDAY:
                setThursday(timeEntry);
                setThursdayTime(null);
                break;
            case FRIDAY:
                setFriday(timeEntry);
                setFridayTime(null);
                break;
            case SATURDAY:
                setSaturday(timeEntry);
                setSaturdayTime(null);
                break;
            case SUNDAY:
                setSunday(timeEntry);
                setSundayTime(null);
                break;
        }
    }
}