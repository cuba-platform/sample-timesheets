
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

    protected List<Integer> weekends = new ArrayList<Integer>();
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
        for (int numbers : weekends) {
            if (dayNumber == numbers) {
                return true;
            }
        }
        return false;
    }

    protected boolean isHoliday(String date) {
        for (String holiday : holidays) {
            if (holiday.equals(date)) {
                return true;
            }
        }
        return false;
    }

    public void setWeekends(List<Integer> weekends) {
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
