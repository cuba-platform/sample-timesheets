/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.data;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.service.ProjectsService;

import java.util.List;
import java.util.function.Function;

public class TasksCollectionLoadDelegate implements Function<LoadContext<Task>, List<Task>> {

    @Override
    public List<Task> apply(LoadContext<Task> taskLoadContext) {
        UserSessionSource source = AppBeans.get(UserSessionSource.NAME);
        ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
        return projectsService.getActiveTasksForUser(source.getUserSession().getCurrentOrSubstitutedUser(), "task-full");
    }
}
