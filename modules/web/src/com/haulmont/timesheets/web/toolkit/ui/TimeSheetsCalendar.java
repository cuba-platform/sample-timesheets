/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.toolkit.ui;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.WeeklyReportEntry;
import com.haulmont.timesheets.global.WorkConfigBean;
import com.haulmont.timesheets.web.toolkit.ui.client.calendar.TimeSheetsCalendarState;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeSheetsCalendar extends Calendar {

    public TimeSheetsCalendar(CalendarEventProvider eventProvider) {
        super(eventProvider);

        getState().weekends = getWeekends();
    }

    @Override
    public TimeSheetsCalendarState getState() {
        return (TimeSheetsCalendarState) super.getState();
    }

    protected List<Integer> getWeekends() {
        WorkConfigBean workConfigBean = AppBeans.get(WorkConfigBean.NAME);
        UserSession userSession = AppBeans.get(UserSession.class);
        WeeklyReportEntry.DayOfWeek[] weekends = workConfigBean.getWeekends();
        List<Integer> dayNumbers = new ArrayList<>(weekends.length);
        for (WeeklyReportEntry.DayOfWeek day : weekends) {
            int number = WeeklyReportEntry.DayOfWeek.convertToDayOfWeekNumber(day, userSession.getLocale());
            if (number > 0) {
                dayNumbers.add(number);
            }
        }
        return dayNumbers;
    }
}
