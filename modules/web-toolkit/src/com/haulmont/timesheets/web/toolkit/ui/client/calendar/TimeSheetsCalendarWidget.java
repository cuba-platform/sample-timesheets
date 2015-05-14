/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.web.toolkit.ui.client.calendar;

import com.vaadin.client.ui.VCalendar;
import com.vaadin.client.ui.calendar.schedule.CalendarDay;
import com.vaadin.client.ui.calendar.schedule.SimpleDayCell;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gorelov
 * @version $Id$
 */
public class TimeSheetsCalendarWidget extends VCalendar {

    protected List<Integer> weekends = new ArrayList<Integer>();
    protected List<Date> holidays = new ArrayList<Date>();



    @Override
    protected void setCellStyle(Date today, List<CalendarDay> days, String date, SimpleDayCell cell, int columns, int pos) {
        CalendarDay day = days.get(pos);
        if (isWeekend(day.getDayOfWeek()) || isHoliday(cell.getDate())) {
            cell.addStyleName("holiday");
            cell.setTitle(date);
        }
    }

    protected boolean isWeekend(int dayNumber) {
        for (int numbers : weekends) {
            if (dayNumber == numbers) {
                return true;
            }
        }
        return false;
    }

    protected boolean isHoliday(Date date) {
        return false;
    }

    public void setWeekends(List<Integer> weekends) {
        this.weekends = weekends;
    }

    public void setHolidays(List<Date> holidays) {
        this.holidays = holidays;
    }
}
