/*
 * Copyright (c) 2016 Haulmont
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.timesheets.gui.project;

import com.google.common.collect.ImmutableMap;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.timesheets.entity.*;
import com.haulmont.timesheets.gui.util.ComponentsHelper;
import com.haulmont.timesheets.gui.util.SecurityAssistant;
import com.haulmont.timesheets.service.ProjectsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * @author gorelov
 */
@SuppressWarnings("WeakerAccess")
public class ProjectBrowse extends AbstractLookup {
    @Inject
    protected TreeTable<Project> projectsTable;
    @Inject
    protected Table<Task> tasksTable;
    @Inject
    protected Table<ProjectParticipant> participantsTable;
    @Inject
    protected PopupButton assignBtn;
    @Inject
    protected SecurityAssistant securityAssistant;
    @Inject
    protected ProjectsService projectsService;
    @Inject
    protected HierarchicalDatasource<Project, UUID> projectsDs;
    @Named("participantsTable.create")
    protected CreateAction participantsTableCreate;
    @Named("participantsTable.edit")
    protected EditAction participantsTableEdit;
    @Inject
    protected Metadata metadata;

    @Override
    public void init(Map<String, Object> params) {
        if (securityAssistant.isSuperUser()) {
            projectsDs.setQuery("select e from ts$Project e");
        }

        initProjectsTable();

        initTasksTable();

        initParticipantsTable();
    }

    private void initParticipantsTable() {
        participantsTable.addAction(new CreateAction(participantsTable) {
            @Override
            public String getCaption() {
                return getMessage("caption.createParticipant");
            }

            @Override
            public WindowManager.OpenType getOpenType() {
                return WindowManager.OpenType.DIALOG;
            }

            @Override
            public Map<String, Object> getInitialValues() {
                return ParamsMap.of("project", projectsTable.getSingleSelected());
            }

            @Override
            public void actionPerform(Component component) {
                if (projectsTable.getSingleSelected() != null) {
                    super.actionPerform(component);
                } else {
                    showNotification(getMessage("notification.pleaseSelectProject"), NotificationType.HUMANIZED);
                }
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

            @Override
            public void actionPerform(Component component) {
                if (projectsTable.getSingleSelected() != null) {
                    super.actionPerform(component);
                } else {
                    showNotification(getMessage("notification.pleaseSelectProject"), NotificationType.HUMANIZED);
                }
            }
        });
        tasksTable.addAction(new ComponentsHelper.TaskStatusTrackingAction(tasksTable, "switchStatus"));

        tasksTable.setStyleProvider(new Table.StyleProvider<Task>() {
            @Nullable
            @Override
            public String getStyleName(Task entity, @Nullable String property) {
                if ("status".equals(property)) {
                    return ComponentsHelper.getTaskStatusStyle(entity);
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

        LoadContext<ProjectRole> loadContext = new LoadContext<>(ProjectRole.class);
        loadContext.setQueryString("select pr from ts$ProjectRole pr order by pr.name");
        List<ProjectRole> projectRoles = getDsContext().getDataSupplier().loadList(loadContext);
        projectRoles = new ArrayList<>(projectRoles);
        sortProjectRoles(projectRoles);
        for (final ProjectRole projectRole : projectRoles) {
            assignBtn.addAction(new AbstractAction("assign" + projectRole.getCode()) {
                @Override
                public String getCaption() {
                    return (getMessage("caption.assign" + StringUtils.capitalize(projectRole.getCode().getId().toLowerCase())));
                }

                @SuppressWarnings("unchecked")
                @Override
                public void actionPerform(Component component) {
                    if (CollectionUtils.isNotEmpty(projectsTable.getSelected())) {
                        openLookup("sec$User.lookup", items -> {
                            if (CollectionUtils.isNotEmpty(items)) {
                                Collection<Project> selectedProjects = projectsTable.getSelected();
                                Collection<User> selectedUsers = (Collection) items;
                                boolean needToRefresh =
                                        projectsService.assignUsersToProjects(selectedUsers, selectedProjects, projectRole);
                                if (needToRefresh) {
                                    participantsTable.refresh();
                                }
                            }
                        }, WindowManager.OpenType.DIALOG);
                    } else {
                        showNotification(getMessage("notification.pleaseSelectProject"), NotificationType.HUMANIZED);
                    }
                }
            });
        }

        projectsTable.setStyleProvider(new Table.StyleProvider<Project>() {
            @Nullable
            @Override
            public String getStyleName(Project entity, @Nullable String property) {
                if ("status".equals(property)) {
                    return ComponentsHelper.getProjectStatusStyle(entity);
                }
                return null;
            }
        });
    }

    protected void sortProjectRoles(List<ProjectRole> projectRoles) {
        Collections.sort(projectRoles, new Comparator<ProjectRole>() {
            private Map<ProjectRoleCode, Integer> order = ImmutableMap.<ProjectRoleCode, Integer>builder()
                    .put(ProjectRoleCode.WORKER, 1)
                    .put(ProjectRoleCode.MANAGER, 2)
                    .put(ProjectRoleCode.APPROVER, 3)
                    .put(ProjectRoleCode.OBSERVER, 4).build();

            @Override
            public int compare(ProjectRole o1, ProjectRole o2) {
                ProjectRoleCode code1 = o1.getCode();
                ProjectRoleCode code2 = o2.getCode();

                Integer order1 = order.get(code1);
                Integer order2 = order.get(code2);

                return ObjectUtils.compare(order1, order2);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void copyParticipants() {
        final Project project = projectsTable.getSingleSelected();
        if (project != null) {
            openLookup("ts$Project.lookup", items -> {
                if (CollectionUtils.isNotEmpty(items)) {
                    CommitContext commitContext = new CommitContext();
                    for (Project selected : (Collection<Project>) items) {
                        commitContext.getCommitInstances().addAll(updateParticipants(
                                projectsService.getProjectParticipants(selected, "projectParticipant-full"), project));
                    }
                    getDsContext().getDataSupplier().commit(commitContext);
                    participantsTable.refresh();
                }
            }, WindowManager.OpenType.DIALOG, ParamsMap.of("exclude", project));
        } else {
            showNotification(getMessage("notification.pleaseSelectProject"), NotificationType.HUMANIZED);
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
                ProjectParticipant participant = metadata.create(ProjectParticipant.class);
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