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

package com.haulmont.timesheets.web.calendar;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.widgets.CubaVerticalActionsLayout;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.*;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
import com.haulmont.timesheets.gui.holiday.HolidayEdit;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.service.ProjectsService;
import com.haulmont.timesheets.web.toolkit.ui.TimeSheetsCalendar;
import com.vaadin.event.Action;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.Calendar;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.v7.ui.components.calendar.CalendarDateRange;
import com.vaadin.v7.ui.components.calendar.event.CalendarEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.util.*;

import static com.haulmont.cuba.gui.components.Window.COMMIT_ACTION_ID;

/**
 * @author gorelov
 */
@SuppressWarnings("WeakerAccess")
@UiController("calendar-screen")
@UiDescriptor("calendar-screen.xml")
public class CalendarScreen extends Screen {
    @Inject
    protected ViewRepository viewRepository;
    @Inject
    protected UserSession userSession;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected Notifications notifications;
    @Inject
    protected Messages messages;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected ValidationTools validationTools;
    @Inject
    protected UuidSource uuidSource;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected WorkdaysTools workdaysTools;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected CollectionLoader<Project> projectsDl;
    @Inject
    protected CollectionContainer<Project> projectsDc;
    @Inject
    protected TextField<String> spentTime;
    @Inject
    protected LookupField<Task> task;
    @Inject
    protected LookupField activityType;
    @Inject
    protected DateField<java.sql.Date> monthSelector;
    @Inject
    protected HBoxLayout simpleViewHBox;
    @Inject
    protected HBoxLayout commandLineHBox;
    @Inject
    protected VBoxLayout calBox;
    @Inject
    protected VBoxLayout summaryBox;
    @Inject
    protected Label<String> monthSummary;
    @Inject
    protected CheckBox showSimpleView;
    @Inject
    protected Label<String> monthLabel;
    @Inject
    protected Fragment commandLine;

    protected TimeSheetsCalendar calendar;
    protected Date firstDayOfMonth;
    protected TimeSheetsCalendarEventProvider dataSource;

    @Subscribe
    public void onInit(InitEvent event) {
        projectsDl.setParameter("user", userSession.getCurrentOrSubstitutedUser());
        projectsDl.load();
        firstDayOfMonth = DateTimeUtils.getFirstDayOfMonth(timeSource.currentTimestamp());

        initCalendar();

        monthSelector.addValueChangeListener(e -> {
            firstDayOfMonth = DateTimeUtils.getFirstDayOfMonth(e.getValue());
            updateCalendarRange();
        });

        ((CommandLineFrameController) commandLine.getFrameOwner()).setTimeEntriesHandler(new CommandLineFrameController.ResultTimeEntriesHandler() {
            @Override
            public void handle(List<TimeEntry> resultTimeEntries) {
                if (CollectionUtils.isNotEmpty(resultTimeEntries)) {
                    TimeEntry timeEntry = resultTimeEntries.get(0);
                    ResultAndCause resultAndCause = validationTools.validateTimeEntry(timeEntry);
                    if (resultAndCause.isNegative) {
                        notifications.create()
                                .withType(Notifications.NotificationType.WARNING)
                                .withCaption(resultAndCause.cause)
                                .show();
                        return;
                    }

                    ResultAndCause tagsValidationResult = validationTools.validateTags(timeEntry);
                    if (tagsValidationResult.isNegative) {
                        dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                                .withContentMode(com.haulmont.cuba.gui.components.ContentMode.HTML)
                                .withCaption(messageBundle.getMessage("caption.attention"))
                                .withMessage(tagsValidationResult.cause + messageBundle.getMessage("confirmation.manuallyTagSetting"))
                                .withActions(
                                        new DialogAction(DialogAction.Type.YES) {
                                            @Override
                                            public void actionPerform(Component component) {
                                                doHandle(timeEntry);
                                            }
                                        },
                                        new DialogAction(DialogAction.Type.NO))
                                .show();
                    } else {
                        doHandle(timeEntry);
                    }
                }
            }

            private void doHandle(TimeEntry timeEntry) {
                final List<TimeEntry> results = new ArrayList<>();
                java.util.Calendar javaCalendar = java.util.Calendar.getInstance();
                javaCalendar.setTime(firstDayOfMonth);
                int currentMonth = javaCalendar.get(java.util.Calendar.MONTH);
                int nextDayMonth = javaCalendar.get(java.util.Calendar.MONTH);

                while (currentMonth == nextDayMonth) {
                    if (workdaysTools.isWorkday(javaCalendar.getTime())) {
                        TimeEntry copy = metadata.getTools().copy(timeEntry);
                        copy.setId(uuidSource.createUuid());
                        copy.setDate(javaCalendar.getTime());
                        copy.setStatus(TimeEntryStatus.NEW);
                        copy.setUser(userSession.getCurrentOrSubstitutedUser());
                        results.add(copy);
                    }

                    javaCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    nextDayMonth = javaCalendar.get(java.util.Calendar.MONTH);
                }

                CommitContext context = new CommitContext();
                context.getCommitInstances().addAll(results);
                @SuppressWarnings("unchecked")
                Set<TimeEntry> committed = (Set) dataManager.commit(context);
                List<CalendarEvent> events = new ArrayList<>();
                for (TimeEntry entry : committed) {
                    events.add(new TimeEntryCalendarEventAdapter(entry));
                }
                dataSource.addEvents(events);
            }
        });

        showSimpleView.addValueChangeListener(e -> {
            if (Boolean.TRUE.equals(e.getValue())) {
                commandLine.setVisible(false);
                simpleViewHBox.setVisible(true);
            } else {
                commandLine.setVisible(true);
                simpleViewHBox.setVisible(false);
            }
        });

        projectsDc.addItemChangeListener(e -> {
            setActivityTypeVisibility(e.getItem(), activityType);
        });
    }

