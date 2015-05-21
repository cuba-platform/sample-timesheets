/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.*;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gorelov
 */
public interface ProjectsService {
    String NAME = "ts_ProjectsService";

    List<Project> getChildren(Project parent);

    void setClient(Project project, @Nullable Client client);

    @Nullable
    ProjectRole getUserProjectRole(Project project, User user);

    @Nullable
    <T extends Entity> T getEntityByCode(Class<T> clazz, String code, @Nullable String viewName);

    List<TimeEntry> getTimeEntriesForPeriod(Date start, Date end, User user);

    List<TimeEntry> getTimeEntriesForUser(User user);

    List<Holiday> getHolidays();

    void removeTimeEntry(TimeEntry timeEntry);

    void removeTimeEntries(List<TimeEntry> timeEntries);

    List<Task> getActiveTasksForUser(User user);

    Map<String, Task> getActiveTasksForUserAndProject(User user, Project project);

    List<Project> getActiveProjectsForUser(User user);
}