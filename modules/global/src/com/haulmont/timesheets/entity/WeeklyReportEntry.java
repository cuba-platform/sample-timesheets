/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.timesheets.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.global.StringFormatHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author gorelov
 */
@MetaClass(name = "ts$WeeklyReportEntry")
public class WeeklyReportEntry extends AbstractNotPersistentEntity implements TimeEntryBase {

    private static final long serialVersionUID = -3857876540680481596L;

    @MetaProperty(mandatory = true)
    protected Project project;

    @MetaProperty(mandatory = true)
    protected Task task;

    @MetaProperty
    protected ActivityType activityType;

    @MetaProperty
    protected List<TimeEntry> monday;

    @MetaProperty
    protected List<TimeEntry> tuesday;

    @MetaProperty
    protected List<TimeEntry> wednesday;

    @MetaProperty
    protected List<TimeEntry> thursday;

    @MetaProperty
    protected List<TimeEntry> friday;

    @MetaProperty
    protected List<TimeEntry> saturday;

    @MetaProperty
    protected List<TimeEntry> sunday;

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

    public void setMonday(List<TimeEntry> monday) {
        this.monday = monday;
    }

    public List<TimeEntry> getMonday() {
        return monday;
    }

    public void setTuesday(List<TimeEntry> tuesday) {
        this.tuesday = tuesday;
    }

    public List<TimeEntry> getTuesday() {
        return tuesday;
    }

    public void setWednesday(List<TimeEntry> wednesday) {
        this.wednesday = wednesday;
    }

    public List<TimeEntry> getWednesday() {
        return wednesday;
    }

    public void setThursday(List<TimeEntry> thursday) {
        this.thursday = thursday;
    }

    public List<TimeEntry> getThursday() {
        return thursday;
    }

    public void setFriday(List<TimeEntry> friday) {
        this.friday = friday;
    }

    public List<TimeEntry> getFriday() {
        return friday;
    }

    public void setSaturday(List<TimeEntry> saturday) {
        this.saturday = saturday;
    }

    public List<TimeEntry> getSaturday() {
        return saturday;
    }

    public void setSunday(List<TimeEntry> sunday) {
        this.sunday = sunday;
    }

    public List<TimeEntry> getSunday() {
        return sunday;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    @MetaProperty
    public String getTotal() {
        return StringFormatHelper.getTaskAggregationString(getTotalForTimeEntries(getExistTimeEntries()));
    }

    public HoursAndMinutes getTotalForDay(DayOfWeek day) {
        return getTotalForTimeEntries(getDayOfWeekTimeEntries(day));
    }

    protected HoursAndMinutes getTotalForTimeEntries(List<TimeEntry> timeEntries) {
        HoursAndMinutes total = new HoursAndMinutes();
        if (CollectionUtils.isNotEmpty(timeEntries)) {
            for (TimeEntry timeEntry : timeEntries) {
                Integer timeInMinutes = timeEntry.getTimeInMinutes();
                if (timeInMinutes != null) {
                    total.addMinutes(timeInMinutes);
                }
            }
        }
        return total;
    }

    public List<TimeEntry> getDayOfWeekTimeEntries(DayOfWeek day) {
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
                return Collections.emptyList();
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

    public void changeDayOfWeekTimeEntries(DayOfWeek day, @Nullable List<TimeEntry> timeEntry) {
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

    public void changeDayOfWeekSingleTimeEntry(DayOfWeek day, @Nullable TimeEntry timeEntry) {
        List<TimeEntry> timeEntries = getDayOfWeekTimeEntries(day);
        if (CollectionUtils.isNotEmpty(timeEntries)) {
            timeEntries.remove(timeEntry);
            timeEntries.add(timeEntry);
        }
    }

    public void addTimeEntry(TimeEntry timeEntry) {
        int dayNumber = DateUtils.toCalendar(timeEntry.getDate()).get(Calendar.DAY_OF_WEEK);
        DayOfWeek day = DayOfWeek.fromCalendarDay(dayNumber);
        List<TimeEntry> timeEntries = getDayOfWeekTimeEntries(day);
        if (timeEntries == null) {
            List<TimeEntry> list = new ArrayList<>();
            list.add(timeEntry);
            changeDayOfWeekTimeEntries(day, list);
        } else {
            timeEntries.add(timeEntry);
        }
    }

    public boolean hasTimeEntries() {
        for (DayOfWeek day : DayOfWeek.values()) {
            List<TimeEntry> timeEntries = getDayOfWeekTimeEntries(day);
            if (CollectionUtils.isNotEmpty(timeEntries)) {
                for (TimeEntry timeEntry : timeEntries) {
                    if (PersistenceHelper.isNew(timeEntry)) {
                        return false;
                    }
                }

                return true;
            }
        }
        return false;
    }

    public List<TimeEntry> getExistTimeEntries() {
        List<TimeEntry> timeEntries = null;
        for (DayOfWeek day : DayOfWeek.values()) {
            List<TimeEntry> current = getDayOfWeekTimeEntries(day);
            if (CollectionUtils.isNotEmpty(current)) {
                if (timeEntries == null) {
                    timeEntries = new ArrayList<>();
                }
                timeEntries.addAll(current);
            }
        }

        return timeEntries != null ? timeEntries : Collections.emptyList();
    }

    public boolean hasFilledTime() {
        for (DayOfWeek day : DayOfWeek.values()) {
            String time = getDayOfWeekTime(day);
            if (StringUtils.isNotBlank(time)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Tag> getTags() {
        throw new UnsupportedOperationException();
    }
}