    @Subscribe("showCommandLine")
    public void onShowCommandLine(com.haulmont.cuba.gui.components.Action.ActionPerformedEvent event) {
        showCommandLine();
    }

    protected void setActivityTypeVisibility(Project project, LookupField activityTypeLookupField) {
        List<ActivityType> activityTypes = getActivityTypesForProject(project);
        if (CollectionUtils.isNotEmpty(activityTypes)) {
            activityTypeLookupField.setVisible(true);
            activityTypeLookupField.setOptionsList(activityTypes);
        } else {
            activityTypeLookupField.setVisible(false);
            activityTypeLookupField.setOptionsList(Collections.emptyList());
        }
    }

    protected List<ActivityType> getActivityTypesForProject(Project project) {
        if (project != null) {
            return projectsService.getActivityTypesForProject(project, View.MINIMAL);
        } else {
            return Collections.emptyList();
        }
    }

    public void simpleViewApply() {
        TimeEntry timeEntry = metadata.create(TimeEntry.class);
        timeEntry.setTask(task.getValue());
        timeEntry.setTimeInMinutes(HoursAndMinutes.fromString(spentTime.getValue()).toMinutes());
        ((CommandLineFrameController) commandLine.getFrameOwner()).getTimeEntriesHandler().handle(Arrays.asList(timeEntry));
    }

    protected void showCommandLine() {
        commandLineHBox.setVisible(!commandLineHBox.isVisible());
    }

