
package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Scripting;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author gorelov
 */
public class TimeEntryCalendarEventAdapter extends BasicEvent {
    private static final String DEFAULT_TIME_ENTRY_NAME_PATTERN = "$entry.spentTime [$project.code] $task.name";
    protected Scripting scripting = AppBeans.get(Scripting.NAME);

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
        HashMap<String, Object> context = new HashMap<>();
        context.put("entry", timeEntry);
        context.put("task", timeEntry.getTask());
        context.put("project", timeEntry.getTask().getProject());
        context.put("activity", timeEntry.getActivityType());

        String timeEntryNamePattern = timeEntry.getTask().getProject().getTimeEntryNamePattern();
        if (StringUtils.isBlank(timeEntryNamePattern)) {
            timeEntryNamePattern = DEFAULT_TIME_ENTRY_NAME_PATTERN;
        }

        try {
            return scripting.evaluateGroovy("return \"" + timeEntryNamePattern + "\".toString()", context);
        } catch (Exception e) {
            if (!DEFAULT_TIME_ENTRY_NAME_PATTERN.equals(timeEntryNamePattern)) {
                return scripting.evaluateGroovy("return \"" + DEFAULT_TIME_ENTRY_NAME_PATTERN + "\".toString()", context);
            } else {
                throw new RuntimeException("Groovy scripting fails", e);
            }
        }
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
