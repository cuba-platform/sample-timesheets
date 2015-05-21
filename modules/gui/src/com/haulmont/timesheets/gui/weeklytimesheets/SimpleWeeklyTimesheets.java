/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.ViewRepository;
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
import com.haulmont.timesheets.global.TimeUtils;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.gui.commandline.CommandLineFrameController;
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
    protected DataManager dataManager;
    @Inject
    protected UserSession userSession;
    @Inject
    protected Label weekCaption;
    @Inject
    protected Messages messages;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected ViewRepository viewRepository;

    protected Map<Project, Map<Task, List<TimeEntry>>> timeEntriesForWeekMap = new HashMap<>();

    protected Map<String, Label> labelsCache = new HashMap<>();
    protected Map<String, LookupField> lookupFieldsCache = new HashMap<>();
    protected Map<String, TextField> timeFieldsCache = new HashMap<>();
    protected Map<String, EntityLinkField> linkFieldsCache = new HashMap<>();

    protected Date firstDayOfWeek;

    @Override
    public void init(Map<String, Object> params) {
        firstDayOfWeek = TimeUtils.getFirstDayOfWeek(new Date());

        initWeeklyEntriesTable();

        initDateField();

        updateWeek();

        initCommandLine();
    }

    protected void initDateField() {
        dateField.addListener(new ValueListener() {
            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                firstDayOfWeek = TimeUtils.getFirstDayOfWeek((Date) value);
                updateWeek();
            }
        });
    }

    protected void initCommandLine() {
        commandLine.setTimeEntriesHandler(new CommandLineFrameController.ResultTimeEntriesHandler() {
            @Override
            public void handle(List<TimeEntry> resultTimeEntries) {
                if (CollectionUtils.isNotEmpty(resultTimeEntries)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.TIME_FORMAT);
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
                        Datasource<WeeklyReportEntry> ds = (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
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
                        Datasource<WeeklyReportEntry> ds = (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
                        final LookupField lookupField = componentsFactory.createComponent(LookupField.NAME);
                        lookupField.setDatasource(ds, taskColumnId);
                        lookupField.setWidth("100%");

                        ds.addListener(new DsListenerAdapter<WeeklyReportEntry>() {
                            @Override
                            public void valueChanged(WeeklyReportEntry source, String property, Object prevValue, Object value) {
                                if ("project".equals(property)) {
                                    Project project = (Project) value;
                                    lookupField.setValue(null);
                                    Map<String, Task> tasks = projectsService.getActiveTasksForUserAndProject(userSession.getUser(), project);
                                    lookupField.setOptionsMap((Map) tasks);
                                }
                            }
                        });
                        final Project project = ds.getItem().getProject();
                        if (project != null) {
                            Map<String, Task> tasks = projectsService.getActiveTasksForUserAndProject(userSession.getUser(), project);
                            lookupField.setOptionsMap((Map) tasks);
                        }
                        lookupFieldsCache.put(key, lookupField);
                        return lookupField;
                    }
                }
            }
        });

        final String totalColumnId = "total";
        for (final DayOfWeek day : DayOfWeek.values()) {
            weeklyTsTable.addGeneratedColumn(day.getId(), new Table.ColumnGenerator() {
                @Override
                public Component generateCell(final Entity entity) {
                    final WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                    final String key = getKeyForEntity(entity, day.getId());
                    if (reportEntry.getDayOfWeekTimeEntry(day) == null) {
                        if (timeFieldsCache.containsKey(key)) {
                            return timeFieldsCache.get(key);
                        } else {
                            TextField timeField = componentsFactory.createComponent(TextField.NAME);
                            timeField.setWidth("100%");
                            timeField.setDatasource(weeklyTsTable.getItemDatasource(entity), day.getId() + "Time");
                            timeFieldsCache.put(key, timeField);
                            return timeField;
                        }
                    } else {
                        if (linkFieldsCache.containsKey(key)) {
                            return linkFieldsCache.get(key);
                        } else {
                            EntityLinkField linkField = componentsFactory.createComponent(EntityLinkField.NAME);
                            linkField.setOwner(weeklyTsTable);
                            linkField.setScreenOpenType(WindowManager.OpenType.DIALOG);
                            linkField.setDatasource(weeklyTsTable.getItemDatasource(entity), day.getId());
                            linkField.addListener(new ValueListener() {
                                @Override
                                public void valueChanged(Object source, String property, Object prevValue, Object value) {
                                    Label total = labelsCache.get(getKeyForEntity(entity, totalColumnId));
                                    total.setValue(reportEntry.getTotal());
                                }
                            });
                            linkFieldsCache.put(key, linkField);
                            timeFieldsCache.remove(key);
                            return linkField;
                        }
                    }
                }
            });
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
                            linkFieldsCache.remove(key);
                        }
                        labelsCache.remove(getKeyForEntity(entry, totalColumnId));
                    }
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
                    Date time = TimeUtils.parse(timeStr);
                    if (time != null) {
                        TimeEntry timeEntry = new TimeEntry();
                        timeEntry.setStatus(TimeEntryStatus.NEW);
                        timeEntry.setUser(userSession.getUser());
                        timeEntry.setTask(reportEntry.getTask());
                        timeEntry.setTime(time);
                        timeEntry.setTags(reportEntry.getTask().getDefaultTags());
                        timeEntry.setDate(DateUtils.addDays(firstDayOfWeek, DayOfWeek.getDayOffset(day)));

                        reportEntry.changeDayOfWeekTimeEntry(day, commitTimeEntry(timeEntry));
                    }
                }
            }
        }
        weeklyTsTable.repaint();
    }

    public void setCurrentWeek() {
        firstDayOfWeek = TimeUtils.getFirstDayOfWeek(new Date());
        updateWeek();
    }

    protected TimeEntry commitTimeEntry(TimeEntry timeEntry) {
        CommitContext commitContext = new CommitContext();
        commitContext.getCommitInstances().add(timeEntry);
        commitContext.getViews().put(timeEntry, viewRepository.getView(TimeEntry.class, "timeEntry-full"));

        Set<Entity> commitedEntities = dataManager.commit(commitContext);
        return commitedEntities.size() == 1 ? (TimeEntry) commitedEntities.iterator().next() : null;
    }

    public void movePreviousWeek() {
        firstDayOfWeek = DateUtils.addDays(firstDayOfWeek, -7);
        updateWeek();
    }

    public void moveNextWeek() {
        firstDayOfWeek = DateUtils.addDays(firstDayOfWeek, 7);
        updateWeek();
    }

    protected void updateWeekCaption() {
        weekCaption.setValue(String.format("%s - %s",
                TimeUtils.getDateFormat().format(firstDayOfWeek),
                TimeUtils.getDateFormat().format(DateUtils.addDays(firstDayOfWeek, 6))));
    }

    protected void updateWeek() {
        weeklyEntriesDs.clear();
        timeEntriesForWeekMap.clear();
        updateWeekCaption();
        fillExistingTimeEntries();
        weeklyTsTable.repaint();
    }

    protected void fillExistingTimeEntries() {
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(firstDayOfWeek,
                DateUtils.addDays(firstDayOfWeek, 6), userSession.getUser(), null);
        for (TimeEntry timeEntry : timeEntries) {
            addTimeEntryToMap(timeEntry);
        }

        for (Map.Entry<Project, Map<Task, List<TimeEntry>>> projectEntry : timeEntriesForWeekMap.entrySet()) {
            for (Map.Entry<Task, List<TimeEntry>> taskEntry : projectEntry.getValue().entrySet()) {
                WeeklyReportEntry reportEntry = new WeeklyReportEntry();
                reportEntry.setProject(projectEntry.getKey());
                reportEntry.setTask(taskEntry.getKey());
                weeklyEntriesDs.addItem(reportEntry);
                for (TimeEntry timeEntry : taskEntry.getValue()) {
                    reportEntry.updateTimeEntry(timeEntry);
                }
            }
        }
    }

    protected void addTimeEntryToMap(TimeEntry timeEntry) {
        Project project = timeEntry.getTask().getProject();
        Task task = timeEntry.getTask();
        Map<Task, List<TimeEntry>> taskMap = timeEntriesForWeekMap.get(project);
        if (taskMap == null) {
            taskMap = new HashMap<>();
            timeEntriesForWeekMap.put(project, taskMap);
        }

        List<TimeEntry> timeEntryList = taskMap.get(task);
        if (timeEntryList == null) {
            timeEntryList = new ArrayList<>();
            taskMap.put(task, timeEntryList);
        }
        timeEntryList.add(timeEntry);
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
            WeeklyReportEntry entry = target.getSingleSelected();
            if (entry != null) {
                projectsService.removeTimeEntries(entry.getExistTimeEntries());
            }
            super.actionPerform(component);
        }
    }
}