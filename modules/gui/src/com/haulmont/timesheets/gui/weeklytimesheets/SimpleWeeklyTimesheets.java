/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.ViewRepository;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.lang.time.DateUtils;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gorelov
 */
public class SimpleWeeklyTimesheets extends AbstractWindow {
    @Inject
    protected Table weeklyTsTable;
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
    protected Label weekLabel;
    @Inject
    protected Messages messages;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected ViewRepository viewRepository;

    protected Map<Project, Map<Task, List<TimeEntry>>> timeEntriesForWeekMap = new HashMap<>();
    protected Map<String, LookupField> projectsLookupFieldsCache = new HashMap<>();
    protected Map<String, Label> projectsLabelsCache = new HashMap<>();
    protected Map<String, LookupField> tasksLookupFieldsCache = new HashMap<>();
    protected Map<String, Label> tasksLabelsCache = new HashMap<>();
    protected Map<String, TimeField> timeFieldsCache = new HashMap<>();
    protected Map<String, EntityLinkField> linkFieldsCache = new HashMap<>();
    protected Map<String, Label> totalLabelsCache = new HashMap<>();
    protected Date firstDayOfWeek;
    protected DateFormat dateFormat;

    @Override
    public void init(Map<String, Object> params) {
        firstDayOfWeek = getFirstDayOfWeek();
        dateFormat = new SimpleDateFormat(messages.getMainMessage("dateFormat"));

        weeklyTsTable.addAction(new WeeklyReportEntryRemoveAction(weeklyTsTable));

        final String projectColumnId = "project";
        weeklyTsTable.addGeneratedColumn(projectColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                String key = getKeyForEntity(entity, projectColumnId);
                WeeklyReportEntry weeklyReportEntry = (WeeklyReportEntry) entity;
                if (weeklyReportEntry.hasTimeEntries()) {
                    if (projectsLabelsCache.containsKey(key)) {
                        return projectsLabelsCache.get(key);
                    } else {
                        Label label = componentsFactory.createComponent(Label.NAME);
                        label.setValue(weeklyReportEntry.getProject().getName());
                        projectsLabelsCache.put(key, label);
                        return label;
                    }
                } else {
                    if (projectsLookupFieldsCache.containsKey(key)) {
                        return projectsLookupFieldsCache.get(key);
                    } else {
                        @SuppressWarnings("unchecked")
                        Datasource<WeeklyReportEntry> ds = (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
                        final LookupField lookupField = componentsFactory.createComponent(LookupField.NAME);
                        lookupField.setDatasource(ds, projectColumnId);
                        lookupField.setOptionsDatasource(projectsDs);
                        lookupField.setWidth("100%");
                        projectsLookupFieldsCache.put(key, lookupField);
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
                    if (tasksLabelsCache.containsKey(key)) {
                        return tasksLabelsCache.get(key);
                    } else {
                        Label label = componentsFactory.createComponent(Label.NAME);
                        label.setValue(weeklyReportEntry.getTask().getName());
                        tasksLabelsCache.put(key, label);
                        return label;
                    }
                } else {
                    if (tasksLookupFieldsCache.containsKey(key)) {
                        return tasksLookupFieldsCache.get(key);
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
                                    lookupField.setOptionsMap(projectsService.getAssignedTasks(project, userSession.getUser()));
                                }
                            }
                        });
                        final Project project = ds.getItem().getProject();
                        if (project != null) {
                            Map<String, Object> tasks = projectsService.getAssignedTasks(project, userSession.getUser());
                            lookupField.setOptionsMap(tasks);
                        }
                        tasksLookupFieldsCache.put(key, lookupField);
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
                            TimeField timeField = componentsFactory.createComponent(TimeField.NAME);
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
                            linkField.setFrame(frame);  // TODO: remove after #PL-5371 will release
                            linkField.setDatasource(weeklyTsTable.getItemDatasource(entity), day.getId());
                            linkField.addListener(new ValueListener() {
                                @Override
                                public void valueChanged(Object source, String property, Object prevValue, Object value) {
                                    Label total = totalLabelsCache.get(getKeyForEntity(entity, totalColumnId));
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
                if (totalLabelsCache.containsKey(key)) {
                    label = totalLabelsCache.get(key);
                } else {
                    label = componentsFactory.createComponent(Label.NAME);
                    totalLabelsCache.put(key, label);
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
                // TODO: add new caches
                if (Operation.REMOVE.equals(operation) || Operation.CLEAR.equals(operation)) {
                    for (WeeklyReportEntry entry : items) {
                        tasksLookupFieldsCache.remove(getKeyForEntity(entry, taskColumnId));
                        for (final DayOfWeek day : DayOfWeek.values()) {
                            String key = getKeyForEntity(entry, day.getId());
                            timeFieldsCache.remove(key);
                            linkFieldsCache.remove(key);
                        }
                        totalLabelsCache.remove(getKeyForEntity(entry, totalColumnId));
                    }
                }
            }
        });

        updateWeek();
    }

    public void addReport() {
        weeklyEntriesDs.addItem(new WeeklyReportEntry());
    }

    public void submitAll() {
        Collection<WeeklyReportEntry> entries = weeklyEntriesDs.getItems();
        for (WeeklyReportEntry reportEntry : entries) {
            if (reportEntry.getTask() != null) {
                for (final DayOfWeek day : DayOfWeek.values()) {
                    Date time = reportEntry.getDayOfWeekTime(day);
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

    protected TimeEntry commitTimeEntry(TimeEntry timeEntry) {
        CommitContext commitContext = new CommitContext();
        commitContext.getCommitInstances().add(timeEntry);
        commitContext.getViews().put(timeEntry, viewRepository.getView(TimeEntry.class, "timeEntry-full"));

        Set<Entity> commitedEntities = dataManager.commit(commitContext);
        return commitedEntities.size() == 1 ? (TimeEntry) commitedEntities.iterator().next() : null;
    }

    protected Date getFirstDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    public void movePreviousWeek() {
        firstDayOfWeek = DateUtils.addDays(firstDayOfWeek, -7);
        updateWeek();
    }

    public void moveNextWeek() {
        firstDayOfWeek = DateUtils.addDays(firstDayOfWeek, 7);
        updateWeek();
    }

    protected void updateWeekLabel() {
        weekLabel.setValue(String.format("%s - %s",
                dateFormat.format(firstDayOfWeek),
                dateFormat.format(DateUtils.addDays(firstDayOfWeek, 6))));
    }

    protected void updateWeek() {
        weeklyEntriesDs.clear();
        timeEntriesForWeekMap.clear();
        updateWeekLabel();
        fillExistingTimeEntries();
        weeklyTsTable.repaint();
    }

    protected void fillExistingTimeEntries() {
        List<TimeEntry> timeEntries = projectsService.getTimeEntriesForPeriod(firstDayOfWeek, DateUtils.addDays(firstDayOfWeek, 6), userSession.getUser());
        for (TimeEntry timeEntry : timeEntries) {
            addTimeEntryToMap(timeEntry);
        }

        for (Project project : timeEntriesForWeekMap.keySet()) {
            for (Task task : timeEntriesForWeekMap.get(project).keySet()) {
                WeeklyReportEntry reportEntry = new WeeklyReportEntry();
                reportEntry.setProject(project);
                reportEntry.setTask(task);
                weeklyEntriesDs.addItem(reportEntry);
                for (TimeEntry timeEntry : timeEntriesForWeekMap.get(project).get(task)) {
                    reportEntry.updateTimeEntryRelativeToMondayDate(firstDayOfWeek, timeEntry);
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