/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.chile.core.model.MetaPropertyPath;
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

    <T extends Entity> MetaPropertyPath getEntityMetaPropertyPath(Class<T> clazz, String property);

    List<TimeEntry> getTimeEntriesForPeriod(
            Date start, Date end, User user, @Nullable TimeEntryStatus status, @Nullable String viewName);

    List<TimeEntry> getApprovableTimeEntriesForPeriod(
            Date start, Date end, User approver, User user, @Nullable TimeEntryStatus status, @Nullable String viewName);

    List<Holiday> getHolidaysForPeriod(Date start, Date end);

    List<Task> getActiveTasksForUser(User user, @Nullable String viewName);

    Map<String, Task> getActiveTasksForUserAndProject(User user, Project project, @Nullable String viewName);

    List<ActivityType> getActivityTypesForProject(Project project, @Nullable String viewName);

    List<Project> getActiveProjectsForUser(User user, @Nullable String viewName);

    List<Project> getActiveManagedProjectsForUser(User user, @Nullable String viewName);

    boolean assignUsersToProjects(Collection<User> users, Collection<Project> projects, ProjectRole projectRole);

    List<Tag> getTagsForTheProject(@Nullable Project project, @Nullable String viewName);

    List<Tag> getTagsWithTheTagType(TagType type, @Nullable String viewName);

    List<ProjectParticipant> getProjectParticipants(Project project, @Nullable String viewName);

    List<User> getProjectUsers(Project project, @Nullable String viewName);

    List<User> getManagedUsers(User manager, String viewName);
}