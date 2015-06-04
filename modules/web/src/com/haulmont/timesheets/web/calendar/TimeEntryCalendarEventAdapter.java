/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.calendar;

import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeEntryCalendarEventAdapter extends BasicEvent {

    protected TimeEntry timeEntry;

    public TimeEntryCalendarEventAdapter(TimeEntry timeEntry) {
        this.setTimeEntry(timeEntry);
    }

    public TimeEntry getTimeEntry() {
        return timeEntry;
    }

    public void setTimeEntry(TimeEntry timeEntry) {
        this.timeEntry = timeEntry;
        super.setStyleName(ComponentsHelper.getTimeEntryStatusStyle(this.timeEntry));
    }

    @Override
    public String getCaption() {
        Task task = timeEntry.getTask();
        return String.format("%s [%s] %s", timeEntry.getInstanceName(), task.getProject().getCode(), task.getName());
    }

    @Override
    public String getDescription() {
        return timeEntry.getDescription();
    }

    @Override
    public Date getStart() {
        return timeEntry.getDate();
    }

    @Override
    public Date getEnd() {
        Calendar dateCal = DateUtils.toCalendar(getStart());
        Date timeDate = DateUtils.setYears(timeEntry.getTime(), dateCal.get(Calendar.YEAR));
        timeDate = DateUtils.setMonths(timeDate, dateCal.get(Calendar.MONTH));
        timeDate = DateUtils.setDays(timeDate, dateCal.get(Calendar.DAY_OF_MONTH));
        return timeDate;
    }

    @Override
    public boolean isAllDay() {
        return false;
    }
}
