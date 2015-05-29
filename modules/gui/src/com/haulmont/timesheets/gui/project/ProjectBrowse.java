/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */
package com.haulmont.timesheets.gui.project;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.Project;
import com.haulmont.timesheets.entity.ProjectParticipant;
import com.haulmont.timesheets.entity.ProjectRole;
import com.haulmont.timesheets.entity.Task;
import com.haulmont.timesheets.gui.ComponentsHelper;
import com.haulmont.timesheets.gui.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

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
    protected PopupButton assignBtn;
    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected ProjectsService projectsService;

    @Named("participantsTable.create")
    protected CreateAction participantsTableCreate;
    @Named("participantsTable.edit")
    protected EditAction participantsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        if (securityAssistant.isSuperUser()) {
            params.put("superuser", true);
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
        participantsTable.addAction(new ItemTrackingAction("copy") {
            @Override
            public String getCaption() {
                return getMessage("caption.copy");
            }

            @Override
            public void actionPerform(Component component) {
                copyParticipants();
            }

            @Override
            protected boolean isApplicable() {
                return projectsTable != null && !projectsTable.getSelected().isEmpty();
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
                    }, WindowManager.OpenType.DIALOG);
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

    public void copyParticipants() {
        final Project project = projectsTable.getSingleSelected();
        if (project != null) {
            openLookup("ts$Project.lookup", new Handler() {
                @Override
                public void handleLookup(Collection items) {
                    if (CollectionUtils.isNotEmpty(items)) {
                        CommitContext commitContext = new CommitContext();
                        for (Project selected : (Collection<Project>) items) {
                            commitContext.getCommitInstances().addAll(updateParticipants(
                                    projectsService.getProjectParticipants(selected, "projectParticipant-full"), project));
                        }
                        getDsContext().getDataSupplier().commit(commitContext);
                        participantsTable.refresh();
                    }
                }
            }, WindowManager.OpenType.DIALOG, ParamsMap.of("exclude", project));
        }
    }

    protected Collection<? extends Entity> updateParticipants(List<ProjectParticipant> participants, Project project) {
        if (participants.isEmpty()) {
            return Collections.emptyList();
        }
        List<ProjectParticipant> copies = new ArrayList<>(participants.size());
        List<User> assignedUsers = projectsService.getProjectUsers(project, View.MINIMAL);
        for (ProjectParticipant existParticipant : participants) {
            if (!assignedUsers.contains(existParticipant.getUser())) {
                ProjectParticipant participant = new ProjectParticipant();
                participant.setUser(existParticipant.getUser());
                participant.setRole(existParticipant.getRole());
                participant.setProject(project);

                copies.add(participant);
            }
        }
        return copies;
    }

    @Override
    public void ready() {
        projectsTable.expandAll();
    }
}