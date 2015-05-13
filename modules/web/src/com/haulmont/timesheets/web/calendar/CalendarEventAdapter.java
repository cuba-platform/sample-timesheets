/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.calendar;

import com.haulmont.timesheets.entity.TimeEntry;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent.EventChangeNotifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
public class CalendarEventAdapter implements CalendarEvent, EventChangeNotifier {

    protected TimeEntry timeEntry;
    protected String styleName;
    protected transient List<EventChangeListener> listeners = new ArrayList<>();

    public CalendarEventAdapter(TimeEntry timeEntry) {
        this.timeEntry = timeEntry;
    }

    public TimeEntry getTimeEntry() {
        return timeEntry;
    }

    @Override
    public String getCaption() {
        String caption = timeEntry.getTaskName();
        return caption != null ? caption : timeEntry.getTask().getName();
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

    @Override
    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
        fireEventChange();
    }

    @Override
    public boolean isAllDay() {
        return false;
    }

    @Override
    public void addEventChangeListener(EventChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeEventChangeListener(EventChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires an event change event to the listeners. Should be triggered when
     * some property of the event changes.
     */
    protected void fireEventChange() {
        EventChangeEvent event = new EventChangeEvent(this);

        for (EventChangeListener listener : listeners) {
            listener.eventChange(event);
        }
    }
}
