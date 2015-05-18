/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gorelov
 */
public interface ProjectsService {
    String NAME = "ts_ProjectsService";

    @Nonnull
    List<Project> getChildren(Project parent);

    void setClient(@Nonnull Project project, @Nullable Client client);

    @Nullable
    ProjectRole getUserProjectRole(@Nonnull Project project, @Nonnull User user);

    @Nullable
    ProjectRole getRoleByCode(String code);

    @Nonnull
    List<TimeEntry> getTimeEntriesForPeriod(@Nonnull Date start, @Nonnull Date end, @Nonnull User user);

    @Nonnull
    List<TimeEntry> getTimeEntriesForUser(@Nonnull User user);

    @Nonnull
    List<Holiday> getHolidays();

    void removeTimeEntry(TimeEntry timeEntry);

    void removeTimeEntries(List<TimeEntry> timeEntries);

    @Nonnull
    Map<String, Object> getAssignedTasks(@Nonnull Project project, @Nonnull User user);
}