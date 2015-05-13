/*
 * Copyright (c) 2015 com.haulmont.timesheets.web
 */
package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.TimeEntryStatus;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Layout;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import org.apache.commons.lang.time.DateUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

/**
 * @author gorelov
 */
public class CalendarScreen extends AbstractWindow {

    @Inject
    protected BoxLayout calBox;
    @Inject
    protected LinkButton monthLabel;
    @Inject
    protected UserSession userSession;

    protected Calendar calendar;
    protected Date firstDayOfMonth;

    @Override
    public void init(Map<String, Object> params) {

        firstDayOfMonth = getFirstDayOfMonth();
        updateMonthCaption();

        calendar = new Calendar(new TimeEntryEventProvider(userSession.getUser()));
        calendar.setWidth("100%");
        calendar.setHeight("90%");
        calendar.setTimeFormat(Calendar.TimeFormat.Format24H);
        calendar.setDropHandler(null);
        calendar.setHandler((CalendarComponentEvents.EventMoveHandler) null);   // Do not work
        calendar.setHandler((CalendarComponentEvents.WeekClickHandler) null);
        calendar.setHandler((CalendarComponentEvents.DateClickHandler) null);
        calendar.setHandler((CalendarComponentEvents.EventResizeHandler) null);
        calendar.setHandler(new CalendarComponentEvents.EventClickHandler() {
            @Override
            public void eventClick(CalendarComponentEvents.EventClick event) {
                CalendarEventAdapter eventAdapter = (CalendarEventAdapter) event.getCalendarEvent();
                TimeEntry timeEntry = eventAdapter.getTimeEntry();
                if (!TimeEntryStatus.APPROVED.equals(timeEntry.getStatus())) {
                    editTimeEntry(timeEntry);
                }
            }
        });

        updateCalendarRange();

        Layout layout = WebComponentsHelper.unwrap(calBox);
        layout.addComponent(calendar);
    }

    public void updateCalendarRange() {
        calendar.setStartDate(firstDayOfMonth);
        calendar.setEndDate(getEndDate());
    }

    public void moveNextMonth() {
        firstDayOfMonth = DateUtils.addMonths(firstDayOfMonth, 1);
        updateCalendarRange();
        updateMonthCaption();
    }

    public void movePreviousMonth() {
        firstDayOfMonth = DateUtils.addMonths(firstDayOfMonth, -1);
        updateCalendarRange();
        updateMonthCaption();
    }

    public void addTimeEntry() {
        editTimeEntry(new TimeEntry());
        calendar.setEventProvider(new TimeEntryEventProvider(userSession.getUser()));
    }

    protected void editTimeEntry(TimeEntry timeEntry) {
        TimeEntryEdit editor = openEditor("ts$TimeEntry.edit", timeEntry, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (COMMIT_ACTION_ID.equals(actionId)) {
//                    updateCalendarRange();
                }
            }
        });
    }

    protected void updateMonthCaption() {
        monthLabel.setCaption(String.format("%s %s", getMonthName(firstDayOfMonth), getYear(firstDayOfMonth)));
    }

    protected String getMonthName(Date firstDayOfMonth) {
        return DateUtils.toCalendar(firstDayOfMonth).getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, userSession.getLocale());
    }


    protected int getYear(Date firstDayOfMonth) {
        return DateUtils.toCalendar(firstDayOfMonth).get(java.util.Calendar.YEAR);
    }

    protected Date getFirstDayOfMonth() {
//        return DateUtils.setDays(new Date(), 1);
        java.util.Calendar calendar = getCalendarTithoutTime();
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    protected Date getEndDate() {
        java.util.Calendar calendar = DateUtils.toCalendar(firstDayOfMonth);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    protected java.util.Calendar getCalendarTithoutTime() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar;
    }
}