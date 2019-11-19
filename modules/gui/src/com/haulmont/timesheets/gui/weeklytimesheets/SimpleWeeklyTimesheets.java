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

package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.actions.list.RemoveAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.data.options.ContainerOptions;
import com.haulmont.cuba.gui.components.data.value.ContainerValueSource;
import com.haulmont.cuba.gui.model.CollectionChangeType;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.*;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.gui.timeentry.TimeEntryLookup;
import com.haulmont.timesheets.gui.util.ScreensHelper;
import com.haulmont.timesheets.gui.util.WeeklyReportEntryAggregation;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.*;

/**
 * @author gorelov
 */
@UiController("simple-weekly-timesheets")
@UiDescriptor("simple-weekly-timesheets.xml")
public class SimpleWeeklyTimesheets extends Screen {
    protected static final String COLUMN_SUFFIX = "Column";
    protected static final String TOTAL_COLUMN_ID = "totalColumn";

    @Inject
    protected TimeSource timeSource;
    @Inject
    protected UuidSource uuidSource;
    @Inject
    protected Messages messages;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected Notifications notifications;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected ScreenBuilders screenBuilders;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected WeeklyReportConverter weeklyReportConverter;
    @Inject
    protected TimeParser timeParser;
    @Inject
    protected ValidationTools validationTools;
    @Inject
    protected UserSession userSession;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Label<String> weekCaption;
    @Inject
    protected CollectionContainer<WeeklyReportEntry> weeklyEntriesDc;
    @Inject
    protected CollectionContainer<Project> projectsDc;
    @Inject
    protected CollectionLoader<Project> projectsDl;
    @Inject
    protected LinkButton showPreviousWeek;
    @Inject
    protected LinkButton showNextWeek;
    @Inject
    protected LinkButton setToday;
    @Inject
    protected CommandLineFrameController commandLine;
    @Inject
    protected GroupTable<WeeklyReportEntry> weeklyTsTable;

    protected Map<String, Label> totalLabelsMap = new HashMap<>();

    protected Date firstDayOfWeek;
    protected Date lastDayOfWeek;

    @Install(to = "weeklyTsTable", subject = "styleProvider")
    private String weeklyTsTableStyleProvider(WeeklyReportEntry entity, String property) {
        String id = null;
        if (property != null && property.endsWith(COLUMN_SUFFIX)) {
            id = property.replace(COLUMN_SUFFIX, "");
        }

        DayOfWeek day = DayOfWeek.fromId(id != null ? id : property);
        if (entity == null) {
            User currentOrSubstitutedUser = userSession.getCurrentOrSubstitutedUser();
            if (day != null) {
                return validationTools.isWorkTimeMatchToPlanForDay(
                        DateTimeUtils.getSpecificDayOfWeek(firstDayOfWeek, day.getJavaCalendarDay()),
                        currentOrSubstitutedUser) ? null : "overtime";
            } else if (TOTAL_COLUMN_ID.equals(property)) {
                return validationTools.isWorkTimeMatchToPlanForWeek(
                        firstDayOfWeek, currentOrSubstitutedUser) ? null : "overtime";
            }
        }
        return null;
    }

    @Subscribe("add")
    protected void onAdd(Action.ActionPerformedEvent event) {
        addReport();
    }

    @Subscribe("submitAll")
    protected void onSubmitAll(Action.ActionPerformedEvent event) {
        submitAll();
    }

    @Subscribe("weeklyTsTable.remove")
    protected void onWeeklyTsTableRemove(Action.ActionPerformedEvent event) {
        Set<WeeklyReportEntry> selected = weeklyTsTable.getSelected();
        if (!selected.isEmpty()) {
            for (WeeklyReportEntry entry : selected) {
                removeTimeEntries(entry.getExistTimeEntries());
            }

            for (WeeklyReportEntry item : selected) {
                weeklyEntriesDc.getMutableItems().remove(item);
            }
        }
    }

