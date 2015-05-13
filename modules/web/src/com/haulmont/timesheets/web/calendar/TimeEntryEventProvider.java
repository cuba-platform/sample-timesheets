/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeEntryEventProvider implements CalendarEventProvider {

    protected User user;
    protected ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);

    public TimeEntryEventProvider(User user) {
        this.user = user;
    }

    @Override
    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(startDate, endDate, user);
        if (timeEntries.isEmpty()) {
            return Collections.emptyList();
        }

        List<CalendarEvent> events = new ArrayList<>(timeEntries.size());
        for (TimeEntry entry : timeEntries) {
            CalendarEventAdapter adapter = new CalendarEventAdapter(entry);
            adapter.setStyleName(ComponentsHelper.getTimeEntryStatusStyle(entry));
            events.add(adapter);
        }
        return events;
    }
}
