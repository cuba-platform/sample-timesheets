/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeEntryEventProvider extends BasicEventProvider {

    public TimeEntryEventProvider(User user) {
        ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForUser(user);

        for (TimeEntry entry : timeEntries) {
            eventList.add(new CalendarEventAdapter(entry));
        }
    }

    public void changeEventTimeEntity(@Nonnull TimeEntry timeEntry) {
        CalendarEventAdapter adapter = findEventWithTimeEntry(timeEntry);
        if (adapter != null) {
            adapter.setTimeEntry(timeEntry);
            fireEventSetChange();
        } else {
            super.addEvent(new CalendarEventAdapter(timeEntry));
        }
    }

    @Nullable
    protected CalendarEventAdapter findEventWithTimeEntry(@Nonnull TimeEntry timeEntry) {
        for (CalendarEvent event : eventList) {
            if (event instanceof CalendarEventAdapter) {
                CalendarEventAdapter adapter = (CalendarEventAdapter) event;
                if (timeEntry.getId().equals(adapter.getTimeEntry().getId())) {
                    return adapter;
                }
            }
        }
        return null;
    }
}
