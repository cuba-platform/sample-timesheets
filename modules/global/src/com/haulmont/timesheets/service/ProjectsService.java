/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.*;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gorelov
 */
public interface ProjectsService {
    String NAME = "ts_ProjectsService";

    List<Project> getProjectChildren(Project parent);

    @Nullable
    ProjectRole getUserProjectRole(Project project, User user);

    @Nullable
    <T extends Entity> T getEntityByCode(Class<T> clazz, String code, @Nullable String viewName);

    List<TimeEntry> getTimeEntriesForPeriod(
            Date start, Date end, User user, @Nullable TimeEntryStatus status, @Nullable String viewName);

    List<TimeEntry> getApprovableTimeEntriesForPeriod(
            Date start, Date end, User approver, User user, @Nullable TimeEntryStatus status, @Nullable String viewName);

    List<TimeEntry> getTimeEntriesForUser(User user, @Nullable String viewName);

    List<Holiday> getHolidays();

    List<Task> getActiveTasksForUser(User user, @Nullable String viewName);

    Map<String, Task> getActiveTasksForUserAndProject(User user, Project project, @Nullable String viewName);

    List<Project> getActiveProjectsForUser(User user, @Nullable String viewName);

    List<Project> getActiveManagedProjectsForUser(User user, @Nullable String viewName);

    boolean assignUsersToProjects(Collection<User> users, Collection<Project> projects, ProjectRole projectRole);
}