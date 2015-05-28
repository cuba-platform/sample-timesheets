/*
 * Copyright (c) 2015 com.haulmont.timesheets.web
 */
package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.toolkit.ui.CubaVerticalActionsLayout;
import com.haulmont.timesheets.entity.Holiday;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.HoursAndMinutes;
import com.haulmont.timesheets.global.ValidationTools;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.gui.holiday.HolidayEdit;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
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
import java.math.BigDecimal;
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
    protected BoxLayout summaryBox;
    @Inject
    protected Label monthLabel;
    @Inject
    protected DateField monthSelector;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Messages messages;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected ValidationTools validationTools;

    protected TimeSheetsCalendar calendar;
    protected Date firstDayOfMonth;
    protected TimeSheetsCalendarEventProvider dataSource;

    @Override
    public void init(Map<String, Object> params) {
        firstDayOfMonth = DateTimeUtils.getFirstDayOfMonth(timeSource.currentTimestamp());

        dataSource = new TimeSheetsCalendarEventProvider(userSession.getUser());
        dataSource.addEventSetChangeListener(new CalendarEventProvider.EventSetChangeListener() {
            @Override
            public void eventSetChange(CalendarEventProvider.EventSetChangeEvent changeEvent) {
                updateSummaryColumn();
            }
        });

        initCalendar();

        monthSelector.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                firstDayOfMonth = DateTimeUtils.getFirstDayOfMonth((Date) value);
                updateCalendarRange();
            }
        });
    }

    private void initCalendar() {
        calendar = new TimeSheetsCalendar(dataSource);
        calendar.setWidth("100%");
        calendar.setHeight("88%");
        calendar.setTimeFormat(Calendar.TimeFormat.Format24H);
        calendar.setMoreMsgFormat(messages.getMessage(getClass(), "calendar.moreMsgFormat"));
        calendar.setDropHandler(null);
        calendar.setHandler(new CalendarComponentEvents.EventMoveHandler() {
            @Override
            public void eventMove(CalendarComponentEvents.MoveEvent event) {
                TimeEntryCalendarEventAdapter adapter = (TimeEntryCalendarEventAdapter) event.getCalendarEvent();
                adapter.getTimeEntry().setDate(event.getNewStart());
                TimeEntry committed = getDsContext().getDataSupplier().commit(adapter.getTimeEntry());
                adapter.setTimeEntry(committed);
                updateSummaryColumn();
            }
        });
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

        Layout calendarLayout = WebComponentsHelper.unwrap(calBox);
        calendarLayout.addComponent(calendar);

        updateCalendarRange();
        updateSummaryColumn();
    }

    public void setToday() {
        firstDayOfMonth = DateTimeUtils.getFirstDayOfMonth(timeSource.currentTimestamp());
        updateCalendarRange();
    }

    public void showNextMonth() {
        firstDayOfMonth = DateUtils.addMonths(firstDayOfMonth, 1);
        updateCalendarRange();
    }

    public void showPreviousMonth() {
        firstDayOfMonth = DateUtils.addMonths(firstDayOfMonth, -1);
        updateCalendarRange();
    }

    public void addTimeEntry() {
        editTimeEntry(new TimeEntry());
    }

    //todo eude reimplement with CUBA API
    protected void updateSummaryColumn() {
        summaryBox.removeAll();
        CubaVerticalActionsLayout summaryLayout = WebComponentsHelper.unwrap(summaryBox);
        CubaVerticalActionsLayout summaryCaptionVbox = new CubaVerticalActionsLayout();
        summaryCaptionVbox.setHeight("30px");
        summaryCaptionVbox.setWidth("100%");
        com.vaadin.ui.Label summaryCaption = new com.vaadin.ui.Label();
        summaryCaption.setContentMode(ContentMode.HTML);
        summaryCaption.setValue(getMessage("label.summaryCaption"));
        summaryCaption.setWidthUndefined();
        summaryCaptionVbox.addComponent(summaryCaption);
        summaryCaptionVbox.setComponentAlignment(summaryCaption, com.vaadin.ui.Alignment.MIDDLE_CENTER);
        summaryLayout.addComponent(summaryCaptionVbox);

        FactAndPlan[] summariesByWeeks = calculateSummariesByWeeks();

        for (int i = 1; i < summariesByWeeks.length; i++) {
            com.vaadin.ui.Label hourLabel = new com.vaadin.ui.Label();
            hourLabel.setContentMode(ContentMode.HTML);
            FactAndPlan summaryForTheWeek = summariesByWeeks[i];
            if (summaryForTheWeek == null) {
                summaryForTheWeek = new FactAndPlan();
            }
            if (summaryForTheWeek.isMatch()) {
                hourLabel.setValue(formatMessage("label.hoursSummary",
                        summaryForTheWeek.fact.getHours(), summaryForTheWeek.fact.getMinutes()));
            } else {
                hourLabel.setValue(formatMessage("label.hoursSummaryNotMatch",
                        summaryForTheWeek.fact.getHours(), summaryForTheWeek.fact.getMinutes(),
                        summaryForTheWeek.plan.getHours(), summaryForTheWeek.plan.getMinutes()));
                hourLabel.addStyleName("overtime");
            }
            hourLabel.setWidthUndefined();
            summaryLayout.addComponent(hourLabel);
            summaryLayout.setExpandRatio(hourLabel, 1);
            summaryLayout.setComponentAlignment(hourLabel, com.vaadin.ui.Alignment.MIDDLE_CENTER);
        }
    }

    protected FactAndPlan[] calculateSummariesByWeeks() {
        Date start = firstDayOfMonth;
        java.util.Calendar javaCalendar = java.util.Calendar.getInstance(userSession.getLocale());
        javaCalendar.setTime(firstDayOfMonth);
        int countOfWeeksInTheMonth = javaCalendar.getActualMaximum(java.util.Calendar.WEEK_OF_MONTH);
        Date lastDayOfMonth = DateTimeUtils.getLastDayOfMonth(firstDayOfMonth);

        FactAndPlan[] summariesByWeeks = new FactAndPlan[countOfWeeksInTheMonth + 1];
        int weekIndex = 0;
        for (; start.getTime() <= lastDayOfMonth.getTime(); start = DateUtils.addWeeks(start, 1)) {
            Date firstDayOfWeek = DateTimeUtils.getFirstDayOfWeek(start);
            Date lastDayOfWeek = DateTimeUtils.getLastDayOfWeek(start);

            if (firstDayOfWeek.getTime() < firstDayOfMonth.getTime()) {
                firstDayOfWeek = firstDayOfMonth;
            }
            if (lastDayOfWeek.getTime() > lastDayOfMonth.getTime()) {
                lastDayOfWeek = lastDayOfMonth;
            }
            FactAndPlan summaryForTheWeek = new FactAndPlan();
            summaryForTheWeek.fact.setTime(
                    validationTools.userWorkHoursForPeriod(firstDayOfWeek, lastDayOfWeek, userSession.getUser())
            );
            summaryForTheWeek.plan.setTime(
                    validationTools.workHoursForPeriod(firstDayOfWeek, lastDayOfWeek)
            );
            summariesByWeeks[++weekIndex] = summaryForTheWeek;
        }
        return summariesByWeeks;
    }

    protected void updateCalendarRange() {
        calendar.setStartDate(firstDayOfMonth);
        calendar.setEndDate(DateTimeUtils.getLastDayOfMonth(firstDayOfMonth));

        updateSummaryColumn();
        updateMonthCaption();
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

    protected class CalendarActionHandler implements Action.Handler {
        protected Action addEventAction = new Action(messages.getMessage(getClass(), "addTimeEntry"));
        protected Action deleteEventAction = new Action(messages.getMessage(getClass(), "deleteTimeEntry"));

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
            if (action == addEventAction) {
                // Check that the click was not done on an event
                if (target instanceof Date) {
                    Date date = (Date) target;
                    TimeEntry timeEntry = new TimeEntry();
                    timeEntry.setDate(date);
                    editTimeEntry(timeEntry);
                } else {
                    showNotification(messages.getMessage(getClass(), "cantAddTimeEntry"),
                            NotificationType.WARNING);
                }
            } else if (action == deleteEventAction) {
                // Check if the action was clicked on top of an event
                if (target instanceof HolidayCalendarEventAdapter) {
                    showNotification(messages.getMessage(getClass(), "cantDeleteHoliday"),
                            NotificationType.WARNING);
                } else if (target instanceof TimeEntryCalendarEventAdapter) {
                    TimeEntryCalendarEventAdapter event = (TimeEntryCalendarEventAdapter) target;
                    new EventRemoveAction("eventRemove", getFrame(), event).actionPerform(null);
                } else {
                    showNotification(messages.getMessage(getClass(), "cantDeleteTimeEntry"),
                            NotificationType.WARNING);
                }
            }
        }
    }

    protected class EventRemoveAction extends ComponentsHelper.CustomRemoveAction {

        protected TimeEntryCalendarEventAdapter event;

        protected EventRemoveAction(String id, IFrame frame, TimeEntryCalendarEventAdapter event) {
            super(id, frame);
            this.event = event;
        }

        @Override
        protected void doRemove() {
            calendar.removeEvent(event);
            getDsContext().getDataSupplier().remove(event.getTimeEntry());
        }
    }

    protected static class FactAndPlan {

        protected HoursAndMinutes fact = new HoursAndMinutes();
        protected HoursAndMinutes plan = new HoursAndMinutes();

        protected boolean isMatch() {
            BigDecimal factTime = fact.getTime().setScale(ValidationTools.SCALE, BigDecimal.ROUND_HALF_UP);
            BigDecimal planTime = plan.getTime().setScale(ValidationTools.SCALE, BigDecimal.ROUND_HALF_UP);
            return planTime.equals(factTime);
        }
    }
}