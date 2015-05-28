/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.utils.InstanceUtils;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.global.*;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gorelov
 */
public class SimpleWeeklyTimesheets extends AbstractWindow {

    protected static final String COLUMN_SUFFIX = "Column";
    protected static final String TOTAL_COLUMN_ID = "totalColumn";

    @Inject
    private CommandLineFrameController commandLine;
    @Inject
    protected Table weeklyTsTable;
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

    protected Map<String, Label> totalLabelsMap = new HashMap<>();

    protected Date firstDayOfWeek;
    protected Date lastDayOfWeek;

    @Override
    public void init(Map<String, Object> params) {
        setWeekRange(DateTimeUtils.getFirstDayOfWeek(timeSource.currentTimestamp()));

        updateWeekCaption();
        fillExistingTimeEntries();
        initWeeklyEntriesTable();
        initDateField();
        initCommandLine();
        updateDayColumnsCaptions();
    }

    protected void initDateField() {
        dateField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                setWeekRange(DateTimeUtils.getFirstDayOfWeek((Date) value));
                updateWeek();
            }
        });
    }

    protected void initCommandLine() {
        commandLine.setTimeEntriesHandler(new CommandLineFrameController.ResultTimeEntriesHandler() {
            @Override
            public void handle(List<TimeEntry> resultTimeEntries) {
                if (CollectionUtils.isNotEmpty(resultTimeEntries)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeUtils.TIME_FORMAT);
                    TimeEntry timeEntry = resultTimeEntries.get(0);

                    //todo eude what if there are more than 1 entry
                    String spentTimeStr = simpleDateFormat.format(timeEntry.getTime());

                    WeeklyReportEntry weeklyReportEntry = new WeeklyReportEntry();
                    weeklyReportEntry.setTask(timeEntry.getTask());
                    weeklyReportEntry.setProject(timeEntry.getTask().getProject());

                    weeklyReportEntry.setMonday(copyToList(timeEntry));
                    weeklyReportEntry.setMondayTime(spentTimeStr);

                    weeklyReportEntry.setTuesday(copyToList(timeEntry));
                    weeklyReportEntry.setTuesdayTime(spentTimeStr);

                    weeklyReportEntry.setWednesday(copyToList(timeEntry));
                    weeklyReportEntry.setWednesdayTime(spentTimeStr);

                    weeklyReportEntry.setThursday(copyToList(timeEntry));
                    weeklyReportEntry.setThursdayTime(spentTimeStr);

                    weeklyReportEntry.setFriday(copyToList(timeEntry));
                    weeklyReportEntry.setFridayTime(spentTimeStr);

                    weeklyTsTable.getDatasource().addItem(weeklyReportEntry);
                }
            }

            private List<TimeEntry> copyToList(TimeEntry timeEntry) {
                List<TimeEntry> timeEntries = new ArrayList<>();
                timeEntries.add(doCopy(timeEntry));
                return timeEntries;
            }

            private TimeEntry doCopy(TimeEntry timeEntry) {
                TimeEntry copy = (TimeEntry) InstanceUtils.copy(timeEntry);
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

        weeklyEntriesDs.addListener(new CollectionDsListenerAdapter<WeeklyReportEntry>() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation, List<WeeklyReportEntry> items) {
                if (Operation.REMOVE.equals(operation) || Operation.CLEAR.equals(operation)) {
                    for (WeeklyReportEntry entry : items) {
                        totalLabelsMap.remove(ComponentsHelper.getCacheKeyForEntity(entry, TOTAL_COLUMN_ID));
                    }
                }
            }
        });

        weeklyTsTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                String id = null;
                if (property != null && property.endsWith(COLUMN_SUFFIX)) {
                    id = property.replace(COLUMN_SUFFIX, "");
                }
                DayOfWeek day = DayOfWeek.fromId(id != null ? id : property);
                if (entity == null) {
                    if (day != null) {
                        return validationTools.isWorkTimeMatchToPlanForDay(
                                DateTimeUtils.getSpecificDayOfWeek(firstDayOfWeek, day.getJavaCalendarDay()),
                                userSession.getUser()) ? null : "overtime";
                    } else if (TOTAL_COLUMN_ID.equals(property)) {
                        return validationTools.isWorkTimeMatchToPlanForWeek(
                                firstDayOfWeek, userSession.getUser()) ? null : "overtime";
                    }
                }
                return null;
            }
        });
    }

    protected void initProjectColumn() {
        final String projectColumnId = "project";
        weeklyTsTable.addGeneratedColumn(projectColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry weeklyReportEntry = (WeeklyReportEntry) entity;
                if (weeklyReportEntry.hasTimeEntries()) {
                    Label label = componentsFactory.createComponent(Label.NAME);
                    label.setValue(weeklyReportEntry.getProject().getName());
                    return label;
                } else {
                    @SuppressWarnings("unchecked")
                    Datasource<WeeklyReportEntry> ds =
                            (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
                    final LookupField lookupField = componentsFactory.createComponent(LookupField.NAME);
                    lookupField.setDatasource(ds, projectColumnId);
                    lookupField.setOptionsDatasource(projectsDs);
                    lookupField.setWidth("100%");
                    return lookupField;
                }
            }
        });
    }

    protected void initTaskColumn() {
        final String taskColumnId = "task";
        weeklyTsTable.addGeneratedColumn(taskColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry weeklyReportEntry = (WeeklyReportEntry) entity;
                if (weeklyReportEntry.hasTimeEntries()) {
                    Label label = componentsFactory.createComponent(Label.NAME);
                    label.setValue(weeklyReportEntry.getTask().getName());
                    return label;
                } else {
                    @SuppressWarnings("unchecked")
                    Datasource<WeeklyReportEntry> ds =
                            (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
                    final LookupField lookupField = componentsFactory.createComponent(LookupField.NAME);
                    lookupField.setDatasource(ds, taskColumnId);
                    lookupField.setWidth("100%");

                    ds.addListener(new DsListenerAdapter<WeeklyReportEntry>() {
                        @Override
                        public void valueChanged(WeeklyReportEntry source, String property, Object prevValue, Object value) {
                            if ("project".equals(property)) {
                                Project project = (Project) value;
                                lookupField.setValue(null);
                                Map<String, Task> tasks =
                                        projectsService.getActiveTasksForUserAndProject(
                                                userSession.getUser(),
                                                project,
                                                "task-full");
                                lookupField.setOptionsMap((Map) tasks);
                            }
                        }
                    });
                    final Project project = ds.getItem().getProject();
                    if (project != null) {
                        Map<String, Task> tasks =
                                projectsService.getActiveTasksForUserAndProject(
                                        userSession.getUser(),
                                        project,
                                        "task-full");
                        lookupField.setOptionsMap((Map) tasks);
                    }
                    return lookupField;
                }
            }
        });
    }

    protected void initDaysColumns() {
        for (Date current = firstDayOfWeek; current.getTime() <= lastDayOfWeek.getTime(); current = DateUtils.addDays(current, 1)) {
            final DayOfWeek day = DayOfWeek.fromCalendarDay(DateUtils.toCalendar(current).get(Calendar.DAY_OF_WEEK));
            final String columnId = day.getId() + COLUMN_SUFFIX;
            final Date finalCurrent = current;
            weeklyTsTable.addGeneratedColumn(columnId, new Table.ColumnGenerator() {
                        @Override
                        public Component generateCell(final Entity entity) {
                            final WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                            List<TimeEntry> timeEntries = reportEntry.getDayOfWeekTimeEntries(day);
                            if (CollectionUtils.isEmpty(timeEntries)
                                    || timeEntries.size() == 1 && PersistenceHelper.isNew(timeEntries.get(0))) {
                                TextField timeField = componentsFactory.createComponent(TextField.NAME);
                                timeField.setWidth("100%");
                                timeField.setHeight("22px");
                                timeField.setDatasource(weeklyTsTable.getItemDatasource(reportEntry), day.getId() + "Time");
                                return timeField;
                            } else {
                                HBoxLayout hBox = componentsFactory.createComponent(HBoxLayout.NAME);
                                hBox.setSpacing(true);

                                if (timeEntries.size() == 1) {
                                    final TimeEntry timeEntry = timeEntries.get(0);
                                    final LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                                    linkButton.setCaption(StringFormatHelper.getDayHoursString(reportEntry.getTotalForDay(day)));
                                    linkButton.setAction(new AbstractAction("edit") {
                                        @Override
                                        public void actionPerform(Component component) {
                                            openTimeEntryEditor(timeEntry, linkButton, reportEntry, day);
                                        }
                                    });

                                    hBox.add(linkButton);
                                } else {
                                    final LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                                    linkButton.setCaption(StringFormatHelper.getDayHoursString(reportEntry.getTotalForDay(day)));
                                    linkButton.setAction(new AbstractAction("edit") {

                                        @Override
                                        public void actionPerform(Component component) {
                                            openLookup(
                                                    "ts$TimeEntry.lookup",
                                                    new Lookup.Handler() {
                                                        @Override
                                                        public void handleLookup(Collection items) {
                                                            if (CollectionUtils.isNotEmpty(items)) {
                                                                TimeEntry timeEntry = (TimeEntry) items.iterator().next();
                                                                openTimeEntryEditor(timeEntry, linkButton, reportEntry, day);
                                                            }
                                                        }
                                                    },
                                                    WindowManager.OpenType.DIALOG,
                                                    ParamsMap.of("date", finalCurrent,
                                                            "taskId", reportEntry.getTask().getId(),
                                                            "userId", userSession.getUser().getId()));
                                        }
                                    });
                                    hBox.add(linkButton);
                                }

                                LinkButton removeButton = componentsFactory.createComponent(LinkButton.NAME);
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

                                return hBox;
                            }
                        }
                    }
            );
            weeklyTsTable.setColumnWidth(columnId, 80);

            Table.Column column = weeklyTsTable.getColumn(columnId);
            column.setAggregation(ComponentsHelper.createAggregationInfo(
                    projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, day.getId()),
                    new TimeEntryAggregation()
            ));
        }
    }

    protected void initTotalColumn() {
        weeklyTsTable.addGeneratedColumn(TOTAL_COLUMN_ID, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                Label label = componentsFactory.createComponent(Label.NAME);
                label.setValue(reportEntry.getTotal());
                totalLabelsMap.put(ComponentsHelper.getCacheKeyForEntity(reportEntry, TOTAL_COLUMN_ID), label);
                return label;
            }
        });
        weeklyTsTable.setColumnWidth(TOTAL_COLUMN_ID, 80);
        weeklyTsTable.setColumnCaption(TOTAL_COLUMN_ID, messages.getMessage(getClass(), "total"));

        Table.Column column = weeklyTsTable.getColumn(TOTAL_COLUMN_ID);
        column.setAggregation(ComponentsHelper.createAggregationInfo(
                projectsService.getEntityMetaPropertyPath(WeeklyReportEntry.class, "total"),
                new TotalColumnAggregation()
        ));
    }

    protected void openTimeEntryEditor(
            TimeEntry timeEntry, final LinkButton linkButton, final WeeklyReportEntry reportEntry, final DayOfWeek day) {
        final TimeEntryEdit editor = openEditor(
                "ts$TimeEntry.edit", timeEntry, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (COMMIT_ACTION_ID.equals(actionId)) {
                    TimeEntry committed = getDsContext().getDataSupplier().commit(editor.getItem());
                    reportEntry.changeDayOfWeekSingleTimeEntry(day, committed);
                    linkButton.setCaption(StringFormatHelper.getDayHoursString(reportEntry.getTotalForDay(day)));
                    Label totalLabel = totalLabelsMap.get(ComponentsHelper.getCacheKeyForEntity(reportEntry, TOTAL_COLUMN_ID));
                    totalLabel.setValue(reportEntry.getTotal());
                }
            }
        });
    }

    public void addReport() {
        weeklyEntriesDs.addItem(new WeeklyReportEntry());
    }

    public void submitAll() {
        Collection<WeeklyReportEntry> entries = weeklyEntriesDs.getItems();
        if (!entries.isEmpty()) {
            CommitContext commitContext = new CommitContext();
            for (WeeklyReportEntry weeklyReportEntry : entries) {
                if (weeklyReportEntry.getTask() != null) {
                    for (final DayOfWeek day : DayOfWeek.values()) {
                        String timeStr = weeklyReportEntry.getDayOfWeekTime(day);
                        Date time = timeParser.parse(timeStr);
                        if (time != null) {
                            List<TimeEntry> alreadyCreatedEntries = weeklyReportEntry.getDayOfWeekTimeEntries(day);
                            Set<Tag> defaultTags = weeklyReportEntry.getTask().getDefaultTags();

                            TimeEntry timeEntry = alreadyCreatedEntries != null
                                    ? alreadyCreatedEntries.get(0)
                                    : new TimeEntry();
                            timeEntry.setStatus(TimeEntryStatus.NEW);
                            timeEntry.setUser(userSession.getUser());
                            timeEntry.setTask(weeklyReportEntry.getTask());
                            timeEntry.setTime(time);
                            if (CollectionUtils.isNotEmpty(timeEntry.getTags())) {
                                HashSet<Tag> tags = new HashSet<>(timeEntry.getTags());
                                if (defaultTags != null) {
                                    tags.addAll(defaultTags);
                                }
                                timeEntry.setTags(tags);
                            } else {
                                timeEntry.setTags(defaultTags);
                            }
                            timeEntry.setDate(DateTimeUtils.getSpecificDayOfWeek(firstDayOfWeek, day.getJavaCalendarDay()));

                            commitContext.getCommitInstances().add(timeEntry);
                        }
                    }
                }
            }

            getDsContext().getDataSupplier().commit(commitContext);
            updateWeek();
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
                lastDayOfWeek, userSession.getUser(), null, "timeEntry-full");
        List<WeeklyReportEntry> reportEntries = reportConverterBean.convertFromTimeEntries(timeEntries);
        for (WeeklyReportEntry entry : reportEntries) {
            weeklyEntriesDs.addItem(entry);
        }
    }

    protected void removeTimeEntries(List<TimeEntry> timeEntries) {
        CommitContext commitContext = new CommitContext();
        commitContext.getRemoveInstances().addAll(timeEntries);
        getDsContext().getDataSupplier().commit(commitContext);
    }

    protected class WeeklyReportEntryRemoveAction extends ComponentsHelper.CaptionlessRemoveAction {

        public WeeklyReportEntryRemoveAction(ListComponent target) {
            super(target);
        }

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
}