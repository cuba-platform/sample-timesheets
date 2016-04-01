
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

package com.haulmont.timesheets.web.toolkit.ui;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.DayOfWeek;
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.WorkTimeConfigBean;
import com.haulmont.timesheets.web.calendar.TimeSheetsCalendarEventProvider;
import com.haulmont.timesheets.web.toolkit.ui.client.calendar.TimeSheetsCalendarState;
import com.vaadin.shared.ui.calendar.DateConstants;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

import java.util.*;

/**
 * @author gorelov
 */
public class TimeSheetsCalendar extends Calendar {

    protected String moreMsgFormat = "";

    public TimeSheetsCalendar(CalendarEventProvider eventProvider) {
        super(eventProvider);

        getState().weekends = getWeekends();
    }

    @Override
    public TimeSheetsCalendarState getState() {
        return (TimeSheetsCalendarState) super.getState();
    }

    @Override
    public TimeSheetsCalendarEventProvider getEventProvider() {
        return (TimeSheetsCalendarEventProvider) super.getEventProvider();
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        getState().holidays = getHolidays();
        getState().moreMsgFormat = getMoreMsgFormat();
    }

    public String getMoreMsgFormat() {
        return moreMsgFormat;
    }

    public void setMoreMsgFormat(String moreMsgFormat) {
        this.moreMsgFormat = moreMsgFormat;
    }

    protected Set<String> getHolidays() {
        int durationInDays = (int) (((endDate.getTime()) - startDate.getTime()) / DateConstants.DAYINMILLIS);
        durationInDays++;
        if (durationInDays > 60) {
            throw new RuntimeException("Daterange is too big (max 60) = "
                    + durationInDays);
        }

        Date firstDateToShow = expandStartDate(startDate, durationInDays > 7);
        Date lastDateToShow = expandEndDate(endDate, durationInDays > 7);

        return DateTimeUtils.getDatesRangeAsSeparateStrings(firstDateToShow, lastDateToShow, "yyyy-MM-dd");
    }

    protected Set<Integer> getWeekends() {
        WorkTimeConfigBean workTimeConfigBean = AppBeans.get(WorkTimeConfigBean.NAME);
        UserSession userSession = AppBeans.get(UserSession.class);
        List<DayOfWeek> weekends = workTimeConfigBean.getWeekends();
        Set<Integer> dayNumbers = new LinkedHashSet<>(weekends.size());
        for (DayOfWeek day : weekends) {
            int number = day.convertToDayOfWeekNumber(userSession.getLocale());
            if (number > 0) {
                dayNumbers.add(number);
            }
        }
        return dayNumbers;
    }
}