    protected void initCalendar() {
        dataSource = new TimeSheetsCalendarEventProvider(userSession.getCurrentOrSubstitutedUser());
        dataSource.addEventSetChangeListener(changeEvent -> updateSummaryColumn());

        calendar = new TimeSheetsCalendar(dataSource);

        calendar.setWidth("100%");
        calendar.setHeight("100%");
        calendar.setTimeFormat(Calendar.TimeFormat.Format24H);
        calendar.setMoreMsgFormat(messages.getMessage(getClass(), "calendar.moreMsgFormat"));
        calendar.setDropHandler(null);
        calendar.setHandler((CalendarComponentEvents.MoveEvent event) -> {
            if (event.getCalendarEvent() instanceof TimeEntryCalendarEventAdapter) {
                TimeEntryCalendarEventAdapter adapter = (TimeEntryCalendarEventAdapter) event.getCalendarEvent();
                adapter.getTimeEntry().setDate(event.getNewStart());
                TimeEntry committed = dataManager.commit(adapter.getTimeEntry(),
                        viewRepository.getView(TimeEntry.class, "timeEntry-full"));
                dataSource.changeEventTimeEntity(committed);
                updateSummaryColumn();
            }
        });
        calendar.setHandler((CalendarComponentEvents.WeekClickHandler) null);
        calendar.setHandler((CalendarComponentEvents.DateClickEvent event) -> {
            TimeEntry timeEntry = metadata.create(TimeEntry.class);
            timeEntry.setDate(event.getDate());
            editTimeEntry(timeEntry);
        });
        calendar.setHandler((CalendarComponentEvents.EventResizeHandler) null);
        calendar.setHandler((CalendarComponentEvents.EventClick event) -> {
            if (event.getCalendarEvent() instanceof TimeEntryCalendarEventAdapter) {
                TimeEntryCalendarEventAdapter eventAdapter = (TimeEntryCalendarEventAdapter) event.getCalendarEvent();
                editTimeEntry(eventAdapter.getTimeEntry());
            } else if (event.getCalendarEvent() instanceof HolidayCalendarEventAdapter) {
                HolidayCalendarEventAdapter eventAdapter = (HolidayCalendarEventAdapter) event.getCalendarEvent();
                editHoliday(eventAdapter.getHoliday());
            }
        });
        calendar.addActionHandler(new CalendarActionHandler());

        AbstractOrderedLayout calendarLayout = (AbstractOrderedLayout) WebComponentsHelper.unwrap(calBox);
        calendarLayout.addComponent(calendar);
        calendarLayout.setExpandRatio(calendar, 1);

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
        editTimeEntry(metadata.create(TimeEntry.class));
    }

    protected void updateSummaryColumn() {
        summaryBox.removeAll();
        CubaVerticalActionsLayout summaryLayout = (CubaVerticalActionsLayout) WebComponentsHelper.unwrap(summaryBox);
        CubaVerticalActionsLayout summaryCaptionVbox = new CubaVerticalActionsLayout();
        summaryCaptionVbox.setHeight("30px");
        summaryCaptionVbox.setWidth("100%");
        com.vaadin.ui.Label summaryCaption = new com.vaadin.ui.Label();
        summaryCaption.setContentMode(ContentMode.HTML);
        summaryCaption.setValue(messageBundle.getMessage("label.summaryCaption"));
        summaryCaption.setWidthUndefined();
        summaryCaptionVbox.addComponent(summaryCaption);
        summaryCaptionVbox.setComponentAlignment(summaryCaption, Alignment.MIDDLE_CENTER);
        summaryLayout.addComponent(summaryCaptionVbox);

        FactAndPlan[] summariesByWeeks = calculateSummariesByWeeks();
        FactAndPlan summaryForMonth = new FactAndPlan();
        for (int i = 1; i < summariesByWeeks.length; i++) {
            com.vaadin.ui.Label hourLabel = new com.vaadin.ui.Label();
            hourLabel.setContentMode(ContentMode.HTML);
            FactAndPlan summaryForTheWeek = summariesByWeeks[i];
            if (summaryForTheWeek == null) {
                summaryForTheWeek = new FactAndPlan();
            }
            if (summaryForTheWeek.isMatch()) {
                hourLabel.setValue(messages.formatMessage(CalendarScreen.class, "label.hoursSummary",
                        summaryForTheWeek.fact.getHours(), summaryForTheWeek.fact.getMinutes()));
            } else {
                hourLabel.setValue(messages.formatMessage(CalendarScreen.class, "label.hoursSummaryNotMatch",
                        summaryForTheWeek.fact.getHours(), summaryForTheWeek.fact.getMinutes(),
                        summaryForTheWeek.plan.getHours(), summaryForTheWeek.plan.getMinutes()));
                hourLabel.addStyleName("overtime");
            }
            hourLabel.setWidthUndefined();
            summaryLayout.addComponent(hourLabel);
            summaryLayout.setExpandRatio(hourLabel, 1);
            summaryLayout.setComponentAlignment(hourLabel, Alignment.MIDDLE_CENTER);

            summaryForMonth.fact.add(summaryForTheWeek.fact);
            summaryForMonth.plan.add(summaryForTheWeek.plan);
        }

        if (summaryForMonth.isMatch()) {
            monthSummary.setValue(messages.formatMessage(CalendarScreen.class, "label.monthSummaryFormat",
                    summaryForMonth.fact.getHours(), summaryForMonth.fact.getMinutes()));
            monthSummary.setStyleName("month-summary");
        } else {
            monthSummary.setValue(messages.formatMessage(CalendarScreen.class, "label.monthSummaryFormatNotMatch",
                    summaryForMonth.fact.getHours(), summaryForMonth.fact.getMinutes(),
                    summaryForMonth.plan.getHours(), summaryForMonth.plan.getMinutes()));
            monthSummary.setStyleName("month-summary-overtime");
        }
    }

