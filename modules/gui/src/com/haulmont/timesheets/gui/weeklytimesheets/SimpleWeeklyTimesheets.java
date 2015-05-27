/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.TimeSource;
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
import com.haulmont.timesheets.global.DateTimeUtils;
import com.haulmont.timesheets.global.TimeParser;
import com.haulmont.timesheets.global.WeeklyReportConverter;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
import com.haulmont.timesheets.gui.timeentry.TimeEntryEdit;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gorelov
 */
public class SimpleWeeklyTimesheets extends AbstractWindow {
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

    protected Map<String, Label> labelsCache = new HashMap<>();
    protected Map<String, LookupField> lookupFieldsCache = new HashMap<>();
    protected Map<String, TextField> timeFieldsCache = new HashMap<>();
    protected Map<String, HBoxLayout> hBoxesCache = new HashMap<>();

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
                    weeklyReportEntry.setMondayTime(spentTimeStr);
                    weeklyReportEntry.setTuesdayTime(spentTimeStr);
                    weeklyReportEntry.setWednesdayTime(spentTimeStr);
                    weeklyReportEntry.setThursdayTime(spentTimeStr);
                    weeklyReportEntry.setFridayTime(spentTimeStr);

                    weeklyTsTable.getDatasource().addItem(weeklyReportEntry);
                }
            }
        });
    }

    protected void initWeeklyEntriesTable() {
        weeklyTsTable.addAction(new WeeklyReportEntryRemoveAction(weeklyTsTable));

        final String projectColumnId = "project";
        weeklyTsTable.addGeneratedColumn(projectColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                String key = getKeyForEntity(entity, projectColumnId);
                WeeklyReportEntry weeklyReportEntry = (WeeklyReportEntry) entity;
                if (weeklyReportEntry.hasTimeEntries()) {
                    if (labelsCache.containsKey(key)) {
                        return labelsCache.get(key);
                    } else {
                        Label label = componentsFactory.createComponent(Label.NAME);
                        label.setValue(weeklyReportEntry.getProject().getName());
                        labelsCache.put(key, label);
                        return label;
                    }
                } else {
                    if (lookupFieldsCache.containsKey(key)) {
                        return lookupFieldsCache.get(key);
                    } else {
                        @SuppressWarnings("unchecked")
                        Datasource<WeeklyReportEntry> ds =
                                (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
                        final LookupField lookupField = componentsFactory.createComponent(LookupField.NAME);
                        lookupField.setDatasource(ds, projectColumnId);
                        lookupField.setOptionsDatasource(projectsDs);
                        lookupField.setWidth("100%");
                        lookupFieldsCache.put(key, lookupField);
                        return lookupField;
                    }
                }
            }
        });

        final String taskColumnId = "task";
        weeklyTsTable.addGeneratedColumn(taskColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                String key = getKeyForEntity(entity, taskColumnId);
                WeeklyReportEntry weeklyReportEntry = (WeeklyReportEntry) entity;
                if (weeklyReportEntry.hasTimeEntries()) {
                    if (labelsCache.containsKey(key)) {
                        return labelsCache.get(key);
                    } else {
                        Label label = componentsFactory.createComponent(Label.NAME);
                        label.setValue(weeklyReportEntry.getTask().getName());
                        labelsCache.put(key, label);
                        return label;
                    }
                } else {
                    if (lookupFieldsCache.containsKey(key)) {
                        return lookupFieldsCache.get(key);
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
                        lookupFieldsCache.put(key, lookupField);
                        return lookupField;
                    }
                }
            }
        });

        final String totalColumnId = "total";

        for (Date current = firstDayOfWeek; current.getTime() <= lastDayOfWeek.getTime(); current = DateUtils.addDays(current, 1)) {
            final DayOfWeek day = DayOfWeek.fromCalendarDay(DateUtils.toCalendar(current).get(Calendar.DAY_OF_WEEK));
            final String columnId = day.getId() + "Column";
            final Date finalCurrent = current;
            weeklyTsTable.addGeneratedColumn(columnId, new Table.ColumnGenerator() {
                        @Override
                        public Component generateCell(final Entity entity) {
                            final WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                            final String key = getKeyForEntity(reportEntry, columnId);
                            List<TimeEntry> timeEntries = reportEntry.getDayOfWeekTimeEntries(day);
                            if (timeEntries == null || timeEntries.isEmpty()) {
                                if (timeFieldsCache.containsKey(key)) {
                                    return timeFieldsCache.get(key);
                                } else {
                                    TextField timeField = componentsFactory.createComponent(TextField.NAME);
                                    timeField.setWidth("100%");
                                    timeField.setHeight("22px");
                                    timeField.setDatasource(weeklyTsTable.getItemDatasource(reportEntry), day.getId() + "Time");
                                    timeFieldsCache.put(key, timeField);
                                    return timeField;
                                }
                            } else {
                                if (hBoxesCache.containsKey(key)) {
                                    return hBoxesCache.get(key);
                                } else {
                                    HBoxLayout hBox = componentsFactory.createComponent(HBoxLayout.NAME);
                                    hBox.setSpacing(true);

                                    if (timeEntries.size() == 1) {
                                        final TimeEntry timeEntry = timeEntries.get(0);
                                        final LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                                        linkButton.setCaption(reportEntry.getTotalForDay(day));
                                        linkButton.setAction(new AbstractAction("edit") {
                                            @Override
                                            public void actionPerform(Component component) {
                                                openTimeEntryEditor(timeEntry, linkButton, reportEntry, day, totalColumnId);
                                            }
                                        });

                                        hBox.add(linkButton);
                                    } else {
                                        final LinkButton linkButton = componentsFactory.createComponent(LinkButton.NAME);
                                        linkButton.setCaption(reportEntry.getTotalForDay(day));
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
                                                                    openTimeEntryEditor(timeEntry, linkButton, reportEntry, day, totalColumnId);
                                                                }
                                                            }
                                                        },
                                                        WindowManager.OpenType.DIALOG,
                                                        ParamsMap.of("date", finalCurrent, "taskId", reportEntry.getTask().getId()));
                                                updateWeek();
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
                                            projectsService.removeTimeEntries(reportEntry.getDayOfWeekTimeEntries(day));
                                            reportEntry.changeDayOfWeekTimeEntries(day, null);
                                            hBoxesCache.remove(key);
                                            weeklyTsTable.repaint();
                                        }
                                    });
                                    hBox.add(removeButton);

                                    hBoxesCache.put(key, hBox);
                                    timeFieldsCache.remove(key);

                                    return hBox;
                                }
                            }
                        }
                    }
            );
            Table.Column column = weeklyTsTable.getColumn(columnId);
            column.setWidth(80);
            column.setCaption(ComponentsHelper.getColumnCaption(day.getId(), current));
        }

        weeklyTsTable.addGeneratedColumn(totalColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                String key = getKeyForEntity(entity, totalColumnId);
                Label label;
                if (labelsCache.containsKey(key)) {
                    label = labelsCache.get(key);
                } else {
                    label = componentsFactory.createComponent(Label.NAME);
                    labelsCache.put(key, label);
                }
                label.setValue(reportEntry.getTotal());
                return label;
            }
        });
        weeklyTsTable.setColumnWidth(totalColumnId, 80);
        weeklyTsTable.setColumnCaption(totalColumnId, messages.getMessage(getClass(), "total"));

        weeklyEntriesDs.addListener(new CollectionDsListenerAdapter<WeeklyReportEntry>() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation, List<WeeklyReportEntry> items) {
                if (Operation.REMOVE.equals(operation) || Operation.CLEAR.equals(operation)) {
                    for (WeeklyReportEntry entry : items) {
                        String projectKey = getKeyForEntity(entry, projectColumnId);
                        String taskKey = getKeyForEntity(entry, taskColumnId);
                        lookupFieldsCache.remove(projectKey);
                        lookupFieldsCache.remove(taskKey);
                        labelsCache.remove(projectKey);
                        labelsCache.remove(taskKey);
                        for (final DayOfWeek day : DayOfWeek.values()) {
                            String key = getKeyForEntity(entry, day.getId());
                            timeFieldsCache.remove(key);
                            hBoxesCache.remove(key);
                        }
                        labelsCache.remove(getKeyForEntity(entry, totalColumnId));
                    }
                }
            }
        });
    }

    protected void openTimeEntryEditor(
            TimeEntry timeEntry, final LinkButton linkButton, final WeeklyReportEntry reportEntry,
            final DayOfWeek day, final String totalColumnId) {
        final TimeEntryEdit editor = openEditor(
                "ts$TimeEntry.edit", timeEntry, WindowManager.OpenType.DIALOG);
        editor.addListener(new CloseListener() {
            @Override
            public void windowClosed(String actionId) {
                if (COMMIT_ACTION_ID.equals(actionId)) {
                    TimeEntry committed = getDsContext().getDataSupplier().commit(editor.getItem());
                    reportEntry.changeDayOfWeekSingleTimeEntry(day, committed);
                    linkButton.setCaption(reportEntry.getTotalForDay(day));
                    Label total = labelsCache.get(getKeyForEntity(reportEntry, totalColumnId));
                    total.setValue(reportEntry.getTotal());
                }
            }
        });
    }

    public void addReport() {
        weeklyEntriesDs.addItem(new WeeklyReportEntry());
    }

    public void submitAll() {
        Collection<WeeklyReportEntry> entries = weeklyEntriesDs.getItems();
        for (WeeklyReportEntry reportEntry : entries) {
            if (reportEntry.getTask() != null) {
                for (final DayOfWeek day : DayOfWeek.values()) {
                    String timeStr = reportEntry.getDayOfWeekTime(day);
                    Date time = timeParser.parse(timeStr);
                    if (time != null) {
                        TimeEntry timeEntry = new TimeEntry();
                        timeEntry.setStatus(TimeEntryStatus.NEW);
                        timeEntry.setUser(userSession.getUser());
                        timeEntry.setTask(reportEntry.getTask());
                        timeEntry.setTime(time);
                        timeEntry.setTags(reportEntry.getTask().getDefaultTags());
                        timeEntry.setDate(DateUtils.addDays(firstDayOfWeek, DayOfWeek.getDayOffset(day)));
                        getDsContext().getDataSupplier().commit(timeEntry);
                    }
                }
            }
        }
        updateWeek();
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

    protected void setWeekRange(Date start) {
        firstDayOfWeek = start;
        lastDayOfWeek = DateTimeUtils.getLastDayOfWeek(firstDayOfWeek);
    }

    protected void updateWeek() {
        weeklyEntriesDs.clear();
        updateWeekCaption();
        fillExistingTimeEntries();
        weeklyTsTable.repaint();
    }

    protected void fillExistingTimeEntries() {
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(firstDayOfWeek,
                lastDayOfWeek, userSession.getUser(), null, "timeEntry-full");
        List<WeeklyReportEntry> reportEntries = reportConverterBean.convertFromTimeEntries(timeEntries);
        for (WeeklyReportEntry entry : reportEntries) {
            weeklyEntriesDs.addItem(entry);
        }
    }

    protected String getKeyForEntity(Entity entity, String column) {
        return String.format("%s.%s", entity.getId(), column);
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
                    projectsService.removeTimeEntries(entry.getExistTimeEntries());
                }
            }
            super.actionPerform(component);
        }
    }
}