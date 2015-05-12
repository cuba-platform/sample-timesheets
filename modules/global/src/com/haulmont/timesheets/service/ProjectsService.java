/*
 * Copyright (c) 2015 com.haulmont.ts.service
 */
package com.haulmont.timesheets.service;

import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Client;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectRole;
import com.haulmont.timesheets.entity.Task;

import java.util.List;

/**
 * @author gorelov
 */
public interface ProjectsService {
    String NAME = "ts_ProjectsService";

    List<Project> getChildren(Project parent);

    void setClient(Project project, Client client);

    ProjectRole getUserProjectRole(Project project, User user);

    ProjectRole getRoleByName(String name);

    void updateTask(Task task);
}