/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.ProjectsService;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeSheetsCalendarEventProvider extends BasicEventProvider {

    public TimeSheetsCalendarEventProvider(User user) {
        ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForUser(user);

        for (TimeEntry entry : timeEntries) {
            eventList.add(new TimeEntryCalendarEventAdapter(entry));
        }
    }

    public void changeEventTimeEntity(@Nonnull TimeEntry timeEntry) {
        TimeEntryCalendarEventAdapter adapter = findEventWithTimeEntry(timeEntry);
        if (adapter != null) {
            adapter.setTimeEntry(timeEntry);
            fireEventSetChange();
        } else {
            super.addEvent(new TimeEntryCalendarEventAdapter(timeEntry));
        }
    }

    @Nullable
    protected TimeEntryCalendarEventAdapter findEventWithTimeEntry(@Nonnull TimeEntry timeEntry) {
        for (CalendarEvent event : eventList) {
            if (event instanceof TimeEntryCalendarEventAdapter) {
                TimeEntryCalendarEventAdapter adapter = (TimeEntryCalendarEventAdapter) event;
                if (timeEntry.getId().equals(adapter.getTimeEntry().getId())) {
                    return adapter;
                }
            }
        }
        return null;
    }
}
