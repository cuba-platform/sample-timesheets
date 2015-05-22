/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectRole;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
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
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    private PopupButton assignBtn;

    @Inject
    private ProjectsService projectsService;
    @Named("participantsTable.create")
    protected CreateAction participantsTableCreate;
    @Named("participantsTable.edit")
    protected EditAction participantsTableEdit;

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
        participantsTableEdit.setOpenType(WindowManager.OpenType.DIALOG);
    }

    private void initTasksTable() {
        tasksTable.addAction(new CreateAction(tasksTable) {
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

        LoadContext loadContext = new LoadContext(ProjectRole.class);
        loadContext.setQueryString("select pr from ts$ProjectRole pr order by pr.name");
        List<ProjectRole> projectRoles = getDsContext().getDataSupplier().loadList(loadContext);
        for (final ProjectRole projectRole : projectRoles) {
            assignBtn.addAction(new AbstractAction("assign" + projectRole.getCode()) {
                @Override
                public String getCaption() {
                    return (getMessage("caption.assign" + StringUtils.capitalize(projectRole.getCode().toLowerCase())));
                }

                @Override
                public void actionPerform(Component component) {
                    openLookup("sec$User.lookup", new Handler() {
                        @Override
                        public void handleLookup(Collection items) {
                            if (CollectionUtils.isNotEmpty(items)) {
                                Collection<Project> selectedProjects = (Collection) projectsTable.getSelected();
                                Collection<User> selectedUsers = (Collection) items;
                                boolean needToRefresh =
                                        projectsService.assignUsersToProjects(selectedUsers, selectedProjects, projectRole);
                                if (needToRefresh) {
                                    participantsTable.refresh();
                                }
                            }
                        }
                    }, WindowManager.OpenType.THIS_TAB);
                }
            });
        }

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