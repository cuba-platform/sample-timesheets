/*
 * Copyright (c) 2015 com.haulmont.timesheets.entity
 */
package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nullable;
import java.util.*;

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
    protected String mondayTime;

    @MetaProperty
    protected String tuesdayTime;

    @MetaProperty
    protected String wednesdayTime;

    @MetaProperty
    protected String thursdayTime;

    @MetaProperty
    protected String fridayTime;

    @MetaProperty
    protected String saturdayTime;

    @MetaProperty
    protected String sundayTime;

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
        hours += minutes / 60;
        minutes %= 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    public void setMondayTime(String mondayTime) {
        this.mondayTime = mondayTime;
    }

    public String getMondayTime() {
        return mondayTime;
    }

    public void setTuesdayTime(String tuesdayTime) {
        this.tuesdayTime = tuesdayTime;
    }

    public String getTuesdayTime() {
        return tuesdayTime;
    }

    public void setWednesdayTime(String wednesdayTime) {
        this.wednesdayTime = wednesdayTime;
    }

    public String getWednesdayTime() {
        return wednesdayTime;
    }

    public void setThursdayTime(String thursdayTime) {
        this.thursdayTime = thursdayTime;
    }

    public String getThursdayTime() {
        return thursdayTime;
    }

    public void setFridayTime(String fridayTime) {
        this.fridayTime = fridayTime;
    }

    public String getFridayTime() {
        return fridayTime;
    }

    public void setSaturdayTime(String saturdayTime) {
        this.saturdayTime = saturdayTime;
    }

    public String getSaturdayTime() {
        return saturdayTime;
    }

    public void setSundayTime(String sundayTime) {
        this.sundayTime = sundayTime;
    }

    public String getSundayTime() {
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

    public String getDayOfWeekTime(DayOfWeek day) {
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

    public void changeDayOfWeekTimeEntry(DayOfWeek day, @Nullable TimeEntry timeEntry) {
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

    public void updateTimeEntry(TimeEntry timeEntry) {
        int day = DateUtils.toCalendar(timeEntry.getDate()).get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
                setMonday(timeEntry);
                break;
            case Calendar.TUESDAY:
                setTuesday(timeEntry);
                break;
            case Calendar.WEDNESDAY:
                setWednesday(timeEntry);
                break;
            case Calendar.THURSDAY:
                setThursday(timeEntry);
                break;
            case Calendar.FRIDAY:
                setFriday(timeEntry);
                break;
            case Calendar.SATURDAY:
                setSaturday(timeEntry);
                break;
            case Calendar.SUNDAY:
                setSunday(timeEntry);
                break;
        }
    }

    public boolean hasTimeEntries() {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (getDayOfWeekTimeEntry(day) != null) {
                return true;
            }
        }
        return false;
    }

    public List<TimeEntry> getExistTimeEntries() {
        List<TimeEntry> timeEntries = null;
        for (DayOfWeek day : DayOfWeek.values()) {
            TimeEntry current = getDayOfWeekTimeEntry(day);
            if (current != null) {
                if (timeEntries == null) {
                    timeEntries = new ArrayList<>();
                }
                timeEntries.add(current);
            }
        }

        return timeEntries != null ? timeEntries : Collections.<TimeEntry>emptyList();
    }
}