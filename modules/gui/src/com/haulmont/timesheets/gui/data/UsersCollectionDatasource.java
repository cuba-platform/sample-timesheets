/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.gui.data;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.gui.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;

import java.util.Map;
import java.util.UUID;

/**
 * @author gorelov
 * @version $Id$
 */
public class UsersCollectionDatasource extends CollectionDatasourceImpl<User, UUID> {

    @Override
    protected void loadData(Map<String, Object> params) {
        SecurityAssistant securityAssistant = AppBeans.get(SecurityAssistant.NAME);
        if (securityAssistant.isSuperUser() || securityAssistant.isUserCloser()) {
            super.loadData(params);
        } else {
            detachListener(data.values());
            data.clear();

            ProjectsService projectsService = AppBeans.get(ProjectsService.NAME);
            for (User task : projectsService.getManagedUsersForUser(userSession.getCurrentOrSubstitutedUser(), View.LOCAL)) {
                data.put(task.getId(), task);
                attachListener(task);
            }
        }
    }
}
