/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Holiday;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.service.ProjectsService;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeSheetsCalendarEventProvider extends BasicEventProvider {

    protected Set<Holiday> holidays;
    protected DateFormat format;

    public TimeSheetsCalendarEventProvider(User user) {
        ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForUser(user, "timeEntry-full");

        for (TimeEntry entry : timeEntries) {
            eventList.add(new TimeEntryCalendarEventAdapter(entry));
        }

        holidays = new HashSet<>(projectsService.getHolidays());
        for (Holiday holiday : holidays) {
            eventList.add(new HolidayCalendarEventAdapter(holiday));
        }
    }

    public Set<String> getHolidays(Date startDate, Date endDate) {
        if (holidays == null || holidays.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> stringHolidays = new HashSet<>();

        for (Holiday holiday : holidays) {
            stringHolidays.addAll(holidayAsSeparateStrings(holiday, startDate, endDate));
        }

        return stringHolidays;
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
            holidays.remove(holiday);
            holidays.add(holiday);
            fireEventSetChange();
        }
    }

    public DateFormat getFormat() {
        if (format == null) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        }
        return format;
    }

    public void setFormat(DateFormat format) {
        this.format = format;
    }

    protected Set<String> holidayAsSeparateStrings(Holiday holiday, Date startDate, Date endDate) {
        Date start;
        Date end;
        if (holiday.getStartDate().getTime() >= startDate.getTime()) {
            start = holiday.getStartDate();
        } else {
            start = startDate;
        }
        if (holiday.getEndDate().getTime() <= endDate.getTime()) {
            end = holiday.getEndDate();
        } else {
            end = endDate;
        }

        if (start.equals(startDate) && end.equals(endDate)) {
            return Collections.emptySet();
        } else {
            Set<String> stringDates = new HashSet<>();

            while (start.getTime() <= end.getTime()) {
                stringDates.add(getFormat().format(start));
                start = DateUtils.addDays(start, 1);
            }

            return stringDates;
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
}
