/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectRole;
import com.haulmont.timesheets.entity.TimeEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

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
}