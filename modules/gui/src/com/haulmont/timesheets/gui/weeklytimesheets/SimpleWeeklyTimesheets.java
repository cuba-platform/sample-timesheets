/*
 * Copyright (c) 2015 com.haulmont.timesheets.gui
 */
package com.haulmont.timesheets.gui.weeklytimesheets;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.DayOfWeek;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.WeeklyReportEntry;
import com.haulmont.timesheets.gui.ComponentsHelper;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    protected Map<Project, List<Task>> lookupFieldsOptionsLists = new HashMap<>();

    @Override
    public void init(Map<String, Object> params) {
        weeklyTsTable.addAction(new ComponentsHelper.CaptionlessRemoveAction(weeklyTsTable));

        weeklyTsTable.addGeneratedColumn("task", new Table.ColumnGenerator() {
            @Override
            public Component generateCell(Entity entity) {
                @SuppressWarnings("unchecked")
                Datasource<WeeklyReportEntry> ds = (Datasource<WeeklyReportEntry>) weeklyTsTable.getItemDatasource(entity);
                final LookupField lookupField = componentsFactory.createComponent(LookupField.NAME);
                lookupField.setDatasource(ds, "task");
                lookupField.setWidth("100%");

                ds.addListener(new DsListenerAdapter<WeeklyReportEntry>() {
                    @Override
                    public void valueChanged(WeeklyReportEntry source, String property, Object prevValue, Object value) {
                        if ("project".equals(property)) {
                            Project project = (Project) value;
                            lookupField.setValue(null);
                            lookupField.setOptionsList(getAssignedTasks(project));
                        }
                    }
                });

                final Project project = ds.getItem().getProject();
                if (project != null) {
                    List<Task> tasks = getAssignedTasks(project);
                    lookupField.setOptionsList(tasks);
                    Task task = ds.getItem().getTask();
                    if (task != null && tasks.contains(task)) {
                        lookupField.setValue(task);
                    }
                }
                return lookupField;
            }
        });

        for (final DayOfWeek day : DayOfWeek.values()) {
            weeklyTsTable.addGeneratedColumn(day.getId() + "Time", new Table.ColumnGenerator() {
                @Override
                public Component generateCell(Entity entity) {
                    WeeklyReportEntry reportEntry = (WeeklyReportEntry) entity;
                    if (reportEntry.getDayOfWeekTimeEntry(day) == null) {
                        TimeField timeField = componentsFactory.createComponent(TimeField.NAME);
                        timeField.setDatasource(weeklyTsTable.getItemDatasource(entity), day.getId() + "Time");
                        return timeField;
                    } else {
                        return null;
                    }
                }
            });
            weeklyTsTable.setColumnWidth(day.getId() + "Time", 80);
        }
    }

    public void addReport() {
        weeklyEntriesDs.addItem(new WeeklyReportEntry());
    }

    public void submitAll() {

    }

    protected List<Task> getAssignedTasks(Project project) {
        List<Task> tasks = lookupFieldsOptionsLists.get(project);
        if (tasks == null) {
            LoadContext loadContext = new LoadContext(Task.class)
                    .setView("task-full");
            loadContext.setQueryString("select e from ts$Task e join e.participants p where p.user.id = :userId and e.project.id = :projectId and e.status = 10 order by e.project")
                    .setParameter("projectId", project.getId())
                    .setParameter("userId", userSession.getUser().getId());
            tasks = dataManager.loadList(loadContext);
            lookupFieldsOptionsLists.put(project, tasks);
        }
        return tasks;
    }
}