/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.global;

import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.entity.TimeEntry;
import com.haulmont.timesheets.entity.WeeklyReportEntry;

import javax.annotation.ManagedBean;
import java.util.*;

/**
 * @author gorelov
 * @version $Id$
 */
@ManagedBean
public class WeeklyReportConverter {

    public static final String NAME = "timesheets_WeeklyReportConverterBean";

    public List<WeeklyReportEntry> convertFromTimeEtnries(List<TimeEntry> timeEntries) {

        if (timeEntries.isEmpty()) {
            return Collections.emptyList();
        }

        final Map<Project, Map<Task, List<TimeEntry>>> timeEntriesForWeekMap = new HashMap<>();

        for (TimeEntry timeEntry : timeEntries) {
            addTimeEntryToMap(timeEntriesForWeekMap, timeEntry);
        }

        if (timeEntriesForWeekMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<WeeklyReportEntry> reportEntries = new ArrayList<>();
        for (Map.Entry<Project, Map<Task, List<TimeEntry>>> projectEntry : timeEntriesForWeekMap.entrySet()) {
            for (Map.Entry<Task, List<TimeEntry>> taskEntry : projectEntry.getValue().entrySet()) {
                WeeklyReportEntry reportEntry = new WeeklyReportEntry();
                reportEntry.setProject(projectEntry.getKey());
                reportEntry.setTask(taskEntry.getKey());
                for (TimeEntry timeEntry : taskEntry.getValue()) {
                    reportEntry.updateTimeEntry(timeEntry);
                }
                reportEntries.add(reportEntry);
            }
        }
        return reportEntries;
    }

    protected void addTimeEntryToMap(Map<Project, Map<Task, List<TimeEntry>>> timeEntriesForWeekMap, TimeEntry timeEntry) {
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
}
