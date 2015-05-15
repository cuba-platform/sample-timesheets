/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.calendar;

import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.vaadin.ui.components.calendar.event.BasicEvent;

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
        return String.format("%s %s", timeEntry.getInstanceName(), timeEntry.getTask().getName());
    }

    @Override
    public String getDescription() {
        return timeEntry.getDescription();
    }

    @Override
    public Date getEnd() {
        return timeEntry.getTime();
    }

    @Override
    public Date getStart() {
        return timeEntry.getDate();
    }
}
