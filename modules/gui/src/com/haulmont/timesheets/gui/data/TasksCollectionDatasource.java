/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.data;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.service.ProjectsService;

import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 * @version $Id$
 */
public class TasksCollectionDatasource extends CollectionDatasourceImpl<Task, UUID> {

    protected UserSession userSession = AppBeans.get(UserSession.class);
    protected ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);

    @Override
    protected void loadData(Map<String, Object> params) {

        for (Task task : projectsService.getActiveTasksForUser(userSession.getUser())) {
            data.put(task.getId(), task);
            attachListener(task);
        }
    }
}
