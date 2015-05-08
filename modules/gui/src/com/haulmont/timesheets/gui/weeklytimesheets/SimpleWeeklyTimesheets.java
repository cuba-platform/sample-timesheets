/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.CollectionDatasourceListener;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.ValueListener;
import com.haulmont.cuba.gui.data.impl.CollectionDsListenerAdapter;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.ComponentsHelper;
import org.apache.commons.lang.time.DateUtils;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.haulmont.timesheets.entity.WeeklyReportEntry.DayOfWeek;

/**
 * @author gorelov
 */
public class SimpleWeeklyTimesheets extends AbstractWindow {
    @Inject
    protected Table weeklyTsTable;
    @Inject
    protected CollectionDatasource<WeeklyReportEntry, UUID> weeklyEntriesDs;
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
    protected Metadata metadata;
    @Inject
    protected ViewRepository viewRepository;

    protected Map<String, LookupField> lookupFieldsCache = new HashMap<>();
    protected Map<String, TimeField> timeFieldsCache = new HashMap<>();
    protected Map<String, EntityLinkField> linkFieldsCache = new HashMap<>();
    protected Map<String, Label> totalLabelsCache = new HashMap<>();
    //    protected Map<Date, List<TimeEntry>> weeklyTimeEntriesCache = new HashMap<>();
    protected Date firstDayOfWeek;
    protected DateFormat dateFormat;

    @Override
    public void init(Map<String, Object> params) {
        firstDayOfWeek = getFirstDayOfWeek();
        dateFormat = new SimpleDateFormat(messages.getMainMessage("dateFormat"));
        updateWeekLabel();
        fillExistingTimeEntries();

        final String taskColumnId = "task";
        weeklyTsTable.addAction(new ComponentsHelper.CaptionlessRemoveAction(weeklyTsTable));
        weeklyTsTable.addGeneratedColumn(taskColumnId, new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                String key = getKeyForEntity(entity, taskColumnId);
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
                                lookupField.setOptionsMap(getAssignedTasks(project));
                            }
                        }
                    });
                    final Project project = ds.getItem().getProject();
                    if (project != null) {
                        Map<String, Object> tasks = getAssignedTasks(project);
                        lookupField.setOptionsMap(tasks);
                    }
                    lookupFieldsCache.put(key, lookupField);
                    return lookupField;
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
                            linkField.setFrame(frame);
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
                if (Operation.REMOVE.equals(operation) || Operation.CLEAR.equals(operation)) {
                    for (WeeklyReportEntry entry : items) {
                        lookupFieldsCache.remove(getKeyForEntity(entry, taskColumnId));
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
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    public void movePreviousWeek() {
        firstDayOfWeek = DateUtils.addDays(firstDayOfWeek, -7);
        updateWeekLabel();
        updateWeeklyEntries();
    }

    public void moveNextWeek() {
        firstDayOfWeek = DateUtils.addDays(firstDayOfWeek, 7);
        updateWeekLabel();
        updateWeeklyEntries();
    }

    protected void updateWeekLabel() {
        weekLabel.setValue(String.format("%s - %s",
                dateFormat.format(firstDayOfWeek),
                dateFormat.format(DateUtils.addDays(firstDayOfWeek, 6))));
    }

    protected void updateWeeklyEntries() {
        weeklyEntriesDs.clear();
        fillExistingTimeEntries();
    }

    protected void fillExistingTimeEntries() {
//        List<TimeEntry> timeEntries = getTimeEntriesForPeriod(firstDayOfWeek, DateUtils.addDays(firstDayOfWeek, 6));

    }

    protected Map<String, Object> getAssignedTasks(Project project) {
        LoadContext loadContext = new LoadContext(Task.class)
                .setView("task-full");
        loadContext.setQueryString("select e from ts$Task e join e.participants p where p.user.id = :userId and e.project.id = :projectId and e.status = 10 order by e.project")
                .setParameter("projectId", project.getId())
                .setParameter("userId", userSession.getUser().getId());
        List<Task> taskList = dataManager.loadList(loadContext);
        Map<String, Object> tasksMap = new HashMap<>(taskList.size());
        for (Task task : taskList) {
            tasksMap.put(task.getName(), task);
        }
        return tasksMap;
    }

    protected String getKeyForEntity(Entity entity, String column) {
        return String.format("%s.%s", entity.getId(), column);
    }

//    protected List<TimeEntry> getTimeEntriesForPeriod(Date start, Date end) {
//        List<TimeEntry> timeEntries = weeklyTimeEntriesCache.get(start);
//        if (timeEntries == null) {
//            LoadContext loadContext = new LoadContext(TimeEntry.class)
//                    .setView("timeEntry-full");
//            loadContext.setQueryString("select e from ts$TimeEntry e where e.user.id = :userId and (e.date between :start and :end)")
//                    .setParameter("start", start)
//                    .setParameter("end", end)
//                    .setParameter("userId", userSession.getUser().getId());
//            timeEntries = dataManager.loadList(loadContext);
//            weeklyTimeEntriesCache.put(start, timeEntries);
//        }
//        return timeEntries;
//    }
}