/*
 * Copyright (c) 2015 com.haulmont.timesheets.web
 */
package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.toolkit.ui.CubaVerticalActionsLayout;
import com.haulmont.timesheets.entity.Holiday;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.gui.holiday.HolidayEdit;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.service.ProjectsService;
import com.haulmont.timesheets.web.toolkit.ui.TimeSheetsCalendar;
import com.vaadin.event.Action;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Layout;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.CalendarDateRange;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;
import org.apache.commons.lang.time.DateUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gorelov
 */
public class CalendarScreen extends AbstractWindow {
    @Inject
    protected BoxLayout calBox;
    @Inject
    private BoxLayout summaryBox;
    @Inject
    protected Label monthLabel;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Messages messages;

    protected TimeSheetsCalendar calendar;

    protected Date firstDayOfMonth;

    protected TimeSheetsCalendarEventProvider dataSource;

    @Override
    public void init(Map<String, Object> params) {

        firstDayOfMonth = getFirstDayOfMonth();
        dataSource = new TimeSheetsCalendarEventProvider(userSession.getUser());

        calendar = new TimeSheetsCalendar(dataSource);
        calendar.setWidth("100%");
        calendar.setHeight("89%");
        calendar.setTimeFormat(Calendar.TimeFormat.Format24H);
        calendar.setDropHandler(null);
        calendar.setHandler((CalendarComponentEvents.EventMoveHandler) null);   // Do not work for month view
        calendar.setHandler((CalendarComponentEvents.WeekClickHandler) null);
        calendar.setHandler((CalendarComponentEvents.DateClickHandler) null);
        calendar.setHandler((CalendarComponentEvents.EventResizeHandler) null);
        calendar.setHandler(new CalendarComponentEvents.EventClickHandler() {
            @Override
            public void eventClick(CalendarComponentEvents.EventClick event) {
                if (event.getCalendarEvent() instanceof TimeEntryCalendarEventAdapter) {
                    TimeEntryCalendarEventAdapter eventAdapter = (TimeEntryCalendarEventAdapter) event.getCalendarEvent();
                    editTimeEntry(eventAdapter.getTimeEntry());
                } else if (event.getCalendarEvent() instanceof HolidayCalendarEventAdapter) {
                    HolidayCalendarEventAdapter eventAdapter = (HolidayCalendarEventAdapter) event.getCalendarEvent();
                    editHoliday(eventAdapter.getHoliday());
                }
            }
        });
        calendar.addActionHandler(new CalendarActionHandler());

        updateCalendarRange();
        updateMonthCaption();

        Layout calendarLayout = WebComponentsHelper.unwrap(calBox);
        calendarLayout.addComponent(calendar);

        updateSummary();
        dataSource.addEventSetChangeListener(new CalendarEventProvider.EventSetChangeListener() {
            @Override
            public void eventSetChange(CalendarEventProvider.EventSetChangeEvent changeEvent) {
                updateSummary();
            }
        });
    }

    protected void updateSummary() {
        summaryBox.removeAll();
        CubaVerticalActionsLayout summaryLayout = WebComponentsHelper.unwrap(summaryBox);
        CubaVerticalActionsLayout upperSpacer = new CubaVerticalActionsLayout();
        upperSpacer.setHeight("30px");
        summaryLayout.addComponent(upperSpacer);

        HoursAndMinutes[] summariesByWeeks = calculateSummariesByWeeks();

        for (int i = 1; i < summariesByWeeks.length; i++) {
            com.vaadin.ui.Label label = new com.vaadin.ui.Label();
            label.setContentMode(ContentMode.HTML);
            HoursAndMinutes summaryForTheWeek = summariesByWeeks[i];
            if (summaryForTheWeek == null) {
                summaryForTheWeek = new HoursAndMinutes();
            }
            label.setValue(formatMessage("label.hoursSummary",
                    summaryForTheWeek.getSummaryHours(), summaryForTheWeek.getSummaryMinutes()));
            label.setWidthUndefined();
            summaryLayout.addComponent(label);
            summaryLayout.setExpandRatio(label, 1);
            summaryLayout.setComponentAlignment(label, com.vaadin.ui.Alignment.MIDDLE_CENTER);
        }
    }

    protected HoursAndMinutes[] calculateSummariesByWeeks() {Date start = firstDayOfMonth;
        java.util.Calendar javaCalendar = java.util.Calendar.getInstance();
        javaCalendar.setTime(firstDayOfMonth);
        int countOfWeeksInTheMonth = javaCalendar.getActualMaximum(java.util.Calendar.WEEK_OF_MONTH);
        Date end = getLastDayOfMonth();
        List<CalendarEvent> events = dataSource.getEvents(start, end);
        HoursAndMinutes[] summariesByWeeks = new HoursAndMinutes[countOfWeeksInTheMonth + 1];
        for (CalendarEvent event : events) {
            javaCalendar.setTime(event.getEnd());
            int numberOfWeekForTheEvent = javaCalendar.get(java.util.Calendar.WEEK_OF_MONTH);
            HoursAndMinutes summaryForTheWeek = summariesByWeeks[numberOfWeekForTheEvent];
            if (summaryForTheWeek == null) {
                summaryForTheWeek = new HoursAndMinutes();
            }
            summaryForTheWeek.hours = summaryForTheWeek.hours + javaCalendar.get(java.util.Calendar.HOUR_OF_DAY);
            summaryForTheWeek.minutes = summaryForTheWeek.minutes + javaCalendar.get(java.util.Calendar.MINUTE);

            summariesByWeeks[numberOfWeekForTheEvent] = summaryForTheWeek;
        }
        return summariesByWeeks;
    }