    @Subscribe
    protected void onInit(InitEvent event) {
        setWeekRange(DateTimeUtils.getFirstDayOfWeek(timeSource.currentTimestamp()));
        weeklyTsTable.setSettingsEnabled(false);
        updateWeekCaption();
        fillExistingTimeEntries();
        initWeeklyEntriesTable();
        initDateChangeComponents();
        initCommandLine();
        updateDayColumnsCaptions();
    }

    @Subscribe("dateField")
    protected void onDateFieldValueChange(HasValue.ValueChangeEvent<Date> e) {
        if (hasUnsavedData()) {
            dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                    .withCaption(messages.getMainMessage("closeUnsaved.caption"))
                    .withMessage(messageBundle.getMessage("notification.unsavedData"))
                    .withActions(
                            new DialogAction(DialogAction.Type.OK) {
                                @Override
                                public void actionPerform(Component component) {
                                    setWeekRange(DateTimeUtils.getFirstDayOfWeek(e.getValue()));
                                    updateWeek();
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL))
                    .show();
        } else {
            setWeekRange(DateTimeUtils.getFirstDayOfWeek(e.getValue()));
            updateWeek();
        }
    }

    protected void initDateChangeComponents() {
        setToday.setAction(new CheckUnsavedAction("") {
            @Override
            public void doAction() {
                super.doAction();
                setToday();
            }
        });

        showNextWeek.setAction(new CheckUnsavedAction("") {
            @Override
            public void doAction() {
                showNextWeek();
            }
        });

        showPreviousWeek.setAction(new CheckUnsavedAction("") {
            @Override
            public void doAction() {
                showPreviousWeek();
            }
        });
    }

    protected void initCommandLine() {
        commandLine.setTimeEntriesHandler(new CommandLineFrameController.ResultTimeEntriesHandler() {
            @Override
            public void handle(List<TimeEntry> resultTimeEntries) {
                if (CollectionUtils.isNotEmpty(resultTimeEntries)) {
                    TimeEntry timeEntry = resultTimeEntries.get(0);
                    ResultAndCause resultAndCause = validationTools.validateTimeEntry(timeEntry);
                    if (resultAndCause.isNegative) {
                        notifications.create(Notifications.NotificationType.WARNING)
                                .withCaption(resultAndCause.cause)
                                .show();
                        return;
                    }
                    ResultAndCause validationResult = validationTools.validateTags(timeEntry);
                    if (validationResult.isNegative) {
                        dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                                .withContentMode(ContentMode.HTML)
                                .withCaption(messageBundle.getMessage("caption.attention"))
                                .withMessage(validationResult.cause + messageBundle.getMessage("confirmation.manuallyTagSetting"))
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
                WeeklyReportEntry weeklyReportEntry = setTimeEntryToEachWeekDay(timeEntry);
                weeklyEntriesDc.getMutableItems().add(weeklyReportEntry);
                weeklyTsTable.setSelected(weeklyReportEntry);
            }

            private WeeklyReportEntry setTimeEntryToEachWeekDay(TimeEntry timeEntry) {
                String spentTimeStr = HoursAndMinutes.fromTimeEntry(timeEntry).toString();

                WeeklyReportEntry weeklyReportEntry = new WeeklyReportEntry();
                weeklyReportEntry.setTask(timeEntry.getTask());
                weeklyReportEntry.setActivityType(timeEntry.getActivityType());
                weeklyReportEntry.setProject(timeEntry.getTask().getProject());

                weeklyReportEntry.setMonday(copyAndPutToList(timeEntry));
                weeklyReportEntry.setMondayTime(spentTimeStr);

                weeklyReportEntry.setTuesday(copyAndPutToList(timeEntry));
                weeklyReportEntry.setTuesdayTime(spentTimeStr);

                weeklyReportEntry.setWednesday(copyAndPutToList(timeEntry));
                weeklyReportEntry.setWednesdayTime(spentTimeStr);

                weeklyReportEntry.setThursday(copyAndPutToList(timeEntry));
                weeklyReportEntry.setThursdayTime(spentTimeStr);

                weeklyReportEntry.setFriday(copyAndPutToList(timeEntry));
                weeklyReportEntry.setFridayTime(spentTimeStr);
                return weeklyReportEntry;
            }

            private List<TimeEntry> copyAndPutToList(TimeEntry timeEntry) {
                List<TimeEntry> timeEntries = new ArrayList<>();
                timeEntries.add(doCopy(timeEntry));
                return timeEntries;
            }

            private TimeEntry doCopy(TimeEntry timeEntry) {
                TimeEntry copy = metadata.getTools().copy(timeEntry);
                copy.setId(uuidSource.createUuid());
                return copy;
            }
        });
    }

    protected void initWeeklyEntriesTable() {
        initProjectColumn();
        initTaskColumn();
        initDaysColumns();
        initTotalColumn();

        weeklyEntriesDc.addCollectionChangeListener(e -> {
            if (CollectionChangeType.REMOVE_ITEMS.equals(e.getChangeType())) {
                for (WeeklyReportEntry entry : e.getChanges()) {
                    totalLabelsMap.remove(ScreensHelper.getCacheKeyForEntity(entry, TOTAL_COLUMN_ID));
                }
            }
        });
    }

    protected void initProjectColumn() {
        String projectColumnId = "project";
        weeklyTsTable.addGeneratedColumn(projectColumnId, weeklyReportEntry -> {
            if (weeklyReportEntry.hasTimeEntries()) {
                Label label = uiComponents.create(Label.class);
                label.setValue(weeklyReportEntry.getProject().getName());
                return label;
            } else {
                InstanceContainer<WeeklyReportEntry> dc = weeklyTsTable.getInstanceContainer(weeklyReportEntry);
                LookupField lookupField = uiComponents.create(LookupField.class);
                lookupField.setValueSource(new ContainerValueSource(dc, projectColumnId));
                lookupField.setOptions(new ContainerOptions<>(projectsDc));
                lookupField.setWidth("100%");
                lookupField.setInputPrompt(messages.getMessage(WeeklyReportEntry.class, "WeeklyReportEntry.project"));
                lookupField.focus();
                return lookupField;
            }
        });

        weeklyTsTable.sort(projectColumnId, Table.SortDirection.ASCENDING);
    }

    protected void initTaskColumn() {
        String taskColumnId = "task";
        String activityTypeColumnId = "activityType";
        weeklyTsTable.addGeneratedColumn(taskColumnId, weeklyReportEntry -> {
            if (weeklyReportEntry.hasTimeEntries()) {
                Label label = uiComponents.create(Label.class);
                String caption;
                if (weeklyReportEntry.getActivityType() == null) {
                    caption = weeklyReportEntry.getTask().getName();
                } else {
                    caption = String.format("%s (%s)",
                            weeklyReportEntry.getTask().getName(),
                            weeklyReportEntry.getActivityType().getInstanceName());
                }
                label.setValue(caption);
                return label;
            } else {
                InstanceContainer<WeeklyReportEntry> dc =
                        weeklyTsTable.getInstanceContainer(weeklyReportEntry);
                LookupField taskLookupField = uiComponents.create(LookupField.class);
                taskLookupField.setValueSource(new ContainerValueSource<>(dc, taskColumnId));
                taskLookupField.setWidth("100%");
                taskLookupField.setInputPrompt(messages.getMessage(WeeklyReportEntry.class, "WeeklyReportEntry.task"));

                LookupField activityTypeLookupField = uiComponents.create(LookupField.class);
                activityTypeLookupField.setValueSource(new ContainerValueSource<>(dc, activityTypeColumnId));
                activityTypeLookupField.setWidth("100%");
                activityTypeLookupField.setRequired(true);
                activityTypeLookupField.setInputPrompt(messages.getMessage(WeeklyReportEntry.class, "WeeklyReportEntry.activityType"));
                setActivityTypeVisibility(dc.getItem().getProject(), activityTypeLookupField);

                dc.addItemPropertyChangeListener(e -> {
                    if ("project".equals(e.getProperty())) {
                        Project project = (Project) e.getValue();
                        taskLookupField.setValue(null);
                        Map<String, Object> tasks = getTasksForCurrentUserAndProject(project);
                        taskLookupField.setOptionsMap(tasks);
                        setActivityTypeVisibility(project, activityTypeLookupField);
                    }
                });

                Project project = dc.getItem().getProject();
                if (project != null) {
                    Map<String, Object> tasks = getTasksForCurrentUserAndProject(project);
                    taskLookupField.setOptionsMap(tasks);
                }

                HBoxLayout boxLayout = uiComponents.create(HBoxLayout.class);
                boxLayout.setWidth("100%");
                boxLayout.setSpacing(true);
                boxLayout.add(taskLookupField);
                boxLayout.add(activityTypeLookupField);
                return boxLayout;
            }
        });
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

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getTasksForCurrentUserAndProject(Project project) {
        return (Map) projectsService.getActiveTasksForUserAndProject(
                userSession.getCurrentOrSubstitutedUser(),
                project,
                "task-full");
    }

    protected void initDaysColumns() {
        for (Date current = firstDayOfWeek; current.getTime() <= lastDayOfWeek.getTime(); current = DateUtils.addDays(current, 1)) {
            DayOfWeek day = DayOfWeek.fromCalendarDay(DateUtils.toCalendar(current).get(Calendar.DAY_OF_WEEK));
            String columnId = day.getId() + COLUMN_SUFFIX;
            projectsDl.setParameter("user", userSession.getCurrentOrSubstitutedUser());
            projectsDl.load();
            weeklyTsTable.addGeneratedColumn(columnId, new Table.ColumnGenerator<WeeklyReportEntry>() {
                        @Override
                        public Component generateCell(final WeeklyReportEntry entity) {
                            List<TimeEntry> timeEntries = entity.getDayOfWeekTimeEntries(day);
                            if (CollectionUtils.isEmpty(timeEntries)
                                    || timeEntries.size() == 1 && PersistenceHelper.isNew(timeEntries.get(0))) {
                                return createTextFieldForTimeInput(entity);
                            } else {
                                return createLinkAndActionsForExistingEntry(entity, timeEntries);
                            }
                        }

                        private Component createLinkAndActionsForExistingEntry(WeeklyReportEntry reportEntry, List<TimeEntry> timeEntries) {
                            HBoxLayout hBox = uiComponents.create(HBoxLayout.class);
                            hBox.setSpacing(true);

                            if (timeEntries.size() == 1) {
                                createLinkToSingleTimeEntry(reportEntry, timeEntries, hBox);
                            } else {
                                createLinkToMultipleTimeEntries(reportEntry, hBox, timeEntries.get(0).getDate());
                            }
                            createRemoveButton(reportEntry, hBox);

                            return hBox;
                        }

                        private void createRemoveButton(final WeeklyReportEntry reportEntry, HBoxLayout hBox) {
                            LinkButton removeButton = uiComponents.create(LinkButton.class);
                            removeButton.setIcon("icons/remove.png");
                            removeButton.setAlignment(Component.Alignment.MIDDLE_RIGHT);
                            removeButton.setAction(new ScreensHelper.CustomRemoveAction("timeEntryRemove", dialogs) {
                                @Override
                                protected void doRemove() {
                                    removeTimeEntries(reportEntry.getDayOfWeekTimeEntries(day));
                                    reportEntry.changeDayOfWeekTimeEntries(day, null);
                                }
                            });
                            hBox.add(removeButton);
                        }

                        private void createLinkToMultipleTimeEntries(final WeeklyReportEntry reportEntry, HBoxLayout hBox, final Date date) {
                            LinkButton linkButton = uiComponents.create(LinkButton.class);
                            linkButton.setCaption(reportEntry.getTotalForDay(day).toString());
                            linkButton.setAction(new BaseAction("edit").withHandler(ae -> {
                                TimeEntryLookup lookup = screenBuilders.lookup(TimeEntry.class, SimpleWeeklyTimesheets.this)
                                        .withScreenClass(TimeEntryLookup.class)
                                        .withLaunchMode(OpenMode.DIALOG)
                                        .withAfterCloseListener(ce -> updateWeek())
                                        .build();
                                lookup.setUser(userSession.getCurrentOrSubstitutedUser());
                                lookup.setTask(reportEntry.getTask());
                                lookup.setDate(date);
                                lookup.setActivityType(reportEntry.getActivityType());
                                lookup.show();
                            }));
                            hBox.add(linkButton);
                        }

                        private void createLinkToSingleTimeEntry(final WeeklyReportEntry reportEntry, List<TimeEntry> timeEntries, HBoxLayout hBox) {
                            TimeEntry timeEntry = timeEntries.get(0);
                            LinkButton linkButton = uiComponents.create(LinkButton.class);
                            linkButton.setCaption(reportEntry.getTotalForDay(day).toString());
                            linkButton.setAction(new CheckUnsavedAction("edit") {
                                @Override
                                public void actionPerform(Component component) {
                                    openTimeEntryEditor(timeEntry);
                                }
                            });

                            hBox.add(linkButton);
                        }

                        private Component createTextFieldForTimeInput(WeeklyReportEntry reportEntry) {
                            TextField timeField = uiComponents.create(TextField.class);
                            timeField.setWidth("100%");
                            timeField.setHeight("22px");
                            timeField.setValueSource(new ContainerValueSource<>(weeklyTsTable.getInstanceContainer(reportEntry), day.getId() + "Time"));
                            return timeField;
                        }
                    }
            );
            weeklyTsTable.getColumn(columnId).setWidth(80);
            weeklyTsTable.getColumn(columnId).setCaptionAsHtml(true);

            Table.Column column = weeklyTsTable.getColumn(columnId);
            column.setAggregation(ScreensHelper.createAggregationInfo(
                    projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, day.getId()),
                    new WeeklyReportEntryAggregation()
            ));
        }
    }

    protected void initTotalColumn() {
        weeklyTsTable.addGeneratedColumn(TOTAL_COLUMN_ID, reportEntry -> {
            Label label = uiComponents.create(Label.class);
            label.setValue(reportEntry.getTotal());
            totalLabelsMap.put(ScreensHelper.getCacheKeyForEntity(reportEntry, TOTAL_COLUMN_ID), label);
            return label;
        });
        weeklyTsTable.getColumn(TOTAL_COLUMN_ID).setWidth(80);
        weeklyTsTable.getColumn(TOTAL_COLUMN_ID).setCaption(messages.getMessage(getClass(), "total"));

        Table.Column column = weeklyTsTable.getColumn(TOTAL_COLUMN_ID);
        column.setAggregation(ScreensHelper.createAggregationInfo(
                projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, "total"),
                new TotalColumnAggregation()
        ));
    }

    protected void openTimeEntryEditor(TimeEntry timeEntry) {
        screenBuilders.editor(TimeEntry.class, this)
                .withScreenClass(TimeEntryEdit.class)
                .editEntity(timeEntry)
                .withLaunchMode(OpenMode.DIALOG)
                .withAfterCloseListener(ace -> {
                    if (Window.COMMIT_ACTION_ID.equals(((StandardCloseAction) ace.getCloseAction()).getActionId())) {
                        updateWeek();
                    }
                })
                .build()
                .show();
    }

    public void addReport() {
        WeeklyReportEntry item = new WeeklyReportEntry();
        weeklyEntriesDc.getMutableItems().add(item);
        weeklyTsTable.setSelected(item);
    }

    public void submitAll() {
        Collection<WeeklyReportEntry> entries = weeklyEntriesDc.getMutableItems();
        if (!entries.isEmpty()) {
            CommitContext commitContext = new CommitContext();
            List<String> validationAlerts = new ArrayList<>();
            for (WeeklyReportEntry weeklyReportEntry : entries) {
                ResultAndCause resultAndCause = validationTools.validateWeeklyReport(weeklyReportEntry);
                if (resultAndCause.isNegative) {
                    notifications.create(Notifications.NotificationType.WARNING)
                            .withCaption(resultAndCause.cause)
                            .show();
                    return;
                }

                for (final DayOfWeek day : DayOfWeek.values()) {
                    String dayOfWeekTime = weeklyReportEntry.getDayOfWeekTime(day);
                    if (StringUtils.isNotBlank(dayOfWeekTime)) {
                        HoursAndMinutes hoursAndMinutes = timeParser.parseToHoursAndMinutes(dayOfWeekTime);
                        List<TimeEntry> existingEntries = weeklyReportEntry.getDayOfWeekTimeEntries(day);
                        Set<Tag> defaultTags = weeklyReportEntry.getTask().getDefaultTags();

                        TimeEntry timeEntry = existingEntries != null ? existingEntries.get(0) : metadata.create(TimeEntry.class);
                        timeEntry.setUser(userSession.getCurrentOrSubstitutedUser());
                        timeEntry.setTask(weeklyReportEntry.getTask());
                        timeEntry.setTimeInMinutes(hoursAndMinutes.toMinutes());
                        if (timeEntry.getActivityType() == null) {
                            timeEntry.setActivityType(weeklyReportEntry.getActivityType());
                        }

                        if (CollectionUtils.isNotEmpty(timeEntry.getTags())) {
                            HashSet<Tag> tags = new HashSet<>(timeEntry.getTags());
                            tags.addAll(defaultTags);
                            timeEntry.setTags(tags);
                        } else {
                            timeEntry.setTags(defaultTags);
                        }
                        timeEntry.setDate(DateTimeUtils.getSpecificDayOfWeek(firstDayOfWeek, day.getJavaCalendarDay()));

                        ResultAndCause validationResult = validationTools.validateTags(timeEntry);
                        if (validationResult.isNegative) {
                            validationAlerts.add(messages.formatMessage("notification.timeEntryValidation",
                                    validationResult.cause, timeEntry.getTask().getName(),
                                    timeEntry.getDate(), HoursAndMinutes.fromTimeEntry(timeEntry)));
                        }

                        commitContext.getCommitInstances().add(timeEntry);
                    }
                }
            }

            dataManager.commit(commitContext);
            updateWeek();

            if (validationAlerts.size() > 0) {
                dialogs.createMessageDialog(Dialogs.MessageType.WARNING)
                        .withContentMode(ContentMode.HTML)
                        .withCaption(messageBundle.getMessage("caption.attention"))
                        .withMessage(StringUtils.join(validationAlerts, "<br/>"))
                        .show();
            }
        }
    }


    public void setToday() {
        setWeekRange(DateTimeUtils.getFirstDayOfWeek(timeSource.currentTimestamp()));
        updateWeek();
    }

    public void showPreviousWeek() {
        setWeekRange(DateUtils.addWeeks(firstDayOfWeek, -1));
        updateWeek();
    }

    public void showNextWeek() {
        setWeekRange(DateUtils.addWeeks(firstDayOfWeek, 1));
        updateWeek();
    }

    protected void updateWeekCaption() {
        weekCaption.setValue(String.format("%s - %s",
                DateTimeUtils.getDateFormat().format(firstDayOfWeek),
                DateTimeUtils.getDateFormat().format(lastDayOfWeek)));
    }

    protected void updateDayColumnsCaptions() {
        for (Date current = firstDayOfWeek; current.getTime() <= lastDayOfWeek.getTime(); current = DateUtils.addDays(current, 1)) {
            DayOfWeek day = DayOfWeek.fromCalendarDay(DateTimeUtils.getCalendarDayOfWeek(current));
            String columnId = day.getId() + COLUMN_SUFFIX;
            weeklyTsTable.getColumn(columnId).setCaption(ScreensHelper.getColumnCaption(day.getId(), current));
        }
    }

    protected void setWeekRange(Date start) {
        firstDayOfWeek = start;
        lastDayOfWeek = DateTimeUtils.getLastDayOfWeek(firstDayOfWeek);
    }

    protected void updateWeek() {
        weeklyEntriesDc.getMutableItems().clear();
        updateWeekCaption();
        fillExistingTimeEntries();
        Table.SortInfo sortInfo = weeklyTsTable.getSortInfo();
        if (sortInfo != null) {
            weeklyTsTable.sortBy(sortInfo.getPropertyId(), sortInfo.getAscending());
        }
        updateDayColumnsCaptions();
    }

    protected void fillExistingTimeEntries() {
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(firstDayOfWeek,
                lastDayOfWeek, userSession.getCurrentOrSubstitutedUser(), null, "timeEntry-full");
        List<WeeklyReportEntry> reportEntries = weeklyReportConverter.convertFromTimeEntries(timeEntries);
        for (WeeklyReportEntry entry : reportEntries) {
            weeklyEntriesDc.getMutableItems().add(entry);
        }
    }

    protected void removeTimeEntries(List<TimeEntry> timeEntries) {
        CommitContext commitContext = new CommitContext();
        for (TimeEntry timeEntry : timeEntries) {
            if (!PersistenceHelper.isNew(timeEntry)) {
                commitContext.getRemoveInstances().add(timeEntry);
            }
        }

        dataManager.commit(commitContext);
        // work around (metadata model does not activates listeners now)
        weeklyEntriesDc.setItems(null);
    }

    @Subscribe()
    public void onBeforeClose(BeforeCloseEvent event) {
        if (hasUnsavedData()) {
            dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                    .withCaption(messages.getMainMessage("closeUnsaved.caption"))
                    .withMessage(messages.getMainMessage("closeUnsaved"))
                    .withActions(
                            new DialogAction(DialogAction.Type.OK) {
                                @Override
                                public void actionPerform(Component component) {
                                    close(event.getCloseAction());
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL)
                    )
                    .show();
            event.preventWindowClose();
        }
    }

    protected boolean hasUnsavedData() {
        for (WeeklyReportEntry reportEntry : weeklyEntriesDc.getItems()) {
            if (reportEntry.hasFilledTime() || hasNewEntries(reportEntry)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasNewEntries(WeeklyReportEntry reportEntry) {
        for (TimeEntry timeEntry : reportEntry.getExistTimeEntries()) {
            if (PersistenceHelper.isNew(timeEntry)) {
                return true;
            }
        }
        return false;
    }

    protected class CheckUnsavedAction extends BaseAction {
        public CheckUnsavedAction(String id) {
            super(id);
        }

        @Override
        public void actionPerform(Component component) {
            if (hasUnsavedData()) {
                dialogs.createOptionDialog(Dialogs.MessageType.CONFIRMATION)
                        .withCaption(messages.getMainMessage("closeUnsaved.caption"))
                        .withMessage(messageBundle.getMessage("notification.unsavedData"))
                        .withActions(
                                new DialogAction(DialogAction.Type.OK) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        doAction();
                                    }
                                },
                                new DialogAction(DialogAction.Type.CANCEL))
                        .show();
            } else {
                doAction();
            }
        }

        public void doAction() {

        }
    }
}