    protected FactAndPlan[] calculateSummariesByWeeks() {
        Date start = firstDayOfMonth;
        java.util.Calendar javaCalendar = java.util.Calendar.getInstance(userSession.getLocale());
        javaCalendar.setMinimalDaysInFirstWeek(1);
        javaCalendar.setTime(firstDayOfMonth);
        int countOfWeeksInTheMonth = javaCalendar.getActualMaximum(java.util.Calendar.WEEK_OF_MONTH);
        Date lastDayOfMonth = DateUtils.addHours(DateTimeUtils.getLastDayOfMonth(firstDayOfMonth), 23);

        FactAndPlan[] summariesByWeeks = new FactAndPlan[countOfWeeksInTheMonth + 1];
        for (int i = 0; i < countOfWeeksInTheMonth; i++) {
            Date firstDayOfWeek = DateTimeUtils.getFirstDayOfWeek(start);
            Date lastDayOfWeek = DateUtils.addHours(DateTimeUtils.getLastDayOfWeek(start), 23);

            if (firstDayOfWeek.getTime() < firstDayOfMonth.getTime()) {
                firstDayOfWeek = firstDayOfMonth;
            }
            if (lastDayOfWeek.getTime() > lastDayOfMonth.getTime()) {
                lastDayOfWeek = lastDayOfMonth;
            }
            FactAndPlan summaryForTheWeek = new FactAndPlan();
            User currentOrSubstitutedUser = userSession.getCurrentOrSubstitutedUser();
            summaryForTheWeek.fact.setTime(
                    validationTools.actualWorkHoursForPeriod(firstDayOfWeek, lastDayOfWeek, currentOrSubstitutedUser)
            );
            summaryForTheWeek.plan.setTime(
                    validationTools.workHoursForPeriod(firstDayOfWeek, lastDayOfWeek, currentOrSubstitutedUser)
            );
            summariesByWeeks[i + 1] = summaryForTheWeek;
            start = DateUtils.addWeeks(start, 1);
        }
        return summariesByWeeks;
    }

    protected void updateCalendarRange() {
        Date lastDayOfMonth = DateTimeUtils.getLastDayOfMonth(firstDayOfMonth);

        dataSource.updateWithRange(
                DateTimeUtils.getFirstDayOfWeek(firstDayOfMonth),
                DateTimeUtils.getLastDayOfWeek(lastDayOfMonth));

        calendar.setStartDate(firstDayOfMonth);
        calendar.setEndDate(lastDayOfMonth);

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
        screenBuilders.editor(TimeEntry.class, this)
                .withScreenClass(TimeEntryEdit.class)
                .editEntity(timeEntry)
                .withLaunchMode(OpenMode.DIALOG)
                .withAfterCloseListener(ace -> {
                    if (COMMIT_ACTION_ID.equals(((StandardCloseAction) ace.getCloseAction()).getActionId())) {
                        dataSource.changeEventTimeEntity(ace.getScreen().getEditedEntity());
                    }
                })
                .build()
                .show();
    }

