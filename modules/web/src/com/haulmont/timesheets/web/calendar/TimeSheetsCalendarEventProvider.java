
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

package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Holiday;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.ProjectsService;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 */
public class TimeSheetsCalendarEventProvider extends BasicEventProvider {

    protected ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
    protected User user;

    public TimeSheetsCalendarEventProvider(User user) {
        this.user = user;
    }

    public void updateWithRange(Date startDate, Date endDate) {
        eventList.clear();

        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(startDate, endDate, user, null, "timeEntry-full");
        for (TimeEntry entry : timeEntries) {
            eventList.add(new TimeEntryCalendarEventAdapter(entry));
        }

        List<Holiday> holidays = projectsService.getHolidaysForPeriod(startDate, endDate);
        for (Holiday holiday : holidays) {
            eventList.add(new HolidayCalendarEventAdapter(holiday));
        }

        fireEventSetChange();
    }

    public void changeEventTimeEntity(TimeEntry timeEntry) {
        TimeEntryCalendarEventAdapter adapter = findEventWithTimeEntry(timeEntry);
        if (adapter != null) {
            adapter.setTimeEntry(timeEntry);
            fireEventSetChange();
        } else {
            super.addEvent(new TimeEntryCalendarEventAdapter(timeEntry));
        }
    }

    public void changeEventHoliday(Holiday holiday) {
        HolidayCalendarEventAdapter adapter = findEventWithHoliday(holiday);
        if (adapter != null) {
            adapter.setHoliday(holiday);
            fireEventSetChange();
        }
    }

    @Nullable
    protected TimeEntryCalendarEventAdapter findEventWithTimeEntry(TimeEntry timeEntry) {
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

    @Nullable
    protected HolidayCalendarEventAdapter findEventWithHoliday(Holiday holiday) {
        for (CalendarEvent event : eventList) {
            if (event instanceof HolidayCalendarEventAdapter) {
                HolidayCalendarEventAdapter adapter = (HolidayCalendarEventAdapter) event;
                if (holiday.getId().equals(adapter.getHoliday().getId())) {
                    return adapter;
                }
            }
        }
        return null;
    }

    public void addEvents(Collection<CalendarEvent> events) {
        eventList.addAll(events);
        for (CalendarEvent event : events) {
            if (event instanceof BasicEvent) {
                ((BasicEvent) event).addEventChangeListener(this);
            }
        }
        fireEventSetChange();
    }
}
