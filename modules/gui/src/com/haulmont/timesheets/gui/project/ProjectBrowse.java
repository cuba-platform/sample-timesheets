/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.TreeTable;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.gui.ComponentsHelper;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

/**
 * @author gorelov
 */
public class ProjectBrowse extends AbstractLookup {
    @Inject
    protected TreeTable projectsTable;
    @Inject
    protected Table tasksTable;
    @Inject
    protected Table participantsTable;

    @Named("participantsTable.create")
    protected CreateAction participantsTableCreate;

    @Named("projectsTable.create")
    protected CreateAction projectsTableCreate;

    @Named("projectsTable.edit")
    protected EditAction projectsTableEdit;

    @Inject
    protected UserSessionSource userSessionSource;

    @Override
    public void init(Map<String, Object> params) {
        User user = userSessionSource.getUserSession().getUser();
        if (CollectionUtils.isEmpty(user.getUserRoles())) {
            params.put("superuser", true);
        }

        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRole().getType() == RoleType.SUPER) {
                params.put("superuser", true);
                break;
            }
        }

        initProjectsTable();

        initTasksTable();

        initParticipantsTable();
    }

    private void initParticipantsTable() {
        participantsTable.addAction(new CreateAction(participantsTable) {
            @Override
            public WindowManager.OpenType getOpenType() {
                return WindowManager.OpenType.DIALOG;
            }

            @Override
            public Map<String, Object> getInitialValues() {
                return ParamsMap.of("project", projectsTable.getSingleSelected());
            }
        });
        participantsTableCreate.setOpenType(WindowManager.OpenType.DIALOG);

        ComponentsHelper.addRemoveColumn(participantsTable, "remove");
    }

    private void initTasksTable() {
        tasksTable.addAction(new CreateAction(tasksTable){
            @Override
            public Map<String, Object> getInitialValues() {
                return ParamsMap.of("project", projectsTable.getSingleSelected());
            }
        });
        tasksTable.addAction(new ComponentsHelper.TaskStatusTrackingAction(tasksTable, "switchStatus"));

        tasksTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, @Nullable String property) {
                if ("status".equals(property)) {
                    Task task = (Task) entity;
                    return ComponentsHelper.getTaskStatusStyle(task);
                }
                return null;
            }
        });
    }

    private void initProjectsTable() {
        projectsTable.addAction(new CreateAction(projectsTable) {
            @Override
            public WindowManager.OpenType getOpenType() {
                return WindowManager.OpenType.DIALOG;
            }
        });
        projectsTable.addAction(new EditAction(tasksTable) {
            @Override
            public WindowManager.OpenType getOpenType() {
                return WindowManager.OpenType.DIALOG;
            }

            @Override
            protected void afterCommit(Entity entity) {
                projectsTable.refresh();
            }
        });

        projectsTable.setStyleProvider(new Table.StyleProvider() {
            @Nullable
            @Override
            public String getStyleName(Entity entity, String property) {
                if ("status".equals(property)) {
                    Project project = (Project) entity;
                    return ComponentsHelper.getProjectStatusStyle(project);
                }
                return null;
            }
        });
    }

    @Override
    public void ready() {
        projectsTable.expandAll();
    }
}