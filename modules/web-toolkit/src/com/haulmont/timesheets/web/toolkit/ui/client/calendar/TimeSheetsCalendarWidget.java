
package com.haulmont.timesheets.web.toolkit.ui.client.calendar;

import com.haulmont.timesheets.web.toolkit.ui.client.calendar.shedule.TimeSheetsSimpleDayCell;
import com.vaadin.client.ui.VCalendar;
import com.vaadin.client.ui.calendar.schedule.CalendarDay;
import com.vaadin.client.ui.calendar.schedule.SimpleDayCell;

import java.util.*;

/**
 * @author gorelov
 */
public class TimeSheetsCalendarWidget extends VCalendar {

    protected Set<Integer> weekends = new HashSet<Integer>();
    protected Set<String> holidays = new HashSet<String>();
    protected String moreMsgFormat = "+ %s";

    @Override
    protected void setCellStyle(Date today, List<CalendarDay> days, String date, SimpleDayCell cell, int columns, int pos) {
        ((TimeSheetsSimpleDayCell)cell).setMoreMsgFormat(moreMsgFormat);
        CalendarDay day = days.get(pos);
        if (isWeekend(day.getDayOfWeek()) || isHoliday(date)) {
            cell.addStyleName("holiday");
            cell.setTitle(date);
        }
    }

    @Override
    protected SimpleDayCell createSimpleDayCell(int y, int x) {
        return new TimeSheetsSimpleDayCell(this, y, x);
    }

    protected boolean isWeekend(int dayNumber) {
        return weekends.contains(dayNumber);
    }

    protected boolean isHoliday(String date) {
        return holidays.contains(date);
    }

    public void setWeekends(Set<Integer> weekends) {
        this.weekends = weekends;
    }

    public void setHolidays(Set<String> holidays) {
        this.holidays = holidays;
    }

    public String getMoreMsgFormat() {
        return moreMsgFormat;
    }

    public void setMoreMsgFormat(String moreMsgFormat) {
        this.moreMsgFormat = moreMsgFormat;
    }
}