    protected void editHoliday(Holiday holiday) {
        screenBuilders.editor(Holiday.class, this)
                .withScreenClass(HolidayEdit.class)
                .editEntity(holiday)
                .withLaunchMode(OpenMode.DIALOG)
                .withAfterCloseListener(ace -> {
                    if (COMMIT_ACTION_ID.equals(((StandardCloseAction) ace.getCloseAction()).getActionId())) {
                        dataSource.changeEventHoliday(ace.getScreen().getEditedEntity());
                    }
                })
                .build()
                .show();
    }

    protected class CalendarActionHandler implements Action.Handler {
        protected Action addEventAction = new Action(messages.getMessage(getClass(), "addTimeEntry"));
        protected Action deleteEventAction = new Action(messages.getMessage(getClass(), "deleteTimeEntry"));
        protected Action copyEventAction = new Action(messages.getMessage(getClass(), "copyTimeEntry"));

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
                return new Action[]{addEventAction, copyEventAction, deleteEventAction};
        }

        @Override
        public void handleAction(Action action, Object sender, Object target) {
            if (action == addEventAction) {
                // Check that the click was not done on an event
                if (target instanceof Date) {
                    Date date = (Date) target;
                    TimeEntry timeEntry = metadata.create(TimeEntry.class);
                    timeEntry.setDate(date);
                    editTimeEntry(timeEntry);
                } else {
                    notifications.create()
                            .withType(Notifications.NotificationType.WARNING)
                            .withCaption(messages.getMessage(getClass(), "cantAddTimeEntry"))
                            .show();
                }
            } else if (action == copyEventAction) {
                // Check that the click was not done on an event
                if (target instanceof TimeEntryCalendarEventAdapter) {
                    TimeEntry copiedEntry = metadata.getTools().copy(((TimeEntryCalendarEventAdapter) target).getTimeEntry());
                    copiedEntry.setId(uuidSource.createUuid());
                    copiedEntry.setStatus(TimeEntryStatus.NEW);

                    CommitContext context = new CommitContext();
                    context.getCommitInstances().add(copiedEntry);
                    Set<Entity> entities = dataManager.commit(context);
                    dataSource.changeEventTimeEntity((TimeEntry) entities.iterator().next());
                }
            } else if (action == deleteEventAction) {
                // Check if the action was clicked on top of an event
                if (target instanceof HolidayCalendarEventAdapter) {
                    notifications.create()
                            .withType(Notifications.NotificationType.WARNING)
                            .withCaption(messages.getMessage(getClass(), "cantDeleteHoliday"))
                            .show();
                } else if (target instanceof TimeEntryCalendarEventAdapter) {
                    TimeEntryCalendarEventAdapter event = (TimeEntryCalendarEventAdapter) target;
                    new EventRemoveAction("eventRemove", dialogs, event).actionPerform(null);
                } else {
                    notifications.create()
                            .withType(Notifications.NotificationType.WARNING)
                            .withCaption(messages.getMessage(getClass(), "cantDeleteTimeEntry"))
                            .show();
                }
            }
        }
    }

    protected class EventRemoveAction extends ScreensHelper.CustomRemoveAction {

        protected TimeEntryCalendarEventAdapter event;

        protected EventRemoveAction(String id, Dialogs dialogs, TimeEntryCalendarEventAdapter event) {
            super(id, dialogs);
            this.event = event;
        }

        @Override
        protected void doRemove() {
            dataManager.remove(event.getTimeEntry());
            calendar.removeEvent(event);
        }
    }

    protected static class FactAndPlan {

        protected HoursAndMinutes fact = new HoursAndMinutes();
        protected HoursAndMinutes plan = new HoursAndMinutes();

        protected boolean isMatch() {
            return fact.equals(plan);
        }
    }
}