    public void updateCalendarRange() {
        calendar.setStartDate(firstDayOfMonth);
        calendar.setEndDate(getLastDayOfMonth());

        updateSummary();
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
    }

    protected void editTimeEntry(TimeEntry timeEntry) {
        final TimeEntryEdit editor = openEditor("ts$TimeEntry.edit", timeEntry, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (COMMIT_ACTION_ID.equals(actionId)) {
                    dataSource.changeEventTimeEntity(editor.getItem());
                }
            }
        });
    }

    protected void editHoliday(Holiday holiday) {
        final HolidayEdit editor = openEditor("ts$Holiday.edit", holiday, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (COMMIT_ACTION_ID.equals(actionId)) {
                    dataSource.changeEventHoliday(editor.getItem());
                }
            }
        });
    }

    protected void updateMonthCaption() {
        monthLabel.setValue(String.format("%s %s", getMonthName(firstDayOfMonth), getYear(firstDayOfMonth)));
    }

    protected String getMonthName(Date firstDayOfMonth) {
        return DateUtils.toCalendar(firstDayOfMonth).getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, userSession.getLocale());
    }


    protected int getYear(Date firstDayOfMonth) {
        return DateUtils.toCalendar(firstDayOfMonth).get(java.util.Calendar.YEAR);
    }

    protected Date getFirstDayOfMonth() {
//        return DateUtils.setDays(new Date(), 1);
        java.util.Calendar calendar = getCalendarWithoutTime();
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    protected Date getLastDayOfMonth() {
        java.util.Calendar calendar = DateUtils.toCalendar(firstDayOfMonth);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    protected java.util.Calendar getCalendarWithoutTime() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar;
    }

    protected class CalendarActionHandler implements Action.Handler {
        protected Action addEventAction = new Action(messages.getMessage(getClass(), "addTimeEntry"));
        protected Action deleteEventAction = new Action(messages.getMessage(getClass(), "deleteTimeEntry"));
        protected String confirmationMessage;
        protected String confirmationTitle;
        protected ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);

        @Override
        public Action[] getActions(Object target, Object sender) {
            // The target should be a CalendarDateRage for the
            // entire day from midnight to midnight.
            if (!(target instanceof CalendarDateRange))
                return null;
            CalendarDateRange dateRange = (CalendarDateRange) target;

            // The sender is the Calendar object
            if (!(sender instanceof Calendar))
                return null;
            Calendar calendar = (Calendar) sender;

            // List all the events on the requested day
            List<CalendarEvent> events = calendar.getEvents(dateRange.getStart(), dateRange.getEnd());

            if (events.size() == 0)
                return new Action[]{addEventAction};
            else
                return new Action[]{addEventAction, deleteEventAction};
        }

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            // The sender is the Calendar object
            Calendar calendar = (Calendar) sender;

            if (action == addEventAction) {
                // Check that the click was not done on an event
                if (target instanceof Date) {
                    Date date = (Date) target;
                    TimeEntry timeEntry = new TimeEntry();
                    timeEntry.setDate(date);
                    editTimeEntry(timeEntry);
                } else {
                    showNotification(messages.getMessage(getClass(), "cantAddTimeEntry"), NotificationType.WARNING);
                }
            } else if (action == deleteEventAction) {
                // Check if the action was clicked on top of an event
                if (target instanceof HolidayCalendarEventAdapter) {
                    showNotification(messages.getMessage(getClass(), "cantDeleteHoliday"), NotificationType.WARNING);
                } else if (target instanceof TimeEntryCalendarEventAdapter) {
                    TimeEntryCalendarEventAdapter event = (TimeEntryCalendarEventAdapter) target;
                    confirmAndRemove(event);

                } else {
                    showNotification(messages.getMessage(getClass(), "cantDeleteTimeEntry"), NotificationType.WARNING);
                }
            }
        }

        protected void confirmAndRemove(final TimeEntryCalendarEventAdapter event) {
            final String messagesPackage = AppConfig.getMessagesPack();
            getFrame().showOptionDialog(
                    getConfirmationTitle(messagesPackage),
                    getConfirmationMessage(messagesPackage),
                    MessageType.CONFIRMATION,
                    new com.haulmont.cuba.gui.components.Action[]{
                            new DialogAction(DialogAction.Type.OK) {
                                @Override
                                public void actionPerform(Component component) {
                                    calendar.removeEvent(event);
                                    doRemove(event.getTimeEntry());
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL)
                    }
            );
        }

        protected void doRemove(TimeEntry timeEntry) {
            projectsService.removeTimeEntry(timeEntry);
        }

        protected String getConfirmationMessage(String messagesPackage) {
            if (confirmationMessage != null)
                return confirmationMessage;
            else
                return messages.getMessage(messagesPackage, "dialogs.Confirmation.Remove");
        }

        protected String getConfirmationTitle(String messagesPackage) {
            if (confirmationTitle != null)
                return confirmationTitle;
            else
                return messages.getMessage(messagesPackage, "dialogs.Confirmation");
        }
    }

    protected static class HoursAndMinutes {
        protected int hours = 0;
        protected int minutes = 0;

        protected int getSummaryHours() {
            return hours + minutes / 60;
        }

        protected int getSummaryMinutes() {
            return minutes % 60;
        }
    }
}