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

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.CollectionDatasource.Operation;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.*;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.haulmont.timesheets.gui.util.WeeklyReportEntryAggregation;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.Calendar;

/**
 * @author gorelov
 */
@SuppressWarnings("WeakerAccess")
public class SimpleWeeklyTimesheets extends AbstractWindow {
    protected static final String COLUMN_SUFFIX = "Column";
    protected static final String TOTAL_COLUMN_ID = "totalColumn";

    @Inject
    private LinkButton setToday;
    @Inject
    private LinkButton showNextWeek;
    @Inject
    private LinkButton showPreviousWeek;
    @Inject
    private CommandLineFrameController commandLine;
    @Inject
    protected Table<WeeklyReportEntry> weeklyTsTable;
    @Inject
    protected DateField dateField;
    @Inject
    protected CollectionDatasource<WeeklyReportEntry, UUID> weeklyEntriesDs;
    @Inject
    protected CollectionDatasource<Project, UUID> projectsDs;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Label weekCaption;
    @Inject
    protected Messages messages;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected WeeklyReportConverter reportConverterBean;
    @Inject
    protected TimeParser timeParser;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected UuidSource uuidSource;
    @Inject
    protected ValidationTools validationTools;
    @Inject
    protected Metadata metadata;

    protected Map<String, Label> totalLabelsMap = new HashMap<>();

    protected Date firstDayOfWeek;
    protected Date lastDayOfWeek;

