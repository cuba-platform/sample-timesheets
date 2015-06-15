
package com.haulmont.timesheets.web.calendar;

import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author gorelov
 */
public class TimeEntryCalendarEventAdapter extends BasicEvent {

    protected TimeEntry timeEntry;

    public TimeEntryCalendarEventAdapter(TimeEntry timeEntry) {
        this.setTimeEntry(timeEntry);
    }

    public TimeEntry getTimeEntry() {
        return timeEntry;
    }

    public void setTimeEntry(TimeEntry timeEntry) {
        this.timeEntry = timeEntry;
        super.setStyleName(ComponentsHelper.getTimeEntryStatusStyle(this.timeEntry));
    }

    @Override
    public String getCaption() {
        Task task = timeEntry.getTask();
        return String.format("%s [%s] %s", timeEntry.getSpentTime().getFormattedCaption(), task.getProject().getCode(), task.getName());
    }

    @Override
    public String getDescription() {
        return timeEntry.getDescription();
    }

    @Override
    public Date getStart() {
        return timeEntry.getDate();
    }

    @Override
    public Date getEnd() {
        HoursAndMinutes hoursAndMinutes = HoursAndMinutes.fromTimeEntry(timeEntry);
        Calendar dateCal = DateUtils.toCalendar(getStart());
        dateCal.set(Calendar.HOUR_OF_DAY, hoursAndMinutes.getHours());
        dateCal.set(Calendar.MINUTE, hoursAndMinutes.getMinutes());
        return dateCal.getTime();
    }

    @Override
    public boolean isAllDay() {
        return false;
    }
}