    @Override
    public void init(Map<String, Object> params) {
        setWeekRange(DateTimeUtils.getFirstDayOfWeek(timeSource.currentTimestamp()));
        weeklyTsTable.setSettingsEnabled(false);
        updateWeekCaption();
        fillExistingTimeEntries();
        initWeeklyEntriesTable();
        initDateChangeComponents();
        initCommandLine();
        updateDayColumnsCaptions();
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

        dateField.addValueChangeListener(e -> {
            if (hasUnsavedData()) {
                showOptionDialog(
                        messages.getMainMessage("closeUnsaved.caption"),
                        getMessage("notification.unsavedData"),
                        MessageType.CONFIRMATION,
                        new Action[]{
                                new DialogAction(DialogAction.Type.OK) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        setWeekRange(DateTimeUtils.getFirstDayOfWeek((Date) e.getValue()));
                                        updateWeek();
                                    }
                                },
                                new DialogAction(DialogAction.Type.CANCEL)
                        }
                );
            } else {
                setWeekRange(DateTimeUtils.getFirstDayOfWeek((Date) e.getValue()));
                updateWeek();
            }
        });
    }

    protected void initCommandLine() {
        commandLine.setTimeEntriesHandler(new CommandLineFrameController.ResultTimeEntriesHandler() {
            @Override
            public void handle(List<TimeEntry> resultTimeEntries) {
                if (CollectionUtils.isNotEmpty(resultTimeEntries)) {
                    //todo eude what if there are more than 1 entry
                    final TimeEntry timeEntry = resultTimeEntries.get(0);
                    ResultAndCause resultAndCause = validationTools.validateTimeEntry(timeEntry);
                    if (resultAndCause.isNegative) {
                        showNotification(resultAndCause.cause, NotificationType.WARNING);
                        return;
                    }

                    ResultAndCause validationResult = validationTools.validateTags(timeEntry);
                    if (validationResult.isNegative) {
                        showOptionDialog(getMessage("caption.attention"),
                                validationResult.cause + getMessage("confirmation.manuallyTagSetting"),
                                MessageType.CONFIRMATION_HTML,
                                Arrays.asList(
                                        new DialogAction(DialogAction.Type.YES) {
                                            @Override
                                            public void actionPerform(Component component) {
                                                doHandle(timeEntry);
                                            }
                                        },
                                        new DialogAction(DialogAction.Type.NO)));
                    } else {
                        doHandle(timeEntry);
                    }
                }
            }

            private void doHandle(TimeEntry timeEntry) {
                WeeklyReportEntry weeklyReportEntry = setTimeEntryToEachWeekDay(timeEntry);
                weeklyEntriesDs.addItem(weeklyReportEntry);
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
        weeklyTsTable.addAction(new WeeklyReportEntryRemoveAction(weeklyTsTable));

        initProjectColumn();
        initTaskColumn();
        initDaysColumns();
        initTotalColumn();

        weeklyEntriesDs.addCollectionChangeListener(e -> {
            if (Operation.REMOVE.equals(e.getOperation()) || Operation.CLEAR.equals(e.getOperation())) {
                for (WeeklyReportEntry entry : e.getItems()) {
                    totalLabelsMap.remove(ComponentsHelper.getCacheKeyForEntity(entry, TOTAL_COLUMN_ID));
                }
            }
        });

        weeklyTsTable.setStyleProvider(new Table.StyleProvider<WeeklyReportEntry>() {
            @Nullable
            @Override
            public String getStyleName(WeeklyReportEntry entity, String property) {
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
        });
    }

    protected void initProjectColumn() {
        final String projectColumnId = "project";
        weeklyTsTable.addGeneratedColumn(projectColumnId, entity -> {
            if (entity.hasTimeEntries()) {
                Label label = componentsFactory.createComponent(Label.class);
                label.setValue(entity.getProject().getName());
                return label;
            } else {
                @SuppressWarnings("unchecked")
                Datasource<WeeklyReportEntry> ds =
                        (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
                final LookupField lookupField = componentsFactory.createComponent(LookupField.class);
                lookupField.setDatasource(ds, projectColumnId);
                lookupField.setOptionsDatasource(projectsDs);
                lookupField.setWidth("100%");
                lookupField.setInputPrompt(messages.getMessage(WeeklyReportEntry.class, "WeeklyReportEntry.project"));
                return lookupField;
            }
        });
    }

    protected void initTaskColumn() {
        final String taskColumnId = "task";
        final String activityTypeColumnId = "activityType";
        weeklyTsTable.addGeneratedColumn(taskColumnId, entity -> {
            if (entity.hasTimeEntries()) {
                Label label = componentsFactory.createComponent(Label.class);
                String caption;
                if (entity.getActivityType() == null) {
                    caption = entity.getTask().getName();
                } else {
                    caption = String.format("%s (%s)",
                            entity.getTask().getName(),
                            entity.getActivityType().getInstanceName());
                }
                label.setValue(caption);
                return label;
            } else {
                @SuppressWarnings("unchecked")
                Datasource<WeeklyReportEntry> ds =
                        (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
                final LookupField taskLookupField = componentsFactory.createComponent(LookupField.class);
                taskLookupField.setDatasource(ds, taskColumnId);
                taskLookupField.setWidth("100%");
                taskLookupField.setInputPrompt(messages.getMessage(WeeklyReportEntry.class, "WeeklyReportEntry.task"));

                final LookupField activityTypeLookupField = componentsFactory.createComponent(LookupField.class);
                activityTypeLookupField.setDatasource(ds, activityTypeColumnId);
                activityTypeLookupField.setWidth("100%");
                activityTypeLookupField.setRequired(true);
                activityTypeLookupField.setInputPrompt(messages.getMessage(WeeklyReportEntry.class, "WeeklyReportEntry.activityType"));
                setActivityTypeVisibility(ds.getItem().getProject(), activityTypeLookupField);

                ds.addItemPropertyChangeListener(e -> {
                    if ("project".equals(e.getProperty())) {
                        Project project = (Project) e.getValue();
                        taskLookupField.setValue(null);
                        Map<String, Object> tasks = getTasksForCurrentUserAndProject(project);
                        taskLookupField.setOptionsMap(tasks);
                        setActivityTypeVisibility(project, activityTypeLookupField);
                    }
                });

                Project project = ds.getItem().getProject();
                if (project != null) {
                    Map<String, Object> tasks = getTasksForCurrentUserAndProject(project);
                    taskLookupField.setOptionsMap(tasks);
                }

                BoxLayout boxLayout = componentsFactory.createComponent(HBoxLayout.class);
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
            final DayOfWeek day = DayOfWeek.fromCalendarDay(DateUtils.toCalendar(current).get(Calendar.DAY_OF_WEEK));
            final String columnId = day.getId() + COLUMN_SUFFIX;
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
                            HBoxLayout hBox = componentsFactory.createComponent(HBoxLayout.class);
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
                            LinkButton removeButton = componentsFactory.createComponent(LinkButton.class);
                            removeButton.setIcon("icons/remove.png");
                            removeButton.setAlignment(Alignment.MIDDLE_RIGHT);
                            removeButton.setAction(new ComponentsHelper.CustomRemoveAction("timeEntryRemove", getFrame()) {
                                @Override
                                protected void doRemove() {
                                    removeTimeEntries(reportEntry.getDayOfWeekTimeEntries(day));
                                    reportEntry.changeDayOfWeekTimeEntries(day, null);
                                    weeklyTsTable.repaint();
                                }
                            });
                            hBox.add(removeButton);
                        }

                        private void createLinkToMultipleTimeEntries(final WeeklyReportEntry reportEntry, HBoxLayout hBox, final Date date) {
                            final LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                            linkButton.setCaption(reportEntry.getTotalForDay(day).toString());
                            linkButton.setAction(new AbstractAction("edit") {

                                @Override
                                public void actionPerform(Component component) {
                                    Window window = openWindow(
                                            "ts$TimeEntry.lookup",
                                            WindowManager.OpenType.DIALOG,
                                            ParamsMap.of("date", date,
                                                    "task", reportEntry.getTask(),
                                                    "activityType", reportEntry.getActivityType(),
                                                    "user", userSession.getCurrentOrSubstitutedUser()));
                                    window.addCloseListener(actionId -> updateWeek());
                                }
                            });
                            hBox.add(linkButton);
                        }

                        private void createLinkToSingleTimeEntry(final WeeklyReportEntry reportEntry, List<TimeEntry> timeEntries, HBoxLayout hBox) {
                            final TimeEntry timeEntry = timeEntries.get(0);
                            final LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
                            linkButton.setCaption(reportEntry.getTotalForDay(day).toString());
                            linkButton.setAction(new AbstractAction("edit") {
                                @Override
                                public void actionPerform(Component component) {
                                    openTimeEntryEditor(timeEntry);
                                }
                            });

                            hBox.add(linkButton);
                        }

                        private Component createTextFieldForTimeInput(WeeklyReportEntry reportEntry) {
                            TextField timeField = componentsFactory.createComponent(TextField.class);
                            timeField.setWidth("100%");
                            timeField.setHeight("22px");
                            timeField.setDatasource(weeklyTsTable.getItemDatasource(reportEntry), day.getId() + "Time");
                            return timeField;
                        }
                    }
            );
            weeklyTsTable.setColumnWidth(columnId, 80);

            Table.Column column = weeklyTsTable.getColumn(columnId);
            column.setAggregation(ComponentsHelper.createAggregationInfo(
                    projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, day.getId()),
                    new WeeklyReportEntryAggregation()
            ));
        }
    }

    protected void initTotalColumn() {
        weeklyTsTable.addGeneratedColumn(TOTAL_COLUMN_ID, entity -> {
            Label label = componentsFactory.createComponent(Label.class);
            label.setValue(entity.getTotal());
            totalLabelsMap.put(ComponentsHelper.getCacheKeyForEntity(entity, TOTAL_COLUMN_ID), label);
            return label;
        });
        weeklyTsTable.setColumnWidth(TOTAL_COLUMN_ID, 80);
        weeklyTsTable.setColumnCaption(TOTAL_COLUMN_ID, messages.getMessage(getClass(), "total"));

        Table.Column column = weeklyTsTable.getColumn(TOTAL_COLUMN_ID);
        column.setAggregation(ComponentsHelper.createAggregationInfo(
                projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, "total"),
                new TotalColumnAggregation()
        ));
    }

    protected void openTimeEntryEditor(TimeEntry timeEntry) {
        final TimeEntryEdit editor = (TimeEntryEdit) openEditor(
                "ts$TimeEntry.edit", timeEntry, WindowManager.OpenType.DIALOG);
        editor.addListener(actionId -> {
            if (COMMIT_ACTION_ID.equals(actionId)) {
                updateWeek();
            }
        });
    }

    public void addReport() {
        WeeklyReportEntry item = new WeeklyReportEntry();
        weeklyEntriesDs.addItem(item);
        weeklyTsTable.setSelected(item);
        weeklyTsTable.requestFocus();
    }

    public void submitAll() {
        Collection<WeeklyReportEntry> entries = weeklyEntriesDs.getItems();
        if (!entries.isEmpty()) {
            CommitContext commitContext = new CommitContext();
            List<String> validationAlerts = new ArrayList<>();
            for (WeeklyReportEntry weeklyReportEntry : entries) {
                ResultAndCause resultAndCause = validationTools.validateWeeklyReport(weeklyReportEntry);
                if (resultAndCause.isNegative) {
                    showNotification(resultAndCause.cause, NotificationType.WARNING);
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
                            validationAlerts.add(formatMessage("notification.timeEntryValidation",
                                    validationResult.cause, timeEntry.getTask().getName(),
                                    timeEntry.getDate(), HoursAndMinutes.fromTimeEntry(timeEntry)));
                        }

                        commitContext.getCommitInstances().add(timeEntry);
                    }
                }
            }

            getDsContext().getDataSupplier().commit(commitContext);
            updateWeek();

            if (validationAlerts.size() > 0) {
                showMessageDialog(getMessage("caption.attention"), StringUtils.join(validationAlerts, "<br/>"), MessageType.WARNING_HTML);
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
            weeklyTsTable.setColumnCaption(columnId, ComponentsHelper.getColumnCaption(day.getId(), current));
        }
    }

    protected void setWeekRange(Date start) {
        firstDayOfWeek = start;
        lastDayOfWeek = DateTimeUtils.getLastDayOfWeek(firstDayOfWeek);
    }

    protected void updateWeek() {
        weeklyEntriesDs.clear();
        updateWeekCaption();
        fillExistingTimeEntries();
        weeklyTsTable.repaint();
        updateDayColumnsCaptions();
    }

    protected void fillExistingTimeEntries() {
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(firstDayOfWeek,
                lastDayOfWeek, userSession.getCurrentOrSubstitutedUser(), null, "timeEntry-full");
        List<WeeklyReportEntry> reportEntries = reportConverterBean.convertFromTimeEntries(timeEntries);
        for (WeeklyReportEntry entry : reportEntries) {
            weeklyEntriesDs.addItem(entry);
        }
    }

    protected void removeTimeEntries(List<TimeEntry> timeEntries) {
        CommitContext commitContext = new CommitContext();
        for (TimeEntry timeEntry : timeEntries) {
            if (!PersistenceHelper.isNew(timeEntry)) {
                commitContext.getRemoveInstances().add(timeEntry);
            }
        }

        getDsContext().getDataSupplier().commit(commitContext);
    }

    protected class WeeklyReportEntryRemoveAction extends ComponentsHelper.CaptionlessRemoveAction {

        public WeeklyReportEntryRemoveAction(ListComponent target) {
            super(target);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerform(Component component) {
            Set<WeeklyReportEntry> entries = target.getSelected();
            if (!entries.isEmpty()) {
                for (WeeklyReportEntry entry : entries) {
                    removeTimeEntries(entry.getExistTimeEntries());
                }
            }
            super.actionPerform(component);
        }
    }

    @Override
    protected boolean preClose(final String actionId) {
        if (hasUnsavedData()) {
            showOptionDialog(
                    messages.getMainMessage("closeUnsaved.caption"),
                    messages.getMainMessage("closeUnsaved"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.OK) {
                                @Override
                                public void actionPerform(Component component) {
                                    close(actionId, true);
                                }
                            },
                            new DialogAction(DialogAction.Type.CANCEL)
                    }
            );
            return false;
        }
        return true;
    }

    protected boolean hasUnsavedData() {
        for (WeeklyReportEntry reportEntry : weeklyEntriesDs.getItems()) {
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

    protected class CheckUnsavedAction extends AbstractAction {
        public CheckUnsavedAction(String id) {
            super(id);
        }

        @Override
        public void actionPerform(Component component) {
            if (hasUnsavedData()) {
                showOptionDialog(
                        messages.getMainMessage("closeUnsaved.caption"),
                        getMessage("notification.unsavedData"),
                        MessageType.CONFIRMATION,
                        new Action[]{
                                new DialogAction(DialogAction.Type.OK) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        doAction();
                                    }
                                },
                                new DialogAction(DialogAction.Type.CANCEL)
                        }
                );
            } else {
                doAction();
            }
        }

        public void doAction() {

        }
    